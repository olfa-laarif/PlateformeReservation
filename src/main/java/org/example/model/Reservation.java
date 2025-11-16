package org.example.model;

import org.example.exception.AnnulationTardiveException;
import java.time.LocalDateTime;

public class Reservation {

    private int idReservation;
    private Client client;
    private Evenement evenement;
    private CategoriePlace categorie;
    private int nbPlaces;
    private LocalDateTime dateReservation;

    public Reservation(int id, Client client, Evenement event, CategoriePlace cat, int nbPlaces) {
        this.idReservation = id;
        this.client = client;
        this.evenement = event;
        this.categorie = cat;
        this.nbPlaces = nbPlaces;
        this.dateReservation = LocalDateTime.now();
    }


    public void annuler() throws AnnulationTardiveException {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limite = evenement.getDateEvent().atStartOfDay().minusHours(24);

        // Vérifie si on est à moins de 24h de l'événement
        if (now.isAfter(limite)) {
            throw new AnnulationTardiveException("Annulation impossible : moins de 24h avant l'événement.");
        }

        // On remet les places à la catégorie
        categorie.setPlacesRestantes(categorie.getPlacesRestantes() + nbPlaces);
    }


    // Getters

    public int getIdReservation() {
        return idReservation;
    }

    public Client getClient() {
        return client;
    }

    public Evenement getEvenement() {
        return evenement;
    }

    public CategoriePlace getCategorie() {
        return categorie;
    }

    public int getNbPlaces() {
        return nbPlaces;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }
}
