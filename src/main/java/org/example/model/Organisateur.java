package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Organisateur extends Utilisateur {
    private List<Evenement> evenements = new ArrayList<>();

    public Organisateur(int id, String pseudo,String prenom,String nom, String email, String mdp) {
        super(id, pseudo,prenom,nom, email, mdp, "ORGANISATEUR");
    }

    public void ajouterEvenement(Evenement e) {
        evenements.add(e);
    }

    public List<Evenement> getEvenements() {
        return evenements;
    }
}
