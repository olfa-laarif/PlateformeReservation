package org.example.service;

import org.example.dao.CategoriePlaceDAO;
import org.example.dao.ReservationDAO;
import org.example.exception.PlacesInsuffisantesException;
import org.example.model.CategoriePlace;
import org.example.model.Client;
import org.example.model.Evenement;
import org.example.model.Reservation;
import org.example.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationService {

	private final CategoriePlaceDAO categoriePlaceDAO = new CategoriePlaceDAO();
	private final ReservationDAO reservationDAO = new ReservationDAO();

	/**
	 * Réserve des places pour un client sur une catégorie d'un événement en base de données.
	 * Utilise une transaction pour décrémenter les places et créer la réservation atomiquement.
	 *
	 * @throws PlacesInsuffisantesException si pas assez de places
	 */
	public Reservation reserver(Client client, Evenement event, int categorieId, int nbPlaces) throws PlacesInsuffisantesException {

		CategoriePlace categorie = null;
		for (CategoriePlace c : event.getCategories()) {
			if (c.getIdCategorie() == categorieId) {
				categorie = c;
				break;
			}
		}

		if (categorie == null) {
			throw new IllegalArgumentException("Catégorie introuvable pour cet événement (id=" + categorieId + ")");
		}

		try (Connection conn = Database.getConnection()) {
			try {
				conn.setAutoCommit(false);

				// tente de décrémenter les places dans la base (conditionnelle)
				boolean ok = categoriePlaceDAO.decrementPlaces(conn, categorieId, nbPlaces);
				if (!ok) {
					conn.rollback();
					throw new PlacesInsuffisantesException("Pas assez de places disponibles.");
				}

				// récupère le nouveau nombre de places restantes
				int newRemaining = categoriePlaceDAO.getPlacesRestantes(conn, categorieId);

				// crée la réservation en base
				int generatedId = reservationDAO.saveReservation(conn, client.getIdUser(), categorieId, nbPlaces);

				conn.commit();

				// met à jour l'objet en mémoire pour refléter la base
				categorie.setPlacesRestantes(newRemaining);

				// retourne la réservation avec l'id généré
				return new Reservation(generatedId, client, event, categorie, nbPlaces);

			} catch (SQLException | PlacesInsuffisantesException e) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					// ignore
				}
				if (e instanceof PlacesInsuffisantesException) throw (PlacesInsuffisantesException) e;
				throw new RuntimeException("Erreur lors de la réservation : " + e.getMessage(), e);
			} finally {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException ignored) {}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Impossible d'accéder à la base de données : " + e.getMessage(), e);
		}
	}

	/**
	 * Annule une réservation (client peut annuler la sienne). Vérifie la limite de temps via
	 * la logique métier (24h) et remet les places en base.
	 */
	public void annulerReservation(int reservationId, Client client) throws Exception {
		try (Connection conn = Database.getConnection()) {
			try {
				conn.setAutoCommit(false);

				// récupère détails
				var opt = reservationDAO.getDetails(conn, reservationId);
				if (opt.isEmpty()) throw new IllegalArgumentException("Réservation introuvable.");
				var rec = opt.get();

				// vérifier que le client est bien le propriétaire
				// récupère reservation.client_id pour vérifier
				try (PreparedStatement ps = conn.prepareStatement("SELECT client_id FROM reservation WHERE reservation_id = ?")) {
					ps.setInt(1, reservationId);
					try (var rs = ps.executeQuery()) {
						if (rs.next()) {
							int clientId = rs.getInt("client_id");
							if (clientId != client.getIdUser()) throw new SecurityException("Vous ne pouvez pas annuler cette réservation.");
						} else {
							throw new IllegalArgumentException("Réservation introuvable.");
						}
					}
				}

				// vérifier la règle d'annulation (moins de 24h)
				java.time.LocalDateTime now = java.time.LocalDateTime.now();
				java.time.LocalDateTime limite = rec.getEventDate().minusHours(24);
				if (now.isAfter(limite)) {
					throw new org.example.exception.AnnulationTardiveException("Annulation impossible : moins de 24h avant l'événement.");
				}

				// remettre les places
				boolean restored = categoriePlaceDAO.incrementPlaces(conn, rec.getPlaceId(), rec.getQuantity());
				if (!restored) {
					conn.rollback();
					throw new RuntimeException("Impossible de restaurer les places.");
				}

				// supprimer la réservation et ses relations
				reservationDAO.deleteReservation(conn, reservationId);

				conn.commit();
			} catch (Exception e) {
				try { conn.rollback(); } catch (SQLException ex) {}
				throw e;
			} finally {
				try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
			}
		}
	}
}
