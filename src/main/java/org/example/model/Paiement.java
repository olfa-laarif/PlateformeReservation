package org.example.model;

import org.example.exception.PaiementInvalideException;

import java.time.LocalDateTime;

public class Paiement implements Payable {

    private int idPaiement;
    private Reservation reservation;
    private String nomPorteur;
    private String numeroCarte;
    private double montant;
    private LocalDateTime datePaiement;

    public Paiement(int id, Reservation reservation, String nomPorteur, String numeroCarte) {
        this.idPaiement = id;
        this.reservation = reservation;
        this.nomPorteur = nomPorteur;
        this.numeroCarte = numeroCarte;
        this.montant = reservation.getNbPlaces() * reservation.getCategorie().getPrix();
        this.datePaiement = LocalDateTime.now();
    }

    @Override
    public void payer() throws PaiementInvalideException {
        if (numeroCarte == null || numeroCarte.length() < 10) {
            throw new PaiementInvalideException("Numéro de carte invalide !");
        }
    }
}

