package org.example.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.example.dao.ReservationDAO;
import org.example.model.Client;
import org.example.model.ReservationSummary;
import org.example.service.ReservationService;
import org.example.util.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Contrôleur gérant l'historique des réservations du client.
 * Affiche un tableau synthétique et offre l'action d'annulation.
 */
public class HistoryController {

    @FXML private TableView<ReservationSummary> table;
    @FXML private TableColumn<ReservationSummary, String> colDate;
    @FXML private TableColumn<ReservationSummary, String> colEvent;
    @FXML private TableColumn<ReservationSummary, String> colEventDate;
    @FXML private TableColumn<ReservationSummary, Integer> colQty;
    @FXML private TableColumn<ReservationSummary, Double> colTotal;
    @FXML private TableColumn<ReservationSummary, Void> colAction;
    @FXML private Label statusLabel;

    private Client client;
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final ReservationService reservationService = new ReservationService();
    private Parent previousRoot;
    @FXML private Button backButton;

    /**
     * Configure les colonnes du tableau après chargement FXML.
     */
    @FXML
    public void initialize() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        colDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getReservationDate().format(dtf)));
        colEvent.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEventName()));
        colEventDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEventDate().format(dtf)));
        colQty.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getQuantity()));
        colTotal.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getTotal()));

        // action column : bouton annuler
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Annuler");
            {
                btn.setOnAction(e -> {
                    ReservationSummary rs = getTableView().getItems().get(getIndex());
                    onCancel(rs);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    /**
     * Stocke la racine précédente pour permettre un retour simple.
     * @param previousRoot racine de la scène à restaurer
     */
    public void setPreviousRoot(Parent previousRoot) {
        this.previousRoot = previousRoot;
        if (backButton != null) {
            backButton.setOnAction(e -> {
                // replace the current scene root with previousRoot
                try {
                    backButton.getScene().setRoot(previousRoot);
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Impossible de retourner: " + ex.getMessage(), ButtonType.OK).showAndWait();
                }
            });
        }
    }

    /**
     * Définit le client courant et charge son historique.
     * @param client client authentifié
     */
    public void setClient(Client client) {
        this.client = client;
        loadData();
    }

    /**
     * Charge les réservations du client et les injecte dans le tableau.
     */
    private void loadData() {
        if (client == null) return;
        try (Connection conn = Database.getConnection()) {
            List<ReservationSummary> list = reservationDAO.listByClient(conn, client.getIdUser());
            table.setItems(FXCollections.observableArrayList(list));
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Erreur chargement historique: " + e.getMessage());
        }
    }

    /**
     * Déclenche l'annulation de la réservation sélectionnée.
     * @param rs résumé de réservation affiché dans la table
     */
    private void onCancel(ReservationSummary rs) {
        if (client == null) { statusLabel.setText("Client non connecté."); return; }
        try {
            reservationService.annulerReservation(rs.getReservationId(), client);
            new Alert(Alert.AlertType.INFORMATION, "Réservation annulée.", ButtonType.OK).showAndWait();
            loadData();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur annulation: " + e.getMessage(), ButtonType.OK).showAndWait();
            statusLabel.setText("Erreur annulation: " + e.getMessage());
        }
    }
}
