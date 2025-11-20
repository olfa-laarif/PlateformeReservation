package org.example.model;

import org.example.exception.AnnulationTardiveException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Modèle représentant une réservation (client, événement, places et date).
 * Sert de support aux vues et aux services métier.
 */
    public class Reservation {

        private int idReservation;
        private Client client;
        private Evenement evenement;
        private LocalDateTime dateReservation;
        private List<Place> places; // Liste des places réservées

        /**
         * Construit une réservation avec les éléments nécessaires.
         */
        public Reservation(int idReservation, Client client, Evenement evenement, List<Place> places, LocalDateTime dateReservation) {
            this.idReservation = idReservation;
            this.client = client;
            this.evenement = evenement;
            this.places = places;
            this.dateReservation = dateReservation;
        }

        /**
         * Annule la réservation si la limite de 24h est respectée puis libère les places.
         * @throws AnnulationTardiveException si la date limite est dépassée
         */
        public void annuler() throws AnnulationTardiveException {
            LocalDateTime now = LocalDateTime.now();

            if (evenement.getDateEvent().minusHours(24).isBefore(now)){
                throw new AnnulationTardiveException("Annulation impossible : moins de 24h avant l'événement.");
            }

            // Libérer toutes les places de la réservation
            if (places != null) {
                for (Place place : places) {
                    place.liberer();
                }
            }

            System.out.println("Réservation annulée pour l'événement " + evenement.getNom());
        }


        /**
         * Calcule le montant total estimé à partir des places.
         * @return total agrégé
         */
        public double calculateTotalPrice() {
            // Pas de places
            if (this.places == null || this.places.isEmpty()) {
                return 0.0;
            }

            return this.places.size() * this.places.getFirst().getPrix();
        }


        // Getters et setters
        public int getIdReservation() { return idReservation; }
        public Client getClient() { return client; }
        public Evenement getEvenement() { return evenement; }
        public LocalDateTime getDateReservation() { return dateReservation; }
        public List<Place> getPlaces() { return places; }
        public void setPlaces(List<Place> places) { this.places = places; }
    }

