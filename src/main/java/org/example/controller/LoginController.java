package org.example.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.controller.ReservationController;
import org.example.model.Client;
import org.example.model.Organisateur;
import org.example.model.Utilisateur;
import org.example.service.UtilisateurService;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField pseudoField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    private UtilisateurService userService = new UtilisateurService();

    @FXML
    public void initialize() {
        loginButton.setOnAction(e -> login());
        signupButton.setOnAction(e -> signup());
    }

    private void login() {
        String pseudo = pseudoField.getText();
        String mdp = passwordField.getText();

        try {
            Utilisateur user = userService.login(pseudo, mdp);

            if (user instanceof Client) {
                Client client = (Client) user;
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/reservation-view.fxml"));
                    Parent root = loader.load();

                    Object ctrl = loader.getController();
                    if (ctrl instanceof ReservationController) {
                        ((ReservationController) ctrl).setClient(client);
                    }

                    // remplacer la racine de la scene actuelle (même fenêtre)
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.getScene().setRoot(root);
                    stage.setTitle("Réservation");
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir la réservation: " + ex.getMessage(), ButtonType.OK);
                    alert.showAndWait();
                }

            } else if (user instanceof Organisateur) {
                // Pour l'instant on ne gère pas l'UI organisateur, on peut ouvrir une vue similaire
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Connexion organisateur réussie (UI non implémentée).", ButtonType.OK);
                a.showAndWait();
            }

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void signup() {
        // ouvrir fenêtre de création de compte
    }
}
