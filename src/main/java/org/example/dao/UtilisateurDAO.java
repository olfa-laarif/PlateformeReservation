package org.example.dao;

import org.example.model.Client;
import org.example.model.Organisateur;
import org.example.model.Utilisateur;
import java.sql.*;


/**
 * DAO (Data Access Object) responsable des opérations CRUD liées aux utilisateurs.
 * Cette classe permet la connexion, la création de comptes, ainsi que la vérification
 * de l'existence d'un email ou d'un pseudo en base de données.
 */
public class UtilisateurDAO {

    private Connection connexion;

    /**
     * Construit un DAO utilisateur utilisant une connexion JDBC.
     *
     * @param connexion la connexion active à la base de données.
     */
    public UtilisateurDAO(Connection connexion) {
        this.connexion = connexion;
    }

    /**
     * Tente de connecter un utilisateur en recherchant en base de données
     * un enregistrement correspondant au pseudo et au mot de passe donnés.
     *
     * @param username le nom d'utilisateur saisi.
     * @param mdp      le mot de passe saisi.
     * @return un objet {@link Client} ou {@link Organisateur} si les identifiants
     *         correspondent à un utilisateur existant, sinon {@code null}.
     * @throws SQLException en cas d'erreur SQL lors de l'exécution de la requête.
     */
    // Connexion utilisateur
    public Utilisateur login(String username, String mdp) throws SQLException {
        String sql = "SELECT * FROM `user` WHERE user_name = ? AND password = ?";

        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, mdp);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("user_type");
                    if ("Client".equalsIgnoreCase(type)) {
                        return new Client(
                                rs.getInt("user_id"),
                                rs.getString("user_name"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("email"),
                                rs.getString("password")
                        );
                    } else {
                        return new Organisateur(
                                rs.getInt("user_id"),
                                rs.getString("user_name"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("email"),
                                rs.getString("password")
                        );
                    }
                }
            }
        }

        return null; // utilisateur non trouvé
    }

    /**
     * Ajoute un nouvel utilisateur dans la base de données.
     * Avant l'insertion, cette méthode vérifie que le pseudo et l'email
     * ne sont pas déjà utilisés.
     *
     * @param user l'utilisateur à ajouter (Client ou Organisateur).
     * @throws Exception si le pseudo ou l'email existe déjà, ou en cas d'erreur SQL.
     */
    public void addUser(Utilisateur user) throws Exception {
        if (existePseudo(user.getPseudo())) {
            throw new Exception("Ce pseudo est déjà utilisé.");
        }
        if (existeEmail(user.getEmail())) {
            throw new Exception("Cet email est déjà utilisé.");
        }

        String sql = """
            INSERT INTO `user`(user_name, first_name, last_name, email, password, user_type)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setString(1, user.getPseudo());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getNom());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getMotDePasse());
            ps.setString(6, (user instanceof Client) ? "Client" : "Organisateur");
            ps.executeUpdate();
        }
    }

    /**
     * Vérifie si un email existe déjà dans la table des utilisateurs.
     *
     * @param email l'adresse email à vérifier.
     * @return {@code true} si l'email est déjà utilisé, {@code false} sinon.
     * @throws SQLException si une erreur survient lors de l'exécution de la requête.
     */
    public boolean existeEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM user WHERE email = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Vérifie si un pseudo existe déjà dans la table des utilisateurs.
     *
     * @param pseudo le pseudo à vérifier.
     * @return {@code true} si le pseudo existe déjà, {@code false} sinon.
     * @throws SQLException si une erreur survient lors de l'exécution de la requête SQL.
     */
    public boolean existePseudo(String pseudo) throws SQLException {
        String sql = "SELECT 1 FROM user WHERE user_name = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, pseudo);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
