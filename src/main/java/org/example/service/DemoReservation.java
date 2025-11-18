package org.example.service;

import org.example.exception.PlacesInsuffisantesException;
import org.example.model.CategoriePlace;
import org.example.model.Client;
import org.example.model.Concert;
import org.example.model.Reservation;

import java.time.LocalDate;

public class DemoReservation {

    public static void main(String[] args) {
        // Crée un événement
        Concert concert = new Concert(1, "Rock Night", LocalDate.now().plusDays(10), "Stade", "The Band");

        // Ajoute des catégories
        CategoriePlace vip = new CategoriePlace(1, "VIP", 100.0, 5);
        CategoriePlace gradin = new CategoriePlace(2, "Gradin", 40.0, 100);

        concert.ajouterCategorie(vip);
        concert.ajouterCategorie(gradin);

        // Crée un client
        Client client = new Client(1, "jdupont", "Jean", "Dupont", "jean@example.com", "pass");

        ReservationService rs = new ReservationService();

        try {
            // Réserve 2 places en VIP
            Reservation r = rs.reserver(client, concert, 1, 2);
            client.ajouterReservation(r);
            System.out.println("Réservation réussie : id=" + r.getIdReservation() + ", places=" + r.getNbPlaces());

            // Tente de réserver plus que le reste
            rs.reserver(client, concert, 1, 10);
        } catch (PlacesInsuffisantesException e) {
            System.out.println("Erreur réservation : " + e.getMessage());
        }
    }
}
