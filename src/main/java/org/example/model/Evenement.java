package org.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Evenement {

    protected int idEvenement;
    protected String nom;
    protected LocalDateTime dateEvenement;
    protected String lieu;
    protected Organisateur organisateur;

    // Liste des places disponibles pour cet événement
    protected List<Place> places = new ArrayList<>();

    public Evenement(int idEvenement, String nom, LocalDateTime dateEvenement, String lieu, Organisateur organisateur) {
        this.idEvenement = idEvenement;
        this.nom = nom;
        this.dateEvenement = dateEvenement;
        this.lieu = lieu;
        this.organisateur = organisateur;
    }

    // Chaque sous-classe définit l'artiste ou l'intervenant
    public abstract String getSpecialGuest();

    // Ajouter une place à l'événement
    public void ajouterPlace(Place place) {
        places.add(place);
    }

    // Retourne la liste des places
    public List<Place> getPlaces() {
        return places;
    }

    // Calcul du chiffre d'affaires total
    public double getTotalVentes() {
        double total = 0;
        for (Place p : places) {
            if (!p.estDisponible()) { // si la place est réservée
                total += p.getPrix();
            }
        }
        return total;
    }

    // Calcul du taux de remplissage
    public double getTauxRemplissage() {
        if (places.isEmpty()) return 0;
        long placesReservees = places.stream().filter(p -> !p.estDisponible()).count();
        return (placesReservees * 100.0) / places.size();
    }

    // Getters et setters
    public int getIdEvenement() { return idEvenement; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public LocalDateTime getDateEvent() { return dateEvenement; }
    public void setDateEvent(LocalDateTime dateEvent) { this.dateEvenement = dateEvent; }
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public Organisateur getOrganisateur() { return organisateur; }
    public void setOrganisateur(Organisateur organisateur) { this.organisateur = organisateur; }
}
