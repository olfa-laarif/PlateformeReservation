package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.model.Client;
import org.example.model.ReservationRecord;
import org.example.service.ReservationService;

import java.sql.Connection;
import java.sql.SQLException;
import org.example.util.Database;
import org.example.dao.ReservationDAO;

public class HistoryController {

    @FXML private TableView<ReservationRecord> table;
    @FXML private TableColumn<ReservationRecord, String> colEvent;
    @FXML private TableColumn<ReservationRecord, Object> colEventDate;
    @FXML private TableColumn<ReservationRecord, String> colCategory;
    @FXML private TableColumn<ReservationRecord, Integer> colQuantity;
    @FXML private TableColumn<ReservationRecord, Double> colTotal;
    @FXML private TableColumn<ReservationRecord, Void> colAction;
    @FXML private Button closeButton;

    private Client client;
    private final ReservationService reservationService = new ReservationService();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    public void setClient(Client client) {
        this.client = client;
        loadData();
    }

    @FXML
    public void initialize() {
        colEvent.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        colEventDate.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());

        // add action column
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Annuler");
            {
                btn.setOnAction(e -> {
                    ReservationRecord rec = getTableView().getItems().get(getIndex());
                    cancelReservation(rec.getReservationId());
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });
    }

    private void loadData() {
        try (Connection conn = Database.getConnection()) {
            java.util.List<ReservationRecord> list = reservationDAO.listByClient(conn, client.getIdUser());
            ObservableList<ReservationRecord> obs = FXCollections.observableArrayList(list);
            table.setItems(obs);
        } catch (SQLException e) {
            showAlert("Erreur DB", e.getMessage());
        }
    }

    private void cancelReservation(int reservationId) {
        try {
            reservationService.annulerReservation(reservationId, client);
            showAlert("Succès", "Réservation annulée.");
            loadData();
        } catch (Exception e) {
            showAlert("Impossible d'annuler", e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(title);
        a.showAndWait();
    }
}
