package org.example.dao;

import org.example.model.Client;
import org.example.model.Organisateur;
import org.example.model.Utilisateur;
import org.example.util.Database;

import java.sql.*;

public class UtilisateurDAO {


    // Connexion utilisateur
    public Utilisateur login(String username, String mdp) throws SQLException {

        String sql = "SELECT * FROM `user` WHERE user_name = ? AND password = ?";

        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, username);
        ps.setString(2, mdp);
        System.out.println("ps"+ps);

        System.out.println("Tentative login : " + username + " / " + mdp);

        ResultSet rs = ps.executeQuery();


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
            } else { // Organisateur
                return new Organisateur(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }
        }else {
            System.out.println("Aucun utilisateur trouvé !");
        }

        return null;
    }


    // Inscription utilisateur
    public void addUser(Utilisateur user) throws SQLException {

        String sql = """
            INSERT INTO `user`(username, first_name, last_name, email, password, user_type)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, user.getPseudo());
        ps.setString(2, user.getPrenom());
        ps.setString(3, user.getNom());
        ps.setString(4, user.getEmail());
        ps.setString(5, user.getMotDePasse());
        ps.setString(6, (user instanceof Client) ? "Client" : "Organisateur");

        ps.executeUpdate();
    }
}
