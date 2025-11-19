package org.example.dao;

import org.example.model.Categorie;
import org.example.model.Evenement;
import org.example.model.Organisateur;
import org.example.model.Place;
import org.example.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaceDAO {

    /**
     * Récupère jusqu'à `limit` places libres pour un événement et une catégorie.
     * Doit être appelé avec une connection (pour contrôler la transaction si besoin).
     */
    public List<Place> findFreePlacesByEventAndCategory(Connection conn, int eventId, int categoryId, int limit) throws SQLException {
        String sql = "SELECT p.place_id, p.price, c.category_id, c.category_name, e.event_id, e.name as event_name, e.event_date, e.location " +
            "FROM place p " +
            "JOIN category c ON p.category_id = c.category_id " +
            "JOIN event e ON p.event_id = e.event_id " +
            "LEFT JOIN reservation_has_place rhp ON p.place_id = rhp.place_id " +
            "WHERE p.event_id = ? AND p.category_id = ? AND rhp.place_id IS NULL " +
            "LIMIT ? FOR UPDATE";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, categoryId);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<Place> list = new ArrayList<>();
                while (rs.next()) {
                    int placeId = rs.getInt("place_id");
                    double price = rs.getDouble("price");
                    int catId = rs.getInt("category_id");
                    String catName = rs.getString("category_name");
                    int evId = rs.getInt("event_id");
                    String evName = rs.getString("event_name");
                    java.sql.Timestamp evTs = rs.getTimestamp("event_date");

                    Categorie cat = new Categorie(catId, catName);
                    Evenement ev = new Evenement(evId, evName, evTs.toLocalDateTime(), rs.getString("location"), (Organisateur) null) {
                        @Override
                        public String getSpecialGuest() { return ""; }
                    };

                    Place p = new Place(placeId, price, cat, ev);
                    list.add(p);
                }
                return list;
            }
        }
    }

    /**
     * NOTE: reservation is stored in `reservation_has_place`. We don't store a `libre` flag.
     * The act of reserving is inserting into `reservation_has_place` (handled by ReservationDAO.saveReservation).
     * These helper methods are intentionally unsupported.
     */
    public boolean reservePlaces(Connection conn, List<Integer> placeIds) {
        throw new UnsupportedOperationException("reservePlaces not supported; reservation is done via ReservationDAO.saveReservation");
    }

    public boolean releasePlaces(Connection conn, List<Integer> placeIds) {
        throw new UnsupportedOperationException("releasePlaces not supported; release is done by deleting reservation_has_place rows via ReservationDAO.deleteReservation");
    }
}
