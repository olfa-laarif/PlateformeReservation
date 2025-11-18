package org.example.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.model.Client;
import org.example.model.Organisateur;
import org.example.model.Utilisateur;
import org.example.controller.ReservationController;
import org.example.service.UtilisateurService;

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
                // Ouvre le dashboard réservation pour le client
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/reservation-view.fxml"));
                    Parent root = loader.load();
                    ReservationController controller = loader.getController();
                    controller.setClient((Client) user);

                    Stage stage = new Stage();
                    stage.setTitle("Dashboard Client - Réservation");
                    stage.setScene(new Scene(root, 600, 450));
                    stage.show();

                    // ferme la fenêtre de login
                    loginButton.getScene().getWindow().hide();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir le dashboard : " + e.getMessage(), ButtonType.OK);
                    alert.showAndWait();
                }
            } else if (user instanceof Organisateur) {
               //TODO
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
