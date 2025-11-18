package org.example.model;

import java.util.HashMap;
import java.util.Map;

/**
 * On stocke seulement trois informations faciles à afficher dans l'interface :
 *  - nombre total de tickets vendus
 *  - chiffre d'affaires généré
 *  - pour chaque catégorie, le pourcentage de places occupées
 */
public class EvenementStats {

    private final int totalTicketsVendues;
    private final double chiffreAffaires;
    private final Map<String, Double> tauxRemplissageParCategorie;

    public EvenementStats(int totalTicketsVendues, double chiffreAffaires,
                          Map<String, Double> tauxRemplissageParCategorie) {
        this.totalTicketsVendues = totalTicketsVendues;
        this.chiffreAffaires = chiffreAffaires;

        // On copie la map reçue pour éviter les surprises si on la modifie ailleurs.
        if (tauxRemplissageParCategorie == null) {
            this.tauxRemplissageParCategorie = new HashMap<>();
        } else {
            this.tauxRemplissageParCategorie = new HashMap<>(tauxRemplissageParCategorie);
        }
    }

    public int getTotalTicketsVendues() {
        return totalTicketsVendues;
    }

    public double getChiffreAffaires() {
        return chiffreAffaires;
    }

    public Map<String, Double> getTauxRemplissageParCategorie() {
        return tauxRemplissageParCategorie;
    }
}