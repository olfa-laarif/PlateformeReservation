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

