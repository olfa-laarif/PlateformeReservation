package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/views/login-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 600, 400);
        // attach application stylesheet
        scene.getStylesheets().add(MainApplication.class.getResource("/styles/app.css").toExternalForm());
        stage.setTitle("Gestion Utilisateurs");
        stage.setScene(scene);
        stage.show();
    }
}
