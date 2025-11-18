package org.example.model;

import java.time.LocalDateTime;
import java.util.List;

public class Spectacle extends Evenement {
    private String troupe;

    public Spectacle(int id, String nom, LocalDateTime date, String lieu, Organisateur organisateur,
                     String troupe, List<Place> places) {
        super(id, nom, date, lieu, organisateur);
        this.troupe = troupe;
        if (places != null) {
            this.places = places;
        }
    }

    @Override
    public String getSpecialGuest() {
        return troupe;
    }

    public String getTroupe() { return troupe; }
    public void setTroupe(String troupe) { this.troupe = troupe; }
}
