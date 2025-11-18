package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.exception.PlacesInsuffisantesException;
import org.example.model.CategoriePlace;
import org.example.model.Client;
import org.example.model.Evenement;
import org.example.service.EvenementService;
import org.example.service.ReservationService;

import javafx.util.StringConverter;

public class ReservationController {

    @FXML
    private ComboBox<Evenement> eventCombo;

    @FXML
    private ComboBox<CategoriePlace> categoryCombo;

    @FXML
    private TextField nbField;

    @FXML
    private Button reserveButton;

    @FXML
    private Label messageLabel;
    @FXML
    private Button historyButton;

    private Client client;

    private final EvenementService evenementService = new EvenementService();
    private final ReservationService reservationService = new ReservationService();

    @FXML
    public void initialize() {
        ObservableList<Evenement> events = FXCollections.observableArrayList(evenementService.getAllEvents());
        eventCombo.setItems(events);

        // Affichage lisible des événements
        eventCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Evenement object) {
                return object == null ? "" : object.getNom() + " (" + object.getDateEvent() + ")";
            }

            @Override
            public Evenement fromString(String string) {
                return null;
            }
        });

        categoryCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(CategoriePlace object) {
                return object == null ? "" : object.getNomCategorie() + " - " + object.getPlacesRestantes() + " places - " + object.getPrix() + "€";
            }

            @Override
            public CategoriePlace fromString(String string) {
                return null;
            }
        });

        eventCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                ObservableList<CategoriePlace> cats = FXCollections.observableArrayList(newV.getCategories());
                categoryCombo.setItems(cats);
                categoryCombo.getSelectionModel().selectFirst();
            }
        });

        reserveButton.setOnAction(e -> doReserve());
        historyButton.setOnAction(e -> openHistory());
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private void openHistory() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/history-view.fxml"));
            javafx.scene.Parent root = loader.load();
            org.example.controller.HistoryController controller = loader.getController();
            controller.setClient(client);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Historique des réservations");
            stage.setScene(new javafx.scene.Scene(root, 800, 400));
            stage.show();
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Impossible d'ouvrir l'historique: " + e.getMessage());
        }
    }

    private void doReserve() {
        Evenement event = eventCombo.getValue();
        CategoriePlace cat = categoryCombo.getValue();
        String nbText = nbField.getText();

        messageLabel.setStyle("-fx-text-fill: black;");

        if (event == null || cat == null) {
            messageLabel.setText("Veuillez sélectionner un événement et une catégorie.");
            return;
        }

        int nb;
        try {
            nb = Integer.parseInt(nbText);
            if (nb <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            messageLabel.setText("Veuillez entrer un nombre de tickets valide (>0).");
            return;
        }

        try {
            var reservation = reservationService.reserver(client, event, cat.getIdCategorie(), nb);
            client.ajouterReservation(reservation);

            // refresh de la liste des catégories pour afficher le nouveau nombre de places
            ObservableList<CategoriePlace> cats = FXCollections.observableArrayList(event.getCategories());
            categoryCombo.setItems(cats);
            // re-sélectionne la catégorie courante si elle existe
            for (CategoriePlace cp : cats) {
                if (cp.getIdCategorie() == cat.getIdCategorie()) {
                    categoryCombo.getSelectionModel().select(cp);
                    break;
                }
            }

            nbField.clear();
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Réservation réussie (id=" + reservation.getIdReservation() + ")");
        } catch (PlacesInsuffisantesException pie) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Erreur : " + pie.getMessage());
        } catch (Exception ex) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Erreur inattendue : " + ex.getMessage());
        }
    }
}
