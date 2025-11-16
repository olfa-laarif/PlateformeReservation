package org.example.model;

import java.time.LocalDate;

public class Conference extends Evenement {
    private String intervenant;

    public Conference(int id, String nom, LocalDate date, String lieu, String intervenant) {
        super(id, nom, date, lieu);
        this.intervenant = intervenant;
    }

    @Override
    public String getSpecialGuest() {
        return intervenant;
    }
}

