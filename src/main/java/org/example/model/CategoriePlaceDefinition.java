package org.example.model;

public class CategoriePlaceDefinition {

    private final String nomCategorie;
    private final double prix;
    private final int quantite;

    public CategoriePlaceDefinition(String nomCategorie, double prix, int quantite) {
        this.nomCategorie = nomCategorie;
        this.prix = prix;
        this.quantite = quantite;
    }

    public String getNomCategorie() {
        return nomCategorie;
    }

    public double getPrix() {
        return prix;
    }

    public int getQuantite() {
        return quantite;
    }

    @Override
    public String toString() {
        return nomCategorie + " - " + prix + " € (" + quantite + " places)";
    }
}
