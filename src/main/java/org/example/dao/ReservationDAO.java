package org.example.dao;

import org.example.model.ReservationSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO dédié aux opérations d'accès/écriture pour les réservations
 * (création, listing agrégé, récupération des places et suppression).
 */
public class ReservationDAO {

    /**
     * Crée une réservation et insère les liens vers les places. Doit être appelé dans une transaction.
     */
    public int saveReservation(Connection conn, int clientId, List<Integer> placeIds) throws SQLException {
        String insertReservation = "INSERT INTO reservation (reservation_date, client_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertReservation, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, clientId);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int resId = keys.getInt(1);
                    String insertRel = "INSERT INTO reservation_has_place (reservation_id, place_id) VALUES (?, ?)";
                    try (PreparedStatement ps2 = conn.prepareStatement(insertRel)) {
                        for (Integer pid : placeIds) {
                            ps2.setInt(1, resId);
                            ps2.setInt(2, pid);
                            ps2.addBatch();
                        }
                        ps2.executeBatch();
                    }
                    return resId;
                } else {
                    throw new SQLException("Impossible de récupérer l'ID de réservation généré.");
                }
            }
        }
    }

    /**
     * Liste les réservations agrégées par réservation pour un client.
     */
    public List<ReservationSummary> listByClient(Connection conn, int clientId) throws SQLException {
        String sql = "SELECT r.reservation_id, r.reservation_date, e.name AS event_name, e.event_date, COUNT(rhp.place_id) AS quantity, SUM(p.price) AS total " +
            "FROM reservation r " +
            "JOIN reservation_has_place rhp ON r.reservation_id = rhp.reservation_id " +
            "JOIN place p ON rhp.place_id = p.place_id " +
            "JOIN event e ON p.event_id = e.event_id " +
            "JOIN payment pay ON pay.reservation_id = r.reservation_id " +
            "WHERE r.client_id = ? " +
            "GROUP BY r.reservation_id, r.reservation_date, e.name, e.event_date " +
            "ORDER BY r.reservation_date DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                List<ReservationSummary> list = new ArrayList<>();
                while (rs.next()) {
                    int resId = rs.getInt("reservation_id");
                    Timestamp resTs = rs.getTimestamp("reservation_date");
                    LocalDateTime resDate = resTs.toLocalDateTime();
                    String eventName = rs.getString("event_name");
                    Timestamp evTs = rs.getTimestamp("event_date");
                    LocalDateTime evDate = evTs.toLocalDateTime();
                    int qty = rs.getInt("quantity");
                    double total = rs.getDouble("total");

                    list.add(new ReservationSummary(resId, eventName, evDate, qty, total, resDate));
                }
                return list;
            }
        }
    }

    /**
     * Récupère la liste des place_ids liés à une réservation (utilisé pour annulation).
     */
    public List<Integer> getPlaceIdsForReservation(Connection conn, int reservationId) throws SQLException {
        String sql = "SELECT place_id FROM reservation_has_place WHERE reservation_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Integer> ids = new ArrayList<>();
                while (rs.next()) ids.add(rs.getInt("place_id"));
                return ids;
            }
        }
    }

    /**
     * Supprime réservation et relations (dans une transaction).
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
