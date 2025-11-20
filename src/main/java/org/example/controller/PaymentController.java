package org.example.controller;

import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.exception.AnnulationTardiveException;
import org.example.model.Paiement;
import javafx.fxml.FXML;
import org.example.exception.PaiementInvalideException;
import org.example.model.Reservation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.UnaryOperator;

public class PaymentController {

    @FXML
    private TextField cbField;

    @FXML
    private TextField nomPropietaireCbField;

    @FXML
    private Label idReservationLabel ;

    @FXML
    private Label montantLabel;

    @FXML
    private Button payerButton;

    @FXML
    private Button annulerButton;

    private Reservation currentReservation;


    /**
     * Initialise le contrôleur après le chargement du FXML.
     * Cette méthode est appelée automatiquement par le FXMLLoader.
     * <p>
     * Elle exécute les tâches d'initialisation suivantes:
     * <ul>
     * <li>Configure les masques de saisie (validation des caractères) pour le numéro de carte bancaire ({@code cbField}) et le nom du titulaire ({@code nomPropietaireCbField}) en appelant les méthodes {@code setupCreditCardMasking()} et {@code setupNameMasking()}.</li>
     * <li>Définit l'action (gestionnaire d'événements) des boutons {@code payerButton} et {@code annulerButton} en les liant aux méthodes {@code effectuerPaiement()} et {@code annulerPaiement()}.</li>
     * <li>Affiche des messages d'erreur critiques si les boutons requis ne sont pas injectés par le FXML.</li>
     * </ul>
     */
    @FXML
    public void initialize() {

        // =================================================================================
        // Initialisation des masques de saisie
        setupCreditCardMasking();
        setupNameMasking();

        // =================================================================================
        // Definition de l'action des boutons

        if (payerButton != null) {
            payerButton.setOnAction(e -> effectuerPaiement());
        } else {
            System.err.println("ERREUR CRITIQUE: Le bouton 'effectuerPaiement' n'a pas été injecté par FXML. Le FXML de paiement pourrait être ancien.");
        }

        if (annulerButton != null) {
            annulerButton.setOnAction(e -> annulerPaiement());
        } else {
            System.err.println("ERREUR CRITIQUE: Le bouton 'annulerButton' n'a pas été injecté par FXML. Le FXML de paiement pourrait être ancien.");
        }
    }


    /**
     * Méthode d'initialisation du contrôleur de paiement.
     * Cette version est principalement utilisée pour des tests ou des scénarios où
     * la réservation doit être chargée à partir de son identifiant et des détails
     * de base sont passés séparément.
     * <p>
     * NOTE: L'implémentation actuelle crée un objet {@code Reservation} incomplet
     * (sans son {@code Evenement} associé), ce qui peut entraîner des exceptions
     * (comme {@code NullPointerException}) si des méthodes dépendant de l'événement
     * (telles que {@code annuler()}) sont appelées.
     *
     * @param idReservation L'identifiant unique de la réservation à récupérer ou à créer.
     * @param montant Le montant total à afficher pour le paiement (utilisé pour renseigner le label).
     */
    public void initialiserDonnees(int idReservation, double montant) {
        try {
            // Récupération de la Réservation dans la BD à partir de son idReservation
            // this.currentReservation = reservationDAO.getReservationById(idReservation);
            // Classe ReservationDAO -> fonction getReservationById ou équivalent non implementée
            this.currentReservation = new Reservation(idReservation, null, null, null, LocalDateTime.now());

            if (this.currentReservation == null) {
                new Alert(Alert.AlertType.ERROR, "La réservation " + idReservation + " n'a pas été trouvée.").showAndWait();
                return;
            }

            // Renseigner le texte des camps de la view
            idReservationLabel.setText(String.valueOf(this.currentReservation.getIdReservation()));
            montantLabel.setText(String.valueOf(montant));

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la récupération de la réservation: " + e.getMessage()).showAndWait();
        }
    }


    /**
     * Initialise le contrôleur avec un objet {@code Reservation} déjà créé et potentiellement complet.
     * Cette méthode est le point d'entrée principal pour le paiement d'une réservation qui vient d'être
     * enregistrée ou récupérée.
     * <p>
     * Le processus garantit l'affectation correcte de la réservation à {@code currentReservation}
     * avant de mettre à jour les éléments de l'interface utilisateur (ID de réservation et montant total).
     *
     * @param reservation L'objet Reservation complet à traiter pour le paiement.
     */
    public void initialiserDonnees(Reservation reservation) {
        try {
            this.currentReservation = reservation;
            // Renseignement des inputs de la view idReservation et montant
            idReservationLabel.setText(String.valueOf(this.currentReservation.getIdReservation()));
            montantLabel.setText(String.valueOf(this.currentReservation.calculateTotalPrice()));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la récupération de la réservation: " + e.getMessage()).showAndWait();
        }
    }


    /**
     * Tente d'effectuer le paiement en utilisant les données saisies dans le formulaire.
     * <p>
     * Le processus inclut les étapes suivantes :
     * <ul>
     * <li>Vérification qu'une réservation courante est bien présente.</li>
     * <li>Validation des champs du formulaire (longueur minimale du nom, format du numéro de carte).</li>
     * <li>Création et exécution de la logique de paiement via l'objet {@code Paiement}.</li>
     * <li>Affichage d'une alerte de succès ou d'échec.</li>
     * <li>En cas de succès, la fenêtre de paiement est fermée après la confirmation de l'utilisateur.</li>
     * </ul>
     * Des alertes d'erreur sont affichées en cas de validation incorrecte ({@code PaiementInvalideException})
     * ou d'échec de la logique métier.
     */
    private void effectuerPaiement() {

        // Vérifier la Reservation avant de continuer
        if (this.currentReservation == null) {
            new Alert(Alert.AlertType.ERROR, "Erreur critique : Aucune réservation n'est liée à cet écran de paiement.").showAndWait();
            return;
        }

        try {
            // Création de l'instance de Paiement avec les données récupérées du formulaire + la Reservation en attribut
            Paiement paiement = new Paiement(
                    nomPropietaireCbField.getText(),
                    cbField.getText(),
                    this.currentReservation
            );

            // Tentative d'enregistrement du paiement
            int reponsePaiement = paiement.effectuerPaiement();

            if (reponsePaiement != 0) {
                // La paiement a échoué (code -1, -2, -3)
                String errorMessage = getErrorMessage(reponsePaiement);

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Paiement échoué");
                alert.setHeaderText("Votre paiement n'a pas été validé !");
                alert.setContentText(
                        "Raison: " + errorMessage +
                                "\nRéservation : " + this.currentReservation.getIdReservation() +
                                "\nMontant : " + this.currentReservation.calculateTotalPrice() + " €"
                );
                alert.showAndWait();
            } else {
                // Succès : affichage d'un message de confirmation
                String contenuRecu = ticketMessageBuilt();

                // Affichage du message de confirmation avec détails
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Paiement réussi");
                alert.setHeaderText("Confirmation de votre Réservation et Paiement");
                alert.setContentText(contenuRecu);
                alert.showAndWait();

                // On ferme la fenetre (Stage)
                Stage stage = (Stage) payerButton.getScene().getWindow();
                stage.close();
            }

        } catch (PaiementInvalideException ex) {
            new Alert(Alert.AlertType.ERROR, "Erreur de validation des données : " + ex.getMessage()).showAndWait();

        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erreur inattendue : " + ex.getMessage()).showAndWait();
        }
    }


    /**
     * Méthode utilitaire pour traduire les codes d'erreur numériques du processus de paiement
     * en messages d'erreur textuels lisibles par l'utilisateur.
     *
     * @param code Le code d'erreur entier retourné par la fonction {@code effectuerPaiement()} de la classe {@code Paiement}.
     * @return Un message d'erreur descriptif correspondant au code, ou un message générique si le code n'est pas reconnu.
     */
    private String getErrorMessage(int code) {
        return switch (code) {
            case -1 -> "Données de carte invalides (Nom, numéro, etc.).";
            case -2 -> "Erreur lors de l'insertion en base de données.";
            default -> "Échec du paiement (Code: " + code + ").";
        };
    }


    /**
     * Gère l'annulation du processus de paiement par l'utilisateur (généralement via le bouton 'Annuler').
     *
     * Cette méthode tente d'abord d'annuler et de supprimer la réservation temporaire créée
     * en appelant {@code deleteReservationHandlerer()}.
     *
     * Si l'annulation de la réservation échoue (c'est-à-dire si {@code deleteReservationHandlerer()}
     * retourne {@code false}), la méthode se termine immédiatement pour permettre à l'utilisateur
     * de rester sur l'écran de paiement.
     *
     * Si l'annulation de la réservation réussit, la fenêtre de paiement (Stage) est fermée
     * et un message est affiché dans le journal.
     */
    private void annulerPaiement() {

        // Supprimer la réservation crée
        if(!this.deleteReservationHandlerer()) return;
        
        // Capturer la fenetre (Stage) du bouton qui a déclenché l'action
        Stage stage = (Stage) annulerButton.getScene().getWindow();

        // Affichage d'un message dans le log
        System.out.println("Paiement annulé par l'utilisateur.");

        // Fermer la fenetre
        stage.close();
    }


    /**
     * Tente d'annuler la réservation actuellement stockée dans le contrôleur.
     *
     * Affiche des alertes à l'utilisateur si l'annulation échoue,
     * que ce soit pour une annulation tardive (avertissement) ou pour toute autre erreur
     * inattendue (erreur critique).
     *
     * @return {@code true} si l'annulation de la réservation a réussi.
     * {@code false} si l'annulation a échoué à cause d'une {@code AnnulationTardiveException}
     * ou toute autre {@code Exception}.
     */
    private boolean deleteReservationHandlerer(){
        try {
            System.out.println(this.currentReservation.getDateReservation());
            this.currentReservation.annuler();
            return true;
        } catch (AnnulationTardiveException ate) {
            new Alert(Alert.AlertType.WARNING, ate.getMessage(), ButtonType.OK).showAndWait();
            return false;
        } catch (Exception e){
            new Alert(Alert.AlertType.ERROR, "Erreur: " + e.getMessage(), ButtonType.OK).showAndWait();
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Applique un masque de saisie ({@code TextFormatter}) au champ de texte du numéro de carte bancaire (CB).
     * <p>
     * Le masque implémente les contraintes suivantes en temps réel:
     * <ul>
     * <li>N'accepte que des caractères numériques (chiffres de 0 à 9).</li>
     * <li>Limite la longueur maximale de la saisie à 16 caractères.</li>
     * </ul>
     */
    private void setupCreditCardMasking() {
        // Masque de saisie pour le numéro de CB (que de chiffres, max 16)
        if (cbField != null) {
            UnaryOperator<TextFormatter.Change> cbFilter = change -> {
                String newText = change.getControlNewText();
                if (newText.length() > 16) {
                    return null; // Rechazar si excede 16 caracteres
                }
                if (newText.matches("\\d*")) {
                    return change; // Aceptar si es solo dígitos
                }
                return null; // Rechazar cualquier otra cosa
            };
            cbField.setTextFormatter(new TextFormatter<>(cbFilter));
        }
    }


    /**
     * Applique un masque de saisie ({@code TextFormatter}) au champ de texte du nom du titulaire de la CB.
     * <p>
     * Le masque autorise uniquement les caractères suivants:
     * <ul>
     * <li>Lettres majuscules et minuscules (A-Z, a-z).</li>
     * <li>Espaces.</li>
     * <li>Le symbole de tiret (trait d'union, '-').</li>
     * </ul>
     */
    private void setupNameMasking() {
        // Masque de saisie pour le nom du titulaire de la CB (lettres, espaces et le simbole "-")
        if (nomPropietaireCbField != null) {
            UnaryOperator<TextFormatter.Change> nameFilter = change -> {
                String newText = change.getControlNewText();

                // Permite letras (minuscules/majuscules), espacios y tirets
                if (newText.matches("[a-zA-Z\\s-]*")) {
                    return change;
                }
                return null;
            };
            nomPropietaireCbField.setTextFormatter(new TextFormatter<>(nameFilter));
        }
    }


    /**
     * Construit et formate le message de confirmation détaillé ("reçu numérique") affiché à l'utilisateur
     * en cas de succès du paiement.
     * <p>
     * Ce message inclut tous les détails de la réservation et de l'événement, tels que:
     * <ul>
     * <li>L'identifiant et le titulaire de la réservation.</li>
     * <li>Le montant total payé.</li>
     * <li>Les détails de l'événement (Nom, Lieu, Date/Heure formatée).</li>
     * <li>Le nombre, la catégorie et le prix unitaire des places réservées.</li>
     * </ul>
     *
     * @return Une chaîne de caractères formatée représentant le reçu de paiement.
     */
    private String ticketMessageBuilt(){

        // Obtention et formatage des données de la Carte Bancaire
        String nomCB = this.nomPropietaireCbField.getText();
        String numCB = this.cbField.getText();

        String hiddenCBNumber;
        if (numCB != null && numCB.length() >= 4) {
            String lastFour = numCB.substring(numCB.length() - 4);
            // Format demandé: "**** **** **** 1234"
            hiddenCBNumber = "**** **** **** " + lastFour;
        } else {
            hiddenCBNumber = "Numéro de CB pas disponible";
        }

        // Format pour la date et heure de l'évenement
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
        String dateHeureEvent = this.currentReservation.getEvenement().getDateEvent().format(formatter);

        // Accès aux données nécessaires opur la construction du ticket
        String nomClient = this.currentReservation.getClient().getPrenom() + " " + this.currentReservation.getClient().getNom();
        int nbPlaces = this.currentReservation.getPlaces().size();
        String nomCategorie = this.currentReservation.getPlaces().getFirst().getCategorie().getNomCategorie();
        double prixUnitaire = this.currentReservation.getPlaces().getFirst().getPrix();
        String nomEvenement = this.currentReservation.getEvenement().getNom();
        String lieuEvenement = this.currentReservation.getEvenement().getLieu();
        double montantTotal = this.currentReservation.calculateTotalPrice();

        return String.format(
                "===========================================\n" +
                        "             PAIEMENT RÉUSSI\n" +
                        "===========================================\n" +
                        "\n--- DÉTAILS DE LA COMMANDE ---\n" +
                        "Référence Réservation : %d\n" +
                        "Titulaire de la Réservation : %s\n" +
                        "Titulaire de la Carte Bancaire : %s\n" +
                        "Carte Bancaire utilisé : %s\n" +
                        "Montant Total Payé : %.2f €\n" +
                        "\n--- BILLETS RÉSERVÉS ---\n" +
                        "Événement : %s\n" +
                        "Lieu : %s\n" +
                        "Date et Heure : %s\n" +
                        "\n--- DÉTAILS DU PRIX ---\n" +
                        "Nombre de Places : %d\n" +
                        "Catégorie : %s\n" +
                        "Prix Unitaire : %.2f €\n" +
                        "===========================================\n",
                this.currentReservation.getIdReservation(),
                nomClient,
                nomCB,
                hiddenCBNumber,
                montantTotal,
                nomEvenement,
                lieuEvenement,
                dateHeureEvent,
                nbPlaces,
                nomCategorie,
                prixUnitaire
        );
    }
}