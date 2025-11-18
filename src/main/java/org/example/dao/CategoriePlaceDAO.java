package org.example.dao;

import org.example.model.Categorie;
import org.example.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriePlaceDAO {

    /**
     * Retourne toutes les catégories stockées dans la base.
     * On se contente ici de lire la table et de construire des objets simples.
     */
    public List<Categorie> findAll() throws SQLException {
        String sql = "SELECT category_id, category_name FROM category ORDER BY category_name";
        List<Categorie> categories = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("category_id");
                String nom = rs.getString("category_name");
                categories.add(new Categorie(id, nom));
            }
        }

        return categories;
    }

    /**
     * Cherche une catégorie par son nom. Si elle existe on la renvoie,
     * sinon on la crée immédiatement puis on la retourne.
     */
    public Categorie findOrCreateByName(Connection connection, String nomCategorie) throws SQLException {
        String nomNettoye = nomCategorie == null ? "" : nomCategorie.trim();

        // 1) On regarde si la catégorie existe déjà
        Categorie existante = chercherParNom(connection, nomNettoye);
        if (existante != null) {
            return existante;
        }

        // 2) Sinon on l'insère
        String insertSql = "INSERT INTO category (category_name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nomNettoye);
            ps.executeUpdate();

            // 3) On récupère l'identifiant généré automatiquement
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int idGenere = keys.getInt(1);
                    return new Categorie(idGenere, nomNettoye);
                }
            }
        }

        throw new SQLException("Impossible de créer la catégorie : " + nomNettoye);
    }

    /**
     * Méthode utilitaire : on essaie simplement de retrouver une ligne par son nom.
     */
    private Categorie chercherParNom(Connection connection, String nomCategorie) throws SQLException {
        String sql = "SELECT category_id, category_name FROM category WHERE LOWER(category_name) = LOWER(?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nomCategorie);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("category_id");
                    String nom = rs.getString("category_name");
                    return new Categorie(id, nom);
                }
            }
        }

        return null;
    }
}
