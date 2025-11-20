package org.example.model;

import org.example.dao.PaiementDAO;
import org.example.exception.PaiementInvalideException;

import java.time.LocalDateTime;

public class Paiement implements Payable {

    // ------------------------------------------------------------------------------------------------------
    // Attributs
    private int id;
    private String nomCB;
    private String numeroCB;
    private LocalDateTime datePaiement;
    private Reservation reservation;

    // ------------------------------------------------------------------------------------------------------
    // Getters
    public int getId(){ return this.id; }
    public String getNomCB(){ return this.nomCB; }
    public String getNumeroCB(){ return this.numeroCB; }
    public LocalDateTime getDatePaiement(){ return this.datePaiement; }
    public Reservation getReservation(){ return this.reservation; }

    // ------------------------------------------------------------------------------------------------------
    // Setters
    public void setId(int id){ this.id = id; }
    public void setNomCB(String nomCB){ this.nomCB = nomCB; }
    public void setNumeroCB(String numeroCB){ this.numeroCB = numeroCB; }
    public void setDatePaiement(LocalDateTime datePaiement){ this.datePaiement = datePaiement; }
    public void setReservation(Reservation reservation){ this.reservation = reservation; }

    // ------------------------------------------------------------------------------------------------------
    // Autres méthodes

    /**
     * Constructeur complet pour créer une instance de {@code Paiement} existante, généralement
     * à partir de données récupérées de la base de données.
     *
     * @param id L'identifiant unique du paiement.
     * @param nomCB Le nom du titulaire de la carte bancaire.
     * @param numeroCB Le numéro de la carte bancaire (doit contenir 16 chiffres).
     * @param datePaiement La date et l'heure exactes où le paiement a été effectué.
     * @param reservation L'objet {@code Reservation} associé à ce paiement.
     * **/
    public Paiement(int id, String nomCB, String numeroCB, LocalDateTime datePaiement, Reservation reservation) {
        this.id = id;
        this.nomCB = nomCB;
        this.numeroCB = numeroCB;
        this.datePaiement = datePaiement;
        this.reservation = reservation;
    }


    /**
     * Constructeur utilisé pour créer une nouvelle instance de {@code Paiement}
     * avant l'insertion dans la base de données.
     * <p>
     * L'identifiant du paiement sera généré par la base de données (auto-incrément).
     * La date de paiement est automatiquement définie à l'heure actuelle du système ({@code LocalDateTime.now()}).
     *
     * @param nomCB Le nom du titulaire de la carte bancaire.
     * @param numeroCB Le numéro de la carte bancaire (doit contenir 16 chiffres).
     * @param reservation L'objet {@code Reservation} auquel ce paiement est lié.
     * **/
    public Paiement(String nomCB, String numeroCB, Reservation reservation) {
        this.nomCB = nomCB;
        this.numeroCB = numeroCB;
        this.reservation = reservation;
        this.datePaiement = LocalDateTime.now();
    }


    /**
     * Tente d'effectuer le paiement après avoir validé les données.
     * <p>
     * Ce méthode vérifie d'abord la validité des informations de paiement. Si la validation réussit,
     * elle procède à une série de contrôles logiques avant de tenter l'insertion en base de données.
     *
     * @return {@code 0} si le paiement est réussi et inséré en base de données.
     * {@code -1} si l'objet {@code Reservation} associé au paiement est {@code null} (vérification de dépendance critique).
     * {@code -2} si l'insertion en base de données échoue (via {@code PaiementDAO.insert()}).
     * @throws PaiementInvalideException Si une donnée de paiement (nom, numéro de carte, montant) est invalide.
     * **/
    public int effectuerPaiement() throws PaiementInvalideException {

        // vérification des données de paiement
        try {
            this.verifierDonneesDePaiement();
        } catch (Exception e) {
            System.err.println("Erreur au moment d'effectuer un paiement (id de réservation : " + this.getReservation().getIdReservation() + ")");
            System.err.println(e.getMessage());
            throw e;
        }

        // vérification de l'objet Reservation
        Object foo = new Object();
        if ( this.reservation == null){
            return -1;
        }

        // insertion du paiement
        try {
            PaiementDAO paiementDAO = new PaiementDAO();
            Paiement addedPaiement = paiementDAO.insert(this);

            if (addedPaiement != null) {
                // paiement réussi
                return 0;
            } else {
                // erreur dans l'insertion du nouveau paiement
                return -2;
            }
        } catch (Exception e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            return -2;
        }
    }


    /**
     * Vérifie la validité des données de paiement avant toute tentative d'insertion en base de données.
     * <p>
     * Les contrôles effectués incluent :
     * <ul>
     * <li>La présence du nom du titulaire de la carte (non vide, au moins 3 charactères et que des lettres, espaces ou "-").</li>
     * <li>Le format du numéro de carte bancaire (exactement 16 chiffres numériques).</li>
     * <li>La validité du montant total de la réservation (doit être supérieur à zéro).</li>
     * </ul>
     *
     * @throws PaiementInvalideException Si une des données de paiement est invalide ou manquante.
     */
    public void verifierDonneesDePaiement() throws PaiementInvalideException {

        // vérification du nom du titulaire de la CB
        String trimmedName = this.nomCB != null ? this.nomCB.trim() : "";
        if (!trimmedName.matches("[a-zA-Z\\s-]{3,}")) {
            throw new PaiementInvalideException("Le nom du titulaire est requis.");
        }

        // vérification du numéro de la CB
        if (!this.numeroCB.matches("\\d{16}")) {
            throw new PaiementInvalideException("Le numéro de carte doit contenir 16 chiffres.");
        }

        // vérification du montant de la réservation
        if (this.reservation.calculateTotalPrice() <= 0){
            throw new PaiementInvalideException("Le montant à payer pour la réservation numéro "
                    + this.reservation.getIdReservation() + " est erroné.");
        }
    }
}
