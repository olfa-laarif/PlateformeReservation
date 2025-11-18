package org.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ReservationDAO {

    /**
     * Sauvegarde la réservation et la relation place -> quantity.
     * Doit être appelée dans une transaction existante (connection fournie et gérée par l'appelant).
     * Retourne l'id généré de la réservation.
     */
    public int saveReservation(Connection conn, int clientId, int placeId, int quantity) throws SQLException {
        String insertReservation = "INSERT INTO reservation (reservation_date, client_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertReservation, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, clientId);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int resId = keys.getInt(1);

                    // insert relation reservation_has_place
                    String insertRel = "INSERT INTO reservation_has_place (reservation_id, place_id, quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement ps2 = conn.prepareStatement(insertRel)) {
                        ps2.setInt(1, resId);
                        ps2.setInt(2, placeId);
                        ps2.setInt(3, quantity);
                        ps2.executeUpdate();
                    }

                    return resId;
                } else {
                    throw new SQLException("Impossible de récupérer l'ID de réservation généré.");
                }
            }
        }
    }

    /**
     * Liste les réservations (une ligne par pair reservation/place) pour un client.
     */
    public java.util.List<org.example.model.ReservationRecord> listByClient(int clientId) throws SQLException {
        String sql = "SELECT r.reservation_id, r.reservation_date, e.name AS event_name, e.event_date, p.place_id, cat.category_name, rhp.quantity, p.price, (p.price * rhp.quantity) AS total " +
                "FROM reservation r " +
                "JOIN reservation_has_place rhp ON r.reservation_id = rhp.reservation_id " +
                "JOIN place p ON rhp.place_id = p.place_id " +
                "JOIN event e ON p.event_id = e.event_id " +
                "JOIN category cat ON p.category_id = cat.category_id " +
                "WHERE r.client_id = ? ORDER BY r.reservation_date DESC";
        throw new SQLException("Use listByClient(Connection, int) instead.");
    }

    public java.util.List<org.example.model.ReservationRecord> listByClient(Connection conn, int clientId) throws SQLException {
        String sql = "SELECT r.reservation_id, r.reservation_date, e.name AS event_name, e.event_date, p.place_id, cat.category_name, rhp.quantity, p.price, (p.price * rhp.quantity) AS total " +
                "FROM reservation r " +
                "JOIN reservation_has_place rhp ON r.reservation_id = rhp.reservation_id " +
                "JOIN place p ON rhp.place_id = p.place_id " +
                "JOIN event e ON p.event_id = e.event_id " +
                "JOIN category cat ON p.category_id = cat.category_id " +
                "WHERE r.client_id = ? ORDER BY r.reservation_date DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                java.util.List<org.example.model.ReservationRecord> list = new java.util.ArrayList<>();
                while (rs.next()) {
                    int reservationId = rs.getInt("reservation_id");
                    java.sql.Timestamp resDateTs = rs.getTimestamp("reservation_date");
                    java.time.LocalDateTime reservationDate = resDateTs.toLocalDateTime();
                    String eventName = rs.getString("event_name");
                    java.sql.Timestamp evTs = rs.getTimestamp("event_date");
                    java.time.LocalDateTime eventDate = evTs.toLocalDateTime();
                    int placeId = rs.getInt("place_id");
                    String categoryName = rs.getString("category_name");
                    int quantity = rs.getInt("quantity");
                    double total = rs.getDouble("total");

                    list.add(new org.example.model.ReservationRecord(reservationId, eventName, eventDate, categoryName, placeId, quantity, total, reservationDate));
                }
                return list;
            }
        }
    }

    /**
     * Récupère les détails nécessaires pour annuler (place_id, quantity, event_date, reservation_date)
     */
    public java.util.Optional<org.example.model.ReservationRecord> getDetails(Connection conn, int reservationId) throws SQLException {
        String sql = "SELECT r.reservation_id, r.reservation_date, e.name AS event_name, e.event_date, p.place_id, cat.category_name, rhp.quantity, p.price, (p.price * rhp.quantity) AS total " +
                "FROM reservation r " +
                "JOIN reservation_has_place rhp ON r.reservation_id = rhp.reservation_id " +
                "JOIN place p ON rhp.place_id = p.place_id " +
                "JOIN event e ON p.event_id = e.event_id " +
                "JOIN category cat ON p.category_id = cat.category_id " +
                "WHERE r.reservation_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int resId = rs.getInt("reservation_id");
                    java.sql.Timestamp resDateTs = rs.getTimestamp("reservation_date");
                    java.time.LocalDateTime reservationDate = resDateTs.toLocalDateTime();
                    String eventName = rs.getString("event_name");
                    java.sql.Timestamp evTs = rs.getTimestamp("event_date");
                    java.time.LocalDateTime eventDate = evTs.toLocalDateTime();
                    int placeId = rs.getInt("place_id");
                    String categoryName = rs.getString("category_name");
                    int quantity = rs.getInt("quantity");
                    double total = rs.getDouble("total");

                    return java.util.Optional.of(new org.example.model.ReservationRecord(resId, eventName, eventDate, categoryName, placeId, quantity, total, reservationDate));
                }
                return java.util.Optional.empty();
            }
        }
    }

    /**
     * Supprime une réservation et ses relations (doit être appelée dans une transaction).
     */
    public void deleteReservation(Connection conn, int reservationId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM reservation_has_place WHERE reservation_id = ?")) {
            ps.setInt(1, reservationId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM payment WHERE reservation_id = ?")) {
            ps.setInt(1, reservationId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM reservation WHERE reservation_id = ?")) {
            ps.setInt(1, reservationId);
            ps.executeUpdate();
        }
    }
}
