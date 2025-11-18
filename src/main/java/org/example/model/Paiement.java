package org.example.model;

import org.example.exception.PaiementInvalideException;

import java.time.LocalDateTime;

public class Paiement implements Payable {

        private int id;
        private String nomCarte;
        private String numeroCarte;
        private double montant;   // nouveau champ
        private LocalDateTime datePaiement;
        private Reservation reservation;

        public Paiement(int id, String nomCarte, String numeroCarte, Reservation reservation) {
            this.id = id;
            this.nomCarte = nomCarte;
            this.numeroCarte = numeroCarte;
            this.montant = reservation.getPlaces().stream().mapToDouble(Place::getPrix).sum(); // enregistrement du montant payé
            this.datePaiement = LocalDateTime.now();
            this.reservation = reservation;
        }

    @Override
    public void payer() throws PaiementInvalideException {
        if (numeroCarte == null || numeroCarte.length() < 10) {
            throw new PaiementInvalideException("Numéro de carte invalide !");
        }
    }
}

    public static void verifierDonneesDePaiement(String nom, String numeroCarte, double montant, int idReservation) throws PaiementInvalideException {

        // vérification du nom du titulaire de la CB
        if (nom.isEmpty()) {
            throw new PaiementInvalideException("Le nom du titulaire est requis.");
        }

        // vérification du numéro de la CB
        if (!numeroCarte.matches("\\d{16}")) {
            throw new PaiementInvalideException("Le numéro de carte doit contenir 16 chiffres.");
        }

        // vérification du montant saisit pour la réservation
        double montantTotalReservation;
        /*
        * Récuperation de la réservation de la BD par idReservation
        * + calcul du montant total
        * + comparaison avec le montant à payer
        * */
        if (montant < 0 || montant !== montantTotalReservation){
            throw new PaiementInvalideException("Le montant à payer pour la réservation numéro "
                    + idReservation + " est erroné.");
        }
    }
