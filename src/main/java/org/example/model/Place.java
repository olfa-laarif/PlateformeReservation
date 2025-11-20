package org.example.model;

public class Place {

    private int idPlace;
    private double prix;
    private Categorie categorie;
    private Evenement evenement;
    private boolean libre; // true si la place est disponible, false si réservée

    public Place(int id, double prix, Categorie categorie, Evenement evenement) {
        this.idPlace = id;
        this.prix = prix;
        this.categorie = categorie;
        this.evenement = evenement;
        this.libre = true; // par défaut, la place est libre
    }
    // Getters et setters
    public int getIdPlace() { return idPlace; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }
    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }
    public boolean isLibre() { return libre; }
    public void setLibre(boolean libre) { this.libre = libre; }

    // Vérifie si la place est disponible
    public boolean estDisponible() {
        return libre;
    }

    // Réserver la place
    public void reserver() {
        this.libre = false;
    }

    // Libérer la place
    public void liberer() {
        this.libre = true;
    }
}
