package org.example.dao;

import org.example.model.*;
import org.example.util.Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *  - lire les événements complets (organisateur + places)
 *  - créer un événement et ses places associées
 */
public class EvenementDAO {

    private final CategoriePlaceDAO categoriePlaceDAO = new CategoriePlaceDAO();

    /**
     * Charge tous les événements, triés par date.
     */
    public List<Evenement> findAll() throws SQLException {
        String sql = """
                SELECT e.event_id,
                       e.name,
                       e.event_type,
                       e.special_guest,
                       e.event_date,
                       e.location,
                       e.organizer_id,
                       u.user_name,
                       u.first_name,
                       u.last_name,
                       u.email,
                       u.password
                FROM event e
                JOIN `user` u ON u.user_id = e.organizer_id
                ORDER BY e.event_date
                """;

        List<Evenement> evenements = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Evenement evenement = mapperEvenementComplet(rs, conn);
                evenements.add(evenement);
            }
        }

        return evenements;
    }

    /**
     * Transforme une ligne SQL en objet Evenement, puis charge les places liées.
     */
    private Evenement mapperEvenementComplet(ResultSet rs, Connection conn) throws SQLException {
        int eventId = rs.getInt("event_id");
        String type = rs.getString("event_type");
        String nom = rs.getString("name");
        String specialGuest = rs.getString("special_guest");
        LocalDateTime date = rs.getTimestamp("event_date").toLocalDateTime();
        String lieu = rs.getString("location");

        Organisateur organisateur = new Organisateur(
                rs.getInt("organizer_id"),
                rs.getString("user_name"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("password")
        );

        Evenement evenement = construireEvenement(type, eventId, nom, date, lieu, organisateur, specialGuest);
        List<Place> places = chargerPlaces(conn, eventId, evenement);
        evenement.setPlaces(places);
        return evenement;
    }

    /**
     * Utilise un switch simple pour instancier la bonne sous-classe.
     */
    private Evenement construireEvenement(String type, int id, String nom, LocalDateTime date,
                                          String lieu, Organisateur organisateur, String specialGuest) {
        return switch (type) {
            case "Spectacle" -> new Spectacle(id, nom, date, lieu, organisateur, specialGuest, null);
            case "Conference" -> new Conference(id, nom, date, lieu, organisateur, specialGuest, null);
            default -> new Concert(id, nom, date, lieu, organisateur, specialGuest, null);
        };
    }

    /**
     * Récupère toutes les places d'un événement (avec la catégorie et la disponibilité).
     */
    private List<Place> chargerPlaces(Connection conn, int eventId, Evenement evenement) throws SQLException {
        String sql = """
                SELECT p.place_id,
                       p.price,
                       c.category_id,
                       c.category_name,
                       CASE WHEN rhp.place_id IS NULL THEN 1 ELSE 0 END AS disponible
                FROM place p
                JOIN category c ON c.category_id = p.category_id
                LEFT JOIN reservation_has_place rhp ON rhp.place_id = p.place_id
                WHERE p.event_id = ?
                """;

        List<Place> places = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categorie categorie = new Categorie(rs.getInt("category_id"), rs.getString("category_name"));
                    Place place = new Place(rs.getInt("place_id"), rs.getDouble("price"), categorie, evenement);
                    place.setLibre(rs.getInt("disponible") == 1);
                    places.add(place);
                }
            }
        }

        return places;
    }

    /**
     * Enregistre un événement complet :
     *  - on crée d'abord la ligne dans la table event
     *  - puis on génère toutes les places (une ligne par place)
     * On encapsule le tout dans une transaction pour rester cohérent.
     */
    public void saveEvenement(Evenement evenement, List<CategoriePlaceDefinition> definitions) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try {
                int eventIdCree = insererEvenement(conn, evenement, definitions);
                evenement.setIdEvenement(eventIdCree);
                insererPlaces(conn, eventIdCree, definitions);
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private int insererEvenement(Connection conn, Evenement evenement, List<CategoriePlaceDefinition> definitions) throws SQLException {
        String sql = """
                INSERT INTO event (name, event_type, special_guest, event_date, location, organizer_id, seat_count)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, evenement.getNom());
            ps.setString(2, evenement.getTypeEvenement());
            ps.setString(3, evenement.getSpecialGuest());
            ps.setTimestamp(4, Timestamp.valueOf(evenement.getDateEvent()));
            ps.setString(5, evenement.getLieu());
            ps.setInt(6, evenement.getOrganisateur().getIdUser());
            ps.setInt(7, calculerNombreTotalPlaces(definitions));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Impossible de créer l'événement.");
    }

    private void insererPlaces(Connection conn, int eventId, List<CategoriePlaceDefinition> definitions) throws SQLException {
        String sql = "INSERT INTO place (price, category_id, event_id) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CategoriePlaceDefinition definition : definitions) {
                Categorie categorie = categoriePlaceDAO.findOrCreateByName(conn, definition.getNomCategorie());

                for (int i = 0; i < definition.getQuantite(); i++) {
                    ps.setDouble(1, definition.getPrix());
                    ps.setInt(2, categorie.getIdCategorie());
                    ps.setInt(3, eventId);
                    ps.addBatch();
                }
            }

            ps.executeBatch();
        }
    }

    private int calculerNombreTotalPlaces(List<CategoriePlaceDefinition> definitions) {
        int total = 0;
        for (CategoriePlaceDefinition definition : definitions) {
            total += definition.getQuantite();
        }
        return total;
    }
}
