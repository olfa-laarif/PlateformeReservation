package org.example.service;

import org.example.dao.UtilisateurDAO;
import org.example.model.Utilisateur;

public class UtilisateurService {

    private UtilisateurDAO utilisateurDAO;

    public UtilisateurService(UtilisateurDAO utilisateurDAO) {
        this.utilisateurDAO = utilisateurDAO;
    }

    // Connexion utilisateur
    public Utilisateur login(String pseudo, String mdp) throws Exception {
        if (pseudo.isEmpty() || mdp.isEmpty()) {
            throw new Exception("Pseudo et mot de passe requis.");
        }

        Utilisateur user = utilisateurDAO.login(pseudo, mdp);

        if (user == null) {
            throw new Exception("Identifiants incorrects.");
        }

        return user;
    }

    // Création de compte
    public void creerCompte(Utilisateur user) throws Exception {
        if (user.getPseudo().isEmpty() || user.getNom().isEmpty() || user.getEmail().isEmpty() || user.getMotDePasse().isEmpty()) {
            throw new Exception("Tous les champs doivent être remplis.");
        }

        utilisateurDAO.addUser(user);
    }
}
