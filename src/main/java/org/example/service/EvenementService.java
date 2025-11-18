package org.example.service;

import org.example.dao.CategoriePlaceDAO;
import org.example.dao.EvenementDAO;
import org.example.model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Le service fait le lien entre l'interface (JavaFX) et la couche DAO.
 */
public class EvenementService {

    private final EvenementDAO evenementDAO = new EvenementDAO();
    private final CategoriePlaceDAO categoriePlaceDAO = new CategoriePlaceDAO();

    /**
     * Retourne la liste des événements à afficher.
     */
    public List<Evenement> chargerEvenements() throws SQLException {
        return evenementDAO.findAll();
    }

    /**
     * Récupère les catégories de places connues (VIP, Standard, etc.).
     */
    public List<Categorie> chargerCategories() throws SQLException {
        return categoriePlaceDAO.findAll();
    }

    /**
     * Demande au DAO de créer un événement ainsi que toutes ses places.
     */
    public void creerEvenement(Evenement evenement, List<CategoriePlaceDefinition> definitions) throws SQLException {
        evenementDAO.saveEvenement(evenement, definitions);
    }

    /**
     * Parcourt toutes les places d'un événement et calcule :
     *  - le nombre total vendu
     *  - le chiffre d'affaires
     *  - le taux de remplissage par catégorie
     */
    public EvenementStats calculerStatistiques(Evenement evenement) {
        if (evenement == null) {
            return new EvenementStats(0, 0, new HashMap<>());
        }

        int totalVendues = 0;
        double chiffreAffaires = 0;
        Map<String, Integer> totalParCategorie = new HashMap<>();
        Map<String, Integer> venduesParCategorie = new HashMap<>();

        List<Place> places = evenement.getPlaces();
        if (places == null) {
            places = new ArrayList<>();
        }

        for (Place place : places) {
            String categorie = place.getCategorie().getNomCategorie();

            // On incrémente le nombre total de places de cette catégorie.
            int totalPourCategorie = totalParCategorie.getOrDefault(categorie, 0);
            totalParCategorie.put(categorie, totalPourCategorie + 1);

            // Si la place est vendue, on actualise les indicateurs.
            if (!place.estDisponible()) {
                int venduesPourCategorie = venduesParCategorie.getOrDefault(categorie, 0);
                venduesParCategorie.put(categorie, venduesPourCategorie + 1);

                totalVendues++;
                chiffreAffaires += place.getPrix();
            }
        }

        Map<String, Double> tauxParCategorie = new HashMap<>();
        for (Map.Entry<String, Integer> entry : totalParCategorie.entrySet()) {
            String categorie = entry.getKey();
            int total = entry.getValue();
            int vendues = venduesParCategorie.getOrDefault(categorie, 0);

            double taux;
            if (total == 0) {
                taux = 0;
            } else {
                taux = (vendues * 100.0) / total;
            }

            tauxParCategorie.put(categorie, taux);
        }

        return new EvenementStats(totalVendues, chiffreAffaires, tauxParCategorie);
    }
}
