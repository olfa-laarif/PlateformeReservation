package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.dao.UtilisateurDAO;
import org.example.model.Client;
import org.example.model.Organisateur;
import org.example.model.Utilisateur;
import org.example.service.UtilisateurService;
import org.example.util.Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField pseudoField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button signupButton;

    private UtilisateurService userService;

    @FXML
    public void initialize() {
        try {
            // Initialiser service avec DAO et connexion
            Connection connexion = Database.getConnection();
            UtilisateurDAO utilisateurDAO = new UtilisateurDAO(connexion);
            userService = new UtilisateurService(utilisateurDAO);

        } catch (SQLException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur de connexion à la base : " + ex.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }

        loginButton.setOnAction(e -> login());
        signupButton.setOnAction(e -> signup());
    }

    private void login() {
        try {
            String pseudo = pseudoField.getText();
            String mdp = passwordField.getText();

            Utilisateur user = userService.login(pseudo, mdp);

            if (user instanceof Client) {
                System.out.println("Login Client réussi !");
                // TODO : ouvrir dashboard Client
            } else if (user instanceof Organisateur) {
                System.out.println("Login Organisateur réussi !");
                // TODO : ouvrir dashboard Organisateur
            }

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void signup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/signup-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Créer un compte");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir le formulaire d'inscription : " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

}
