package org.example.model;


import java.time.LocalDate;

public class Spectacle extends Evenement {
    private String troupe;

    public Spectacle(int id, String nom, LocalDate date, String lieu, String troupe) {
        super(id, nom, date, lieu);
        this.troupe = troupe;
    }

    @Override
    public String getSpecialGuest() {
        return troupe;
    }
}



