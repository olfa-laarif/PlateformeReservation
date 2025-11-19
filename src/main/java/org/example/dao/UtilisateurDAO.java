package org.example.dao;

import org.example.model.Client;
import org.example.model.Organisateur;
import org.example.model.Utilisateur;

import java.sql.*;

public class UtilisateurDAO {

    private Connection connexion;

    public UtilisateurDAO(Connection connexion) {
        this.connexion = connexion;
    }

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

    // Création utilisateur
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

    // Vérifier email existant
    public boolean existeEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM user WHERE email = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Vérifier pseudo existant
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
