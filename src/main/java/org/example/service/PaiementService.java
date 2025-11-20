package org.example.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.example.controller.PaymentController;
import org.example.model.Reservation;

import java.io.IOException;
import java.net.URL;

public class PaiementService {

    /**
     * Ouvre une nouvelle fenêtre modale pour la vue de paiement.
     * <p>
     * Cette version de la méthode est principalement utilisée à des fins de test ou de démonstration,
     * car elle initialise le contrôleur de paiement avec des données statiques (ID 42, Montant 99.99).
     * Elle charge le FXML spécifié et définit un titre pour la nouvelle fenêtre.
     *
     * @throws IOException Si le fichier FXML de la vue de paiement n'est pas trouvé ou ne peut être chargé.
     */
    public void openPaymentView() {
        try {
            // Chargement de la view de paiement
            FXMLLoader loader = loadNewView("/views/payment-view.fxml", "Paiement de la Réservation");

            // Obtention du controleur pour la transmition des données
            PaymentController paymentController = loader.getController();

            // Initialisation des données de la view paiement
            paymentController.initialiserDonnees(42, 99.99);

        } catch (IOException e) {
            System.err.println("Erreur de chargement de la vue de paiement.");
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de paiement.", ButtonType.OK);
            alert.showAndWait();
        }
    }


    /**
     * Ouvre une nouvelle fenêtre modale pour la vue de paiement et y transmet l'objet {@code Reservation}
     * complet pour l'initialisation.
     * <p>
     * C'est le point d'entrée standard pour lancer le processus de paiement après qu'une réservation
     * a été créée ou sélectionnée. Elle charge le FXML et initialise le {@code PaymentController}
     * avec toutes les données nécessaires de la réservation.
     *
     * @param reservation L'objet Reservation complet à payer.
     * @throws IOException Si le fichier FXML de la vue de paiement n'est pas trouvé ou ne peut être chargé.
     */
    public void openPaymentView(Reservation reservation) {
        try {
            // Chargement de la view de paiement
            FXMLLoader loader = loadNewView("/views/payment-view.fxml", "Paiement de la Réservation");

            // Obtention du controleur pour la transmition des données
            PaymentController paymentController = loader.getController();
            // Initialisation des données de la view paiement
            //paymentController.initialiserDonnees(reservation.getIdReservation(), reservation.calculateTotalPrice());
            paymentController.initialiserDonnees(reservation);

        } catch (IOException e) {
            System.err.println("Erreur de chargement de la vue de paiement.");
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de paiement.", ButtonType.OK);
            alert.showAndWait();
        }
    }


    /**
     * Méthode utilitaire générique pour charger un fichier FXML, créer une nouvelle scène et l'afficher
     * dans un nouveau {@code Stage} (fenêtre).
     * <p>
     * Cette méthode est conçue pour être réutilisée par d'autres fonctions d'ouverture de vues
     * et gère le processus standard de chargement JavaFX. Le nouveau stage est affiché immédiatement.
     *
     * @param fxmlPath Le chemin d'accès relatif au fichier FXML à charger (ex: "/views/payment-view.fxml").
     * @param title Le titre à afficher dans la barre de titre de la nouvelle fenêtre (Stage).
     * @return Le {@code FXMLLoader} utilisé pour le chargement, permettant d'accéder au contrôleur de la nouvelle vue.
     * @throws IOException Si le fichier FXML spécifié par {@code fxmlPath} n'existe pas ou s'il y a une erreur de lecture.
     */
    private FXMLLoader loadNewView(String fxmlPath, String title) throws IOException {
        URL fxmlUrl = getClass().getResource(fxmlPath);

        if (fxmlUrl == null) {
            throw new IOException("Le fichier FXML n'a pas été trouvé à l'emplacement: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load());

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();

        return loader;
    }
}
