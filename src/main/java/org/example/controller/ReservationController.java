package org.example.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.MainApplication;
import org.example.dao.EvenementDAO;
import org.example.exception.PlacesInsuffisantesException;
import org.example.model.*;
import org.example.service.PaiementService;
import org.example.service.ReservationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Contrôleur JavaFX responsable de l'écran de réservation.
 * Il permet à un client connecté de choisir un événement, une catégorie
 * de places puis de lancer le flux de réservation et de paiement.
 */
public class ReservationController implements Reservable {

    @FXML private ComboBox<Evenement> eventsCombo;
    @FXML private ComboBox<Categorie> categoriesCombo;
    @FXML private Spinner<Integer> qtySpinner;
    @FXML private Button reserveButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private Client client;
    private final EvenementDAO evenementDAO = new EvenementDAO();
    private final ReservationService reservationService = new ReservationService();

    /**
     * Initialise les composants graphiques après le chargement du FXML.
     * Configure les convertisseurs, charge les événements et branche les actions.
     */
    @FXML
    public void initialize() {
        qtySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));

        // converter pour afficher le nom de l'événement
        eventsCombo.setCellFactory(cb -> new ListCell<>(){
            @Override protected void updateItem(Evenement item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });
        eventsCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Evenement object) { return object == null ? "" : object.getNom(); }
            @Override public Evenement fromString(String string) { return null; }
        });

        categoriesCombo.setCellFactory(cb -> new ListCell<>(){
            @Override protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNomCategorie());
            }
        });
        categoriesCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Categorie object) { return object == null ? "" : object.getNomCategorie(); }
            @Override public Categorie fromString(String string) { return null; }
        });

        // load events
        try { loadEvents(); } catch (SQLException e) { statusLabel.setText("Erreur chargement événements: " + e.getMessage()); }

        eventsCombo.setOnAction(e -> onEventSelected());
        reserveButton.setOnAction(e -> onReserve()) ;
        //historyButton.setOnAction(e -> openHistoryWindow());
    }

    /**
     * Injecte le client actuellement connecté pour sécuriser les opérations.
     * @param client client authentifié
     */
    public void setClient(Client client) { this.client = client; }

    /**
     * Pré‑sélectionne un événement (cas où le client vient depuis
     * la page de consultation des événements).
     * @param evenement événement déjà choisi dans une autre vue
     */
    public void preselectEvent(Evenement evenement) {
        if (evenement == null) {
            return;
        }
        // s'assure que la liste est chargée puis sélectionne l'événement correspondant
        if (eventsCombo.getItems() != null && !eventsCombo.getItems().isEmpty()) {
            eventsCombo.getSelectionModel().select(evenement);
            onEventSelected();
        }
    }

    /**
     * Charge la liste des événements pour alimenter la combo.
     */
    private void loadEvents() throws SQLException {
        List<Evenement> events = evenementDAO.listAll();
        eventsCombo.setItems(FXCollections.observableArrayList(events));
    }

    /**
     * Réagit au choix d'un événement pour afficher les catégories associées.
     */
    private void onEventSelected() {
        Evenement ev = eventsCombo.getValue();
        categoriesCombo.getItems().clear();
        if (ev == null) return;
        try {
            List<Categorie> cats = evenementDAO.listCategoriesForEvent(ev.getIdEvenement());
            categoriesCombo.setItems(FXCollections.observableArrayList(cats));
            if (!cats.isEmpty()) categoriesCombo.getSelectionModel().selectFirst();
        } catch (SQLException ex) {
            statusLabel.setText("Erreur chargement catégories: " + ex.getMessage());
        }
    }

    /**
     * Lance la réservation : validations, appel du service puis ouverture du paiement.
     */
    public void onReserve() {
        if (client == null) { statusLabel.setText("Client non identifié. Connectez-vous."); return; }
        Evenement ev = eventsCombo.getValue();
        Categorie cat = categoriesCombo.getValue();
        int qty = qtySpinner.getValue();
        if (ev == null || cat == null) { statusLabel.setText("Sélectionnez un événement et une catégorie."); return; }

        try {
            Reservation newReservation = reservationService.reserver(client, ev, cat.getIdCategorie(), qty);

            /*
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Réservation effectuée.", ButtonType.OK);
            a.showAndWait();
            statusLabel.setText("Réservation réussie.");
            */

            // On ouvre la view pour le paiement
            PaiementService paiementService = new PaiementService();
            paiementService.openPaymentView(newReservation);

        } catch (PlacesInsuffisantesException ex) {
            statusLabel.setText("Pas assez de places disponibles.");
            new Alert(Alert.AlertType.WARNING, ex.getMessage(), ButtonType.OK).showAndWait();
        } catch (Exception ex) {
            statusLabel.setText("Erreur lors de la réservation: " + ex.getMessage());
            new Alert(Alert.AlertType.ERROR, "Erreur: " + ex.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    /**
     * Retourne sur l'écran de consultation des événements.
     */
    @FXML
    private void onBack() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/evenements-view.fxml"));
            Parent root = loader.load();

            Object ctrl = loader.getController();
            if (ctrl instanceof EvenementController) {
                ((EvenementController) ctrl).initData(client);
            }

            Stage stage = (Stage) eventsCombo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Événements");
            stage.centerOnScreen();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Impossible de revenir aux événements: " + e.getMessage(), ButtonType.OK).showAndWait();
        }
    }

}
