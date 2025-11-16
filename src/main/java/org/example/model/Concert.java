package org.example.model;

import java.time.LocalDate;

public class Concert extends Evenement {
    private String artiste;

    public Concert(int id, String nom, LocalDate date, String lieu, String artiste) {
        super(id, nom, date, lieu);
        this.artiste = artiste;
    }

    @Override
    public String getSpecialGuest() {
        return artiste;
    }
}