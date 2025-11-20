package org.example.model;

import java.time.LocalDateTime;
import java.util.List;

public class Concert extends Evenement {
    private String artiste;

    public Concert(int id, String nom, LocalDateTime date, String lieu, Organisateur organisateur,
                   String artiste, List<Place> places) {
        super(id, nom, date, lieu, organisateur);
        this.artiste = artiste;
        if (places != null) {
            this.places = places;
        }
    }

    @Override
    public String getSpecialGuest() {
        return artiste;
    }

    // Getter et setter
    public String getArtiste() { return artiste; }
    public void setArtiste(String artiste) { this.artiste = artiste; }
}
