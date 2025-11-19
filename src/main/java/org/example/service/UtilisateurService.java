package org.example.service;


import org.example.dao.UtilisateurDAO;
import org.example.model.Utilisateur;

public class UtilisateurService {

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public Utilisateur login(String pseudo, String mdp) throws Exception {
        Utilisateur user = utilisateurDAO.login(pseudo, mdp);


        if (user == null) {
            throw new Exception("Identifiants incorrects.");
        }

        return user;
    }

    public void creerCompte(Utilisateur user) throws Exception {
        if (user.getNom().isEmpty() || user.getEmail().isEmpty() || user.getMotDePasse().isEmpty()) {
            throw new Exception("Tous les champs doivent être remplis.");
        }

        utilisateurDAO.addUser(user);
    }
}