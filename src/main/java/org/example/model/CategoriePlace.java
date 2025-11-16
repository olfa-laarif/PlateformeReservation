package org.example.model;

import org.example.exception.AnnulationTardiveException;
import org.example.exception.PlacesInsuffisantesException;

public class CategoriePlace implements Reservable {
    private int idCategorie;
    private String nomCategorie;
    private double prix;
    private int placesTotales;
    private int placesRestantes;

    public CategoriePlace(int id, String nom, double prix, int total) {
        this.idCategorie = id;
        this.nomCategorie = nom;
        this.prix = prix;
        this.placesTotales = total;
        this.placesRestantes = total;
    }


    public double getPrix() {
        return prix;
    }

    public int getPlacesTotales() {
        return placesTotales;
    }

    public int getPlacesRestantes() {
        return placesRestantes;
    }

    public void setPlacesRestantes(int placesRestantes) {
        this.placesRestantes = placesRestantes;
    }

    @Override
    public void reserver(int nombreTickets) throws PlacesInsuffisantesException {
        if (nombreTickets > placesRestantes) {
            throw new PlacesInsuffisantesException("Pas assez de places disponibles.");
        }
        placesRestantes -= nombreTickets;
    }

}

