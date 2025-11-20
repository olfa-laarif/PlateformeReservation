package org.example.service;

import org.example.dao.UtilisateurDAO;
import org.example.model.Utilisateur;

/**
 * Service gérant la logique métier liée aux utilisateurs.
 * Il assure la validation des données avant leur envoi au DAO,
 * ainsi que la gestion des opérations de connexion et de création de compte.
 */
public class UtilisateurService {

    private UtilisateurDAO utilisateurDAO;


    /**
     * Construit le service utilisateur à partir d'un DAO donné.
     *
     * @param utilisateurDAO le DAO chargé d'interagir avec la base de données.
     */
    public UtilisateurService(UtilisateurDAO utilisateurDAO) {
        this.utilisateurDAO = utilisateurDAO;
    }



    /**
     * Tente de connecter un utilisateur à partir de son pseudo et de son mot de passe.
     * Les champs sont d'abord validés puis transmis au DAO pour vérification.
     *
     * @param pseudo le pseudo saisi.
     * @param mdp    le mot de passe saisi.
     * @return l'objet {@link Utilisateur} correspondant si l'authentification réussit.
     * @throws Exception si les champs sont vides ou si les identifiants sont incorrects.
     */
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

    /**
     * Crée un nouveau compte utilisateur après validation des informations essentielles.
     * La méthode délègue ensuite l'insertion en base au DAO.
     *
     * @param user l'utilisateur à créer.
     * @throws Exception si des champs obligatoires sont vides ou si une erreur survient lors de l'insertion.
     */
    public void creerCompte(Utilisateur user) throws Exception {
        if (user.getPseudo().isEmpty() || user.getNom().isEmpty() || user.getEmail().isEmpty() || user.getMotDePasse().isEmpty()) {
            throw new Exception("Tous les champs doivent être remplis.");
        }

        utilisateurDAO.addUser(user);
    }
}
