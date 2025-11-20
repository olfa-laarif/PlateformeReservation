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

    public void setPlaces(List<Place> places) {
        this.places.clear();
        if (places != null) {
            this.places.addAll(places);
        }
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

    public void setIdEvenement(int idEvenement) {
        this.idEvenement = idEvenement;
    }

    public String getTypeEvenement() {
        return getClass().getSimpleName();
    }

    public long getNombrePlacesReservees() {
        return places.stream().filter(p -> !p.estDisponible()).count();
    }

    public long getNombrePlacesDisponibles() {
        return places.stream().filter(Place::estDisponible).count();
    }

    public int getCapaciteTotale() {
        return places.size();
    }
}