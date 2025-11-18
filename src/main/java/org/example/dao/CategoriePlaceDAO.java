package org.example.dao;

import org.example.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO pour les opérations sur la table `place` (catégorie d'un événement).
 * NOTE: on suppose que l'id utilisé dans l'application (`CategoriePlace.getIdCategorie()`)
 * correspond à la colonne `place_id` dans la table `place`.
 */
public class CategoriePlaceDAO {

	/**
	 * Récupère le nombre de places restantes pour une place (place_id).
	 */
	public Integer getPlacesRestantes(int placeId) throws SQLException {
		String sql = "SELECT places_remaining FROM place WHERE place_id = ?";
		try (Connection conn = Database.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, placeId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("places_remaining");
				}
				return null;
			}
		}
	}

	/**
	 * Décrémente le nombre de places (utilisé dans une transaction). Retourne true si l'update a été effectué.
	 */
	public boolean decrementPlaces(Connection conn, int placeId, int quantity) throws SQLException {
		String update = "UPDATE place SET places_remaining = places_remaining - ? WHERE place_id = ? AND places_remaining >= ?";
		try (PreparedStatement ps = conn.prepareStatement(update)) {
			ps.setInt(1, quantity);
			ps.setInt(2, placeId);
			ps.setInt(3, quantity);
			int affected = ps.executeUpdate();
			return affected > 0;
		}
	}

	/**
	 * Récupère la valeur courante de places_remaining (avec la connection courante).
	 */
	public int getPlacesRestantes(Connection conn, int placeId) throws SQLException {
		String sql = "SELECT places_remaining FROM place WHERE place_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, placeId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt("places_remaining");
				throw new SQLException("Place introuvable (id=" + placeId + ")");
			}
		}
	}

	/**
	 * Incrémente le nombre de places (utilisé lors d'une annulation).
	 */
	public boolean incrementPlaces(Connection conn, int placeId, int quantity) throws SQLException {
		String update = "UPDATE place SET places_remaining = places_remaining + ? WHERE place_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(update)) {
			ps.setInt(1, quantity);
			ps.setInt(2, placeId);
			int affected = ps.executeUpdate();
			return affected > 0;
		}
	}
}
