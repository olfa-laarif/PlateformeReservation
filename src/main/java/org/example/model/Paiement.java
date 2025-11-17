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
