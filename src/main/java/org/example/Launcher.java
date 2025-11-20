package org.example;

import javafx.application.Application;


/**
 * Classe de lancement de l'application JavaFX.
 * Elle sert de point d'entrée principal et délègue le démarrage
 * de l'application à la classe {@link MainApplication}.
 */
public class Launcher {
    /**
     * Méthode main servant de point d'entrée de l'application.
     * Elle appelle la méthode {@code launch} de JavaFX pour démarrer
     * l'application graphique.
     */
    public static void main(String[] args) {
        Application.launch(MainApplication.class, args);
    }
}
