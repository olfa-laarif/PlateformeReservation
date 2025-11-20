package org.example.dao;

import org.example.model.Paiement;
import org.example.util.Database;

import java.sql.*;
import java.time.LocalDateTime;

public class PaiementDAO {

    /**
     * @param id L'identifiant unique du paiement à rechercher (colonne {@code payment_id}).
     * @return Une instance de {@code Paiement} si l'enregistrement est trouvé et correctement mappé ;
     * {@code null} si aucun paiement n'existe pour cet ID ou si une erreur de base de données survient.
     **/
    public Paiement getPaiementById(int id) throws SQLException {

        // Construction de la requête
        String sql = "SELECT * FROM `payment` WHERE payment_id = ?";

        // Instantiation de la connection
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Saisie des valeurs manquantes de la requête preparée
            ps.setInt(1, id);

            System.out.println("Trying to get payment having id: " + id);

            // Execution de la requête
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    // Récupération de l'instance de la clase Reservation, attribut de la classe Paiement, via l'id de la réservation
                    //Reservation reservation = ReservationDAO.getReservationById(rs.getInt("reservation_id"));

                    // Mise en forme de la date récupérée de la BD pour la construction de l'instance de la classe Paiement
                    LocalDateTime datePaiement = rs.getTimestamp("payment_date").toLocalDateTime();

                    return new Paiement(
                            rs.getInt("payment_id"),
                            rs.getString("card_name"),
                            rs.getString("card_number"),
                            datePaiement,
                            null // ajouter Reservation quand il soit possible chercher une Reservation par id
                    );
                }
            } catch (Exception e) {
                System.err.println("Erreur dans la base de données au moment de chercher le paiement avec id : " + id);
                System.err.println("Détail de l'erreur : " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        System.out.println("Paiement non trouvé (id : " + id + ")");
        return null;
    }


    /**
     * @param reservation_id L'identifiant unique de la réservation (colonne {@code reservation_id})
     *                       dont on souhaite retrouver le paiement.
     * @return Une instance de {@code Paiement} si un paiement est trouvé pour cette réservation ;
     * {@code null} si aucun paiement n'existe ou si une erreur de base de données survient.
     * @throws SQLException Si une erreur d'accès ou de manipulation de la base de données survient.
     **/
    public Paiement getPaiementByIdReservation(int reservation_id) throws SQLException {

        // Construction de la requête
        String sql = "SELECT * FROM `payment` WHERE reservation_id = ?";

        // Instantiation de la connection
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Saisie des valeurs manquantes de la requête preparée
            ps.setInt(1, reservation_id);

            System.out.println("Trying to get payment having reservation_id: " + reservation_id);

            // Execution de la requête
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    // Récupération de l'instance de la clase Reservation, attribut de la classe Paiement, via l'id de la réservation
                    //Reservation reservation = ReservationDAO.getReservationById(rs.getInt("reservation_id"));

                    // Mise en forme de la date récupérée de la BD pour la construction de l'instance de la classe Paiement
                    LocalDateTime datePaiement = rs.getTimestamp("payment_date").toLocalDateTime();

                    return new Paiement(
                            rs.getInt("payment_id"),
                            rs.getString("card_name"),
                            rs.getString("card_number"),
                            datePaiement,
                            null // ajouter Reservation quand il soit possible chercher une Reservation par id
                    );
                }
            } catch (Exception e) {
                System.err.println("Erreur dans la base de données au moment de chercher le paiement avec l'id de réservation : " + /* reservation.getReservationId() */ "XXXX");
                System.err.println("Détail de l'erreur : " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        System.out.println("Paiement non trouvé (id de réservation : " + /* reservation.getReservationId() */ "XXXX" + ")");
        return null;
    }


    /**
     * @param paiement L'objet {@code Paiement} contenant les données à insérer. L'attribut ID de cet objet
     * est ignoré car il sera généré par la base de données. L'objet {@code Reservation} doit être valide.
     * @return Une nouvelle instance de {@code Paiement} contenant l'ID généré par la base de données,
     * ou {@code null} si l'insertion échoue (par exemple, si aucune ligne n'est affectée ou en cas d'erreur SQL).
     * @throws SQLException Si une erreur d'accès ou de manipulation de la base de données survient.
     **/
    public Paiement insert(Paiement paiement) throws SQLException {

        // Construction de la requête
        String sql = "INSERT INTO `payment` (card_name, card_number, payment_date, reservation_id) VALUES (?, ?, ?, ?)";

        // Instantiation de la connection
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (paiement.getReservation() == null || paiement.getReservation().getIdReservation() == 0) {
                System.err.println("Erreur d'insertion: L'objet Reservation est null ou son ID est manquant.");
                return null;
            }

            // Saisie des valeurs manquantes de la requête preparée
            ps.setString(1, paiement.getNomCB());
            ps.setString(2, paiement.getNumeroCB());
            ps.setInt(4, paiement.getReservation().getIdReservation());

            // Conversion de LocalDateTime à Timestamp
            ps.setTimestamp(3, Timestamp.valueOf(paiement.getDatePaiement()));

            System.out.println("Tentative d'insertion d'un nouveau paiement...");

            // Execution de la requête
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {

                // ** 4. Recuperación del ID autoincremental **
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);

                        System.out.println("Paiement inséré avec succès. ID généré: " + generatedId);

                        return new Paiement(
                                generatedId,
                                paiement.getNomCB(),
                                paiement.getNumeroCB(),
                                paiement.getDatePaiement(),
                                paiement.getReservation()
                        );
                    }
                }
            }

            // Execution de la requête
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    // Récupération de l'instance de la clase Reservation, attribut de la classe Paiement, via l'id de la réservation
                    //Reservation reservation = ReservationDAO.getReservationById(rs.getInt("reservation_id"));

                    // Mise en forme de la date récupérée de la BD pour la construction de l'instance de la classe Paiement
                    LocalDateTime datePaiement = rs.getTimestamp("payment_date").toLocalDateTime();

                    return new Paiement(
                            rs.getInt("payment_id"),
                            rs.getString("card_name"),
                            rs.getString("card_number"),
                            datePaiement,
                            null // ajouter Reservation quand il soit possible chercher une Reservation par id
                    );
                }
            }
            System.out.println("L'insertion n'a affecté aucune ligne. Retour de l'objet Paiement original (non mis à jour).");
            return null;

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'insertion du paiement : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
