package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Classe principale de l'application JavaFX.
 * Elle étend {@link javafx.application.Application} et initialise
 * l'interface graphique en chargeant la vue de connexion.
 *
 * La classe configure également la scène principale et applique
 * la feuille de style CSS de l'application.
 */
public class MainApplication extends Application {
    /**
     * Point d'entrée JavaFX. Cette méthode est appelée après
     * le lancement de l'application et initialise la scène principale.
     *
     * Elle charge la vue FXML du login, applique le CSS et affiche
     * la fenêtre principale.
     *
     * @param stage la fenêtre principale (stage) fournie par JavaFX.
     * @throws IOException si le fichier FXML ou la feuille de style CSS
     * ne peuvent pas être chargés.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/views/login-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 600, 400);
        String css = MainApplication.class.getResource("/styles/app.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("Gestion Utilisateurs");
        stage.setScene(scene);
        stage.show();
    }
}
