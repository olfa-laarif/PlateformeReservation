package org.example.model;

import java.time.LocalDateTime;
import java.util.List;

public class Conference extends Evenement {
    private String intervenant;

    public Conference(int id, String nom, LocalDateTime date, String lieu, Organisateur organisateur,
                      String intervenant, List<Place> places) {
        super(id, nom, date, lieu, organisateur);
        this.intervenant = intervenant;
        if (places != null) {
            this.places = places;
        }
    }

    @Override
    public String getSpecialGuest() {
        return intervenant;
    }

    public String getIntervenant() { return intervenant; }
    public void setIntervenant(String intervenant) { this.intervenant = intervenant; }
}
