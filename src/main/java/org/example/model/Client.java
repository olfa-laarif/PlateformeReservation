package org.example.model;


import java.util.ArrayList;
import java.util.List;

public class Client extends Utilisateur {
    private List<Reservation> historique = new ArrayList<>();

    public Client(int id, String pseudo,String prenom,String nom, String email, String mdp) {
        super(id, pseudo,prenom,nom, email, mdp, "CLIENT");
    }

    public void ajouterReservation(Reservation r) {
        historique.add(r);
    }

    public List<Reservation> getHistorique() {
        return historique;
    }
}

