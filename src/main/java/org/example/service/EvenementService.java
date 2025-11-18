package org.example.service;

import org.example.model.CategoriePlace;
import org.example.model.Concert;
import org.example.model.Evenement;
import org.example.model.Spectacle;
import org.example.model.Conference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service simple en mémoire pour fournir des événements de démonstration.
 */
public class EvenementService {

	private final List<Evenement> events = new ArrayList<>();

	public EvenementService() {
		// crée des événements de démonstration avec catégories
		Concert c1 = new Concert(1, "Rock Night", LocalDate.now().plusDays(10), "Stade", "The Band");
		CategoriePlace vip = new CategoriePlace(1, "VIP", 120.0, 10);
		CategoriePlace gradin = new CategoriePlace(2, "Gradin", 50.0, 200);
		c1.ajouterCategorie(vip);
		c1.ajouterCategorie(gradin);

		Conference conf = new Conference(2, "Tech Talk", LocalDate.now().plusDays(20), "Centre Conf","wassila");
		CategoriePlace plein = new CategoriePlace(3, "Standard", 20.0, 150);
		conf.ajouterCategorie(plein);

		Spectacle s = new Spectacle(3, "Cirque", LocalDate.now().plusDays(5), "Théâtre","katia");
		CategoriePlace fosse = new CategoriePlace(4, "Fosse", 70.0, 80);
		s.ajouterCategorie(fosse);

		events.add(c1);
		events.add(conf);
		events.add(s);
	}

	public List<Evenement> getAllEvents() {
		return events;
	}
}
