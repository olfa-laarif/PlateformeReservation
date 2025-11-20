package org.example.service;

import org.example.dao.PlaceDAO;
import org.example.dao.ReservationDAO;
import org.example.exception.PlacesInsuffisantesException;
import org.example.model.Client;
import org.example.model.Evenement;
import org.example.model.Place;
import org.example.model.Reservation;
import org.example.util.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier regroupant les opérations de gestion des réservations :
 * création transactionnelle, annulation et validations associées.
 */
public class ReservationService {

	private final PlaceDAO placeDAO = new PlaceDAO();
	private final ReservationDAO reservationDAO = new ReservationDAO();

	/**
	 * Réserve `nbPlaces` places pour le client sur l'événement et la catégorie fournis.
	 * Recherche des places libres, les marque réservées et crée la réservation (transactionnel).
	 * @param client client demandeur
	 * @param event événement sélectionné
	 * @param categoryId identifiant de catégorie de siège
	 * @param nbPlaces quantité désirée
	 * @return réservation matérialisée avec les places retenues
	 * @throws PlacesInsuffisantesException si le stock est insuffisant
	 */
	public Reservation reserver(Client client, Evenement event, int categoryId, int nbPlaces) throws PlacesInsuffisantesException {
		try {
			return Database.runInTransaction(conn -> {
				List<Place> free = placeDAO.findFreePlacesByEventAndCategory(conn, event.getIdEvenement(), categoryId, nbPlaces);
				if (free.size() < nbPlaces) {
					throw new PlacesInsuffisantesException("Pas assez de places libres pour cette catégorie.");
				}

				List<Integer> ids = free.stream().map(Place::getIdPlace).collect(Collectors.toList());
				int resId = reservationDAO.saveReservation(conn, client.getIdUser(), ids);
				return new Reservation(resId, client, event, free, LocalDateTime.now());
			});
		} catch (PlacesInsuffisantesException pie) {
			throw pie;
		} catch (Exception e) {
			throw new RuntimeException("Erreur lors de la réservation: " + e.getMessage(), e);
		}
	}

	/**
	 * Annule une réservation : vérifie la propriété, la fenêtre temporelle puis libère les places.
	 * @param reservationId identifiant à supprimer
	 * @param client propriétaire attendu de la réservation
	 * @throws Exception si l'annulation est refusée ou échoue
	 */
	public void annulerReservation(int reservationId, Client client) throws Exception {
		try {
			Database.runInTransaction(conn -> {
				// vérifier propriétaire
				try (var ps = conn.prepareStatement("SELECT client_id FROM reservation WHERE reservation_id = ?")) {
					ps.setInt(1, reservationId);
					try (var rs = ps.executeQuery()) {
						if (rs.next()) {
							int clientId = rs.getInt("client_id");
							if (clientId != client.getIdUser()) throw new SecurityException("Réservation non autorisée.");
						} else throw new IllegalArgumentException("Réservation introuvable.");
					}
				}

				// récupère place ids
				List<Integer> placeIds = reservationDAO.getPlaceIdsForReservation(conn, reservationId);

				// vérifier règle annulation : récupérer event date via join
				try (var ps = conn.prepareStatement("SELECT e.event_date FROM event e JOIN place p ON e.event_id = p.event_id JOIN reservation_has_place rhp ON p.place_id = rhp.place_id WHERE rhp.reservation_id = ? LIMIT 1")) {
					ps.setInt(1, reservationId);
					try (var rs = ps.executeQuery()) {
						if (rs.next()) {
							var evTs = rs.getTimestamp("event_date");
							var evDate = evTs.toLocalDateTime();
							if (LocalDateTime.now().isAfter(evDate.minusHours(24))) {
								throw new org.example.exception.AnnulationTardiveException("Annulation impossible : moins de 24h avant l'événement.");
							}
						}
					}
				}

				// supprimer la réservation (cela supprime aussi les lignes dans reservation_has_place)
				reservationDAO.deleteReservation(conn, reservationId);
				return null;
			});
		} catch (Exception e) {
			if (e instanceof org.example.exception.AnnulationTardiveException) throw e;
			throw new RuntimeException("Erreur annulation: " + e.getMessage(), e);
		}
	}
}
