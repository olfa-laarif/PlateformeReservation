package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.dao.UtilisateurDAO;
import org.example.model.Client;
import org.example.model.Organisateur;
import org.example.model.Utilisateur;
import org.example.service.UtilisateurService;
import org.example.util.Database;

import java.sql.Connection;
import java.sql.SQLException;

public class SignUpController {

    @FXML
    private TextField pseudoField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> typeCompteComboBox;

    @FXML
    private Button signupButton;

    @FXML
    private Button cancelButton;

    private UtilisateurService userService;


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

        // Remplir le ComboBox si nécessaire
        if (typeCompteComboBox.getItems().isEmpty()) {
            typeCompteComboBox.getItems().addAll("Client", "Organisateur");
        }

        // Actions sur les boutons
        signupButton.setOnAction(e -> creerCompte());
        cancelButton.setOnAction(e -> fermerFenetre());
    }


    private void creerCompte() {
        String pseudo = pseudoField.getText().trim();
        String prenom = prenomField.getText().trim();
        String nom = nomField.getText().trim();
        String email = emailField.getText().trim();
        String mdp = passwordField.getText().trim();
        String typeCompte = typeCompteComboBox.getValue();

        try {
            if (pseudo.isEmpty() || prenom.isEmpty() || nom.isEmpty() || email.isEmpty() || mdp.isEmpty() || typeCompte == null) {
                throw new Exception("Tous les champs doivent être remplis.");
            }

            Utilisateur user;
            if ("Client".equalsIgnoreCase(typeCompte)) {
                user = new Client(0, pseudo, prenom, nom, email, mdp);
            } else {
                user = new Organisateur(0, pseudo, prenom, nom, email, mdp);
            }

            userService.creerCompte(user);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Compte créé avec succès !", ButtonType.OK);
            alert.showAndWait();

            fermerFenetre();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void fermerFenetre() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
