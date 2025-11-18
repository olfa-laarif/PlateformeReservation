package org.example.dao;

import org.example.model.Concert;
import org.example.model.Evenement;
import org.example.model.Organisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.example.util.Database;

public class EvenementDAO {

    /**
     * Liste les événements (id, name, event_date, location). Utilise une classe concrète simple.
     */
    public List<Evenement> listAll() throws SQLException {
        String sql = "SELECT event_id, name, event_date, location FROM event ORDER BY event_date";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Evenement> list = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("event_id");
                    String name = rs.getString("name");
                    java.sql.Timestamp ts = rs.getTimestamp("event_date");
                    LocalDateTime date = ts == null ? LocalDateTime.now() : ts.toLocalDateTime();
                    String location = rs.getString("location");

                    // On crée un Concert simple comme instance concrète (special guest vide)
                    Concert c = new Concert(id, name, date, location, (Organisateur) null, "", null);
                    list.add(c);
                }
                return list;
            }
        }
    }

    /**
     * Récupère les catégories (id, name) disponibles pour un événement.
     */
    public List<org.example.model.Categorie> listCategoriesForEvent(int eventId) throws SQLException {
        String sql = "SELECT DISTINCT c.category_id, c.category_name FROM category c JOIN place p ON c.category_id = p.category_id WHERE p.event_id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                List<org.example.model.Categorie> list = new ArrayList<>();
                while (rs.next()) {
                    int cid = rs.getInt("category_id");
                    String cname = rs.getString("category_name");
                    list.add(new org.example.model.Categorie(cid, cname));
                }
                return list;
            }
        }
    }
}
 
