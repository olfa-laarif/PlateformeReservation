package org.example.controller;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.Client;
import org.example.model.Organisateur;
import org.example.model.Utilisateur;
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
               //TODO
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
