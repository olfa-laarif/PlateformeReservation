package org.example.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.MainApplication;
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
            ouvrirDashboard(user);
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void signup() {
        // ouvrir fenêtre de création de compte
    }

    private void ouvrirDashboard(Utilisateur utilisateur) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/evenements-view.fxml"));
        Parent root = loader.load();
        EvenementController controller = loader.getController();
        controller.initData(utilisateur);

        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(root, 980, 720));
        stage.setTitle("Plateforme - " + utilisateur.getTypeCompte());
        stage.centerOnScreen();
    }
}
