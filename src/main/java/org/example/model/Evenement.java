package org.example.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Evenement {
    protected int idEvent;
    protected String nom;
    protected LocalDate dateEvent;
    protected String lieu;

    protected List<CategoriePlace> categories = new ArrayList<>();

    public Evenement(int id, String nom, LocalDate date, String lieu) {
        this.idEvent = id;
        this.nom = nom;
        this.dateEvent = date;
        this.lieu = lieu;
    }

    public abstract String getSpecialGuest();

    public void ajouterCategorie(CategoriePlace categorie) {
        categories.add(categorie);
    }

    public List<CategoriePlace> getCategories() {
        return categories;
    }

    public double getTotalVentes() {
    //TODO
        return 0;
    }

    public double getTauxRemplissage() {
        //TODO
        return 0;
    }

    // Getters et setters
    public int getIdEvent() { return idEvent; }
    public String getNom() { return nom; }
    public LocalDate getDateEvent() { return dateEvent; }
    public String getLieu() { return lieu; }

    public void setNom(String nom) { this.nom = nom; }
    public void setDateEvent(LocalDate dateEvent) { this.dateEvent = dateEvent; }
    public void setLieu(String lieu) { this.lieu = lieu; }


}

