package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.stage.Stage;
import org.example.model.*;
import org.example.service.EvenementService;
import org.example.MainApplication;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Contrôleur principal pour la **gestion des événements**.
 * <p>
 * Il regroupe trois grands volets fonctionnels :
 * <ul>
 *     <li><b>Création d'événements</b> (côté organisateur) :
 *     saisie du nom, type, date/heure, lieu, intervenant, catégories de places,
 *     prix et quantités, puis enregistrement en base.</li>
 *     <li><b>Consultation d'événements</b> (côté client) :
 *     affichage d'une liste triée par date avec filtres sur le type, le lieu
 *     et l'artiste/intervenant.</li>
 *     <li><b>Statistiques</b> (côté organisateur) :
 *     affichage du nombre de tickets vendus, du chiffre d'affaires et du taux
 *     de remplissage par catégorie.</li>
 * </ul>
 * Il gère aussi la navigation :
 * <ul>
 *     <li>Retour à l'écran de connexion.</li>
 *     <li>Redirection vers l'écran de réservation d'un événement sélectionné
 *     pour un client.</li>
 * </ul>
 */
public class EvenementController {

    // --- Constantes utiles --------------------------------------------------
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String HEURE_PAR_DEFAUT = "20:00";

    // --- Sections affichées ou non selon le rôle ----------------------------
    @FXML private TitledPane creationPane;
    @FXML private TitledPane statsPane;

    // --- Formulaire de création ---------------------------------------------
    @FXML private TextField nomField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField heureField;
    @FXML private TextField lieuField;
    @FXML private TextField specialGuestField;
    @FXML private Label specialGuestLabel;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private TextField categoriePersonnaliseeField;
    @FXML private Spinner<Double> prixSpinner;
    @FXML private Spinner<Integer> quantiteSpinner;
    @FXML private ListView<CategoriePlaceDefinition> categoriesListView;
    @FXML private Label creationFeedbackLabel;

    // --- Filtres + tableau de consultation ---------------------------------
    @FXML private ComboBox<String> filtreTypeCombo;
    @FXML private TextField filtreLieuField;
    @FXML private TextField filtreGuestField;
    @FXML private TableView<Evenement> evenementsTable;
    @FXML private TableColumn<Evenement, String> nomColumn;
    @FXML private TableColumn<Evenement, String> typeColumn;
    @FXML private TableColumn<Evenement, String> dateColumn;
    @FXML private TableColumn<Evenement, String> lieuColumn;
    @FXML private TableColumn<Evenement, String> guestColumn;
    @FXML private TableColumn<Evenement, String> placesColumn;
    @FXML private Button reserverSelectionButton;
    @FXML private Button retourConnexionButton;

    // --- Statistiques -------------------------------------------------------
    @FXML private Label statInfoLabel;
    @FXML private Label statTotalTicketsLabel;
    @FXML private Label statChiffreAffairesLabel;
    @FXML private ListView<String> statCategorieListView;

    private final EvenementService evenementService = new EvenementService();
    private final ObservableList<Evenement> evenements = FXCollections.observableArrayList();
    private final ObservableList<Evenement> evenementsAffiches = FXCollections.observableArrayList();
    private final ObservableList<CategoriePlaceDefinition> categoriesEnCreation = FXCollections.observableArrayList();
    private Utilisateur utilisateurConnecte;

    // ------------------------------------------------------------------------
    /**
     * Méthode appelée automatiquement par JavaFX juste après le chargement du FXML.
     * <p>
     * Elle prépare l'écran en :
     * <ul>
     *     <li>configurant l'affichage des différentes sections,</li>
     *     <li>initialisant le formulaire de création d'événement,</li>
     *     <li>configurant les filtres de recherche,</li>
     *     <li>configurant le tableau des événements,</li>
     *     <li>chargeant la liste des événements depuis la base.</li>
     * </ul>
     */
    @FXML
    public void initialize() {
        configurerGestionAffichage();
        configurerFormulaireCreation();
        configurerFiltres();
        configurerTableau();
        rechargerEvenements();
    }

    /**
     * Initialise le contrôleur avec l'utilisateur actuellement connecté.
     * <p>
     * Cette méthode est appelée par le contrôleur de connexion après une
     * authentification réussie. Elle adapte l'interface selon le type
     * d'utilisateur :
     * <ul>
     *     <li><b>Organisateur</b> : accès à la création d'événements et aux statistiques.</li>
     *     <li><b>Client</b> : accès uniquement à la liste des événements
     *     et au bouton de réservation.</li>
     * </ul>
     *
     * @param utilisateur utilisateur connecté (client ou organisateur)
     */
    public void initData(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
        boolean estOrganisateur = utilisateur instanceof Organisateur;
        creationPane.setVisible(estOrganisateur);
        statsPane.setVisible(estOrganisateur);
        if (statInfoLabel != null) {
            statInfoLabel.setVisible(estOrganisateur);
        }
        // Le bouton "Réserver l'événement sélectionné" est uniquement utile pour un client
        if (reserverSelectionButton != null) {
            reserverSelectionButton.setVisible(!estOrganisateur);
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Configure le comportement d'affichage des différentes sections.
     * <p>
     * On lie les propriétés {@code managed} et {@code visible} pour que
     * les panneaux ou boutons masqués ne prennent plus de place dans la
     * mise en page. On initialise également la liste des catégories en
     * cours de création.
     */
    private void configurerGestionAffichage() {
        creationPane.managedProperty().bind(creationPane.visibleProperty());
        statsPane.managedProperty().bind(statsPane.visibleProperty());
        if (statInfoLabel != null) {
            statInfoLabel.managedProperty().bind(statInfoLabel.visibleProperty());
        }
        // Quand le bouton "Réserver" est caché, il ne prend plus de place dans la mise en page
        if (reserverSelectionButton != null) {
            reserverSelectionButton.managedProperty().bind(reserverSelectionButton.visibleProperty());
        }
        // La liste affiche directement le contenu de categoriesEnCreation
        categoriesListView.setItems(categoriesEnCreation);
        categoriesListView.setPlaceholder(new Label("Ajoutez une catégorie de places"));
    }

    /**
     * Initialise le formulaire de création d'événement avec des valeurs par défaut.
     * <p>
     * - Configure les spinners (prix, quantité).<br>
     * - Alimente la combo de type d'événement et de catégories de places.<br>
     * - Positionne la date par défaut (demain) et l'heure par défaut (20:00).<br>
     * - Vide le message de retour de création.
     */
    private void configurerFormulaireCreation() {
        configurerSpinners();
        configurerTypeCombo();
        configurerCategorieCombo();
        datePicker.setValue(LocalDate.now().plusDays(1));
        heureField.setText(HEURE_PAR_DEFAUT);
        creationFeedbackLabel.setText("");
    }

    /**
     * Configure les spinners de prix et de quantité pour les catégories de places.
     * <p>
     * - Prix : de 5€ à 1000€, pas de 5€.<br>
     * - Quantité : de 1 à 1000, pas de 1.
     */
    private void configurerSpinners() {
        DoubleSpinnerValueFactory prixFactory = new DoubleSpinnerValueFactory(5, 1000, 50, 5);
        prixSpinner.setValueFactory(prixFactory);

        IntegerSpinnerValueFactory quantiteFactory = new IntegerSpinnerValueFactory(1, 1000, 50, 1);
        quantiteSpinner.setValueFactory(quantiteFactory);
    }

    /**
     * Configure la liste déroulante du type d'événement.
     * <p>
     * Ajoute les valeurs possibles (Concert, Spectacle, Conference) et met
     * à jour le libellé de l'artiste/intervenant en fonction du type.
     */
    private void configurerTypeCombo() {
        typeCombo.getItems().setAll("Concert", "Spectacle", "Conference");
        typeCombo.getSelectionModel().selectFirst();
        typeCombo.valueProperty().addListener((obs, oldValue, newValue) -> mettreAJourLibelleSpecialGuest(newValue));
        mettreAJourLibelleSpecialGuest(typeCombo.getValue());
    }

    /**
     * Met à jour le texte du label décrivant le champ "special guest"
     * selon le type d'événement sélectionné.
     *
     * @param typeEvenement type choisi (Concert, Spectacle, Conference)
     */
    private void mettreAJourLibelleSpecialGuest(String typeEvenement) {
        if ("Conference".equals(typeEvenement)) {
            specialGuestLabel.setText("Intervenant principal");
        } else if ("Spectacle".equals(typeEvenement)) {
            specialGuestLabel.setText("Troupe / Compagnie");
        } else {
            specialGuestLabel.setText("Artiste principal");
        }
    }

    /**
     * Configure la liste déroulante des catégories de places.
     * <p>
     * - Active ou désactive le champ de catégorie personnalisée selon le choix.<br>
     * - Charge la liste des catégories depuis la base (ou valeurs par défaut).
     */
    private void configurerCategorieCombo() {
        categoriePersonnaliseeField.setDisable(true);
        categorieCombo.valueProperty().addListener((obs, oldValue, newValue) ->
                categoriePersonnaliseeField.setDisable(!"Personnalisée".equals(newValue)));
        chargerCategoriesDepuisBdd();
    }

    /**
     * Charge la liste des catégories de places depuis la base de données
     * via le service d'événements.
     * <p>
     * En cas d'erreur SQL, des catégories par défaut sont utilisées
     * (VIP, Gold, Silver, Standard).
     */
    private void chargerCategoriesDepuisBdd() {
        List<String> categories;
        try {
            categories = evenementService.chargerCategories()
                    .stream()
                    .map(Categorie::getNomCategorie)
                    .sorted(String::compareToIgnoreCase)
                    .toList();
        } catch (SQLException e) {
            categories = List.of("VIP", "Gold", "Silver", "Standard");
        }
        ObservableList<String> items = FXCollections.observableArrayList(categories);
        items.add("Personnalisée");
        categorieCombo.setItems(items);
        categorieCombo.getSelectionModel().selectFirst();
    }

    // ------------------------------------------------------------------------
    /**
     * Configure les filtres de recherche d'événements (type, lieu, guest).
     * <p>
     * Chaque modification de filtre déclenche un recalcul de la liste
     * d'événements affichés.
     */
    private void configurerFiltres() {
        filtreTypeCombo.getItems().setAll("Tous", "Concert", "Spectacle", "Conference");
        filtreTypeCombo.getSelectionModel().selectFirst();

        filtreTypeCombo.valueProperty().addListener((obs, o, n) -> appliquerFiltres());
        filtreLieuField.textProperty().addListener((obs, o, n) -> appliquerFiltres());
        filtreGuestField.textProperty().addListener((obs, o, n) -> appliquerFiltres());
    }

    /**
     * Configure les colonnes du tableau des événements et les actions associées.
     * <p>
     * - Définit comment chaque colonne lit les données d'un objet {@link Evenement}.<br>
     * - Relie la liste observable {@code evenementsAffiches} à la table.<br>
     * - Met à jour les statistiques quand la sélection change.<br>
     * - Configure les actions des boutons de réservation et de retour à la connexion.
     */
    private void configurerTableau() {
        nomColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTypeEvenement()));
        dateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateEvent().format(DATE_FORMATTER)));
        lieuColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLieu()));
        guestColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSpecialGuest()));
        placesColumn.setCellValueFactory(data -> {
            Evenement evt = data.getValue();
            String texte = evt.getNombrePlacesDisponibles() + "/" + evt.getCapaciteTotale();
            return new SimpleStringProperty(texte);
        });

        evenementsTable.setItems(evenementsAffiches);
        evenementsTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, ancien, selection) -> afficherStatistiques(selection));

        // Bouton "Réserver" actif uniquement pour un client avec un événement sélectionné
        if (reserverSelectionButton != null) {
            reserverSelectionButton.setOnAction(e -> ouvrirReservationPourSelection());
        }

        // Bouton "Retour à la connexion" pour revenir à l'écran de login
        if (retourConnexionButton != null) {
            retourConnexionButton.setOnAction(e -> retournerALaConnexion());
        }
    }

    /**
     * Recharge la liste complète des événements depuis la base de données,
     * trie les événements par date, puis applique les filtres en cours.
     */
    private void rechargerEvenements() {
        try {
            evenements.setAll(evenementService.chargerEvenements());
            evenements.sort(Comparator.comparing(Evenement::getDateEvent));
            appliquerFiltres();
        } catch (SQLException e) {
            afficherErreur("Erreur de chargement", e.getMessage());
        }
    }

    /**
     * Applique les filtres saisis (type, lieu, artiste/intervenant)
     * à la liste complète des événements, puis met à jour la liste
     * observable {@code evenementsAffiches} utilisée par la table.
     */
    private void appliquerFiltres() {
        List<Evenement> resultat = new ArrayList<>();
        for (Evenement evt : evenements) {
            if (correspondFiltre(evt)) {
                resultat.add(evt);
            }
        }
        evenementsAffiches.setAll(resultat);
    }

    /**
     * Indique si un événement correspond aux filtres actuellement saisis.
     *
     * @param evt événement à tester
     * @return {@code true} si l'événement correspond à tous les filtres,
     *         {@code false} sinon
     */
    private boolean correspondFiltre(Evenement evt) {
        if (evt == null) {
            return false;
        }

        String typeChoisi = filtreTypeCombo.getValue();
        if (typeChoisi != null && !"Tous".equalsIgnoreCase(typeChoisi)
                && !evt.getTypeEvenement().equalsIgnoreCase(typeChoisi)) {
            return false;
        }

        String lieuRecherche = filtreLieuField.getText();
        if (!estVide(lieuRecherche) && !evt.getLieu().toLowerCase(Locale.ROOT).contains(lieuRecherche.toLowerCase(Locale.ROOT))) {
            return false;
        }

        String guestRecherche = filtreGuestField.getText();
        if (!estVide(guestRecherche) && !evt.getSpecialGuest().toLowerCase(Locale.ROOT).contains(guestRecherche.toLowerCase(Locale.ROOT))) {
            return false;
        }

        return true;
    }

    /**
     * Vérifie si un texte est null ou ne contient que des espaces.
     *
     * @param texte texte à tester
     * @return {@code true} si le texte est null ou vide/blanc, sinon {@code false}
     */
    private boolean estVide(String texte) {
        return texte == null || texte.isBlank();
    }

    // ------------------------------------------------------------------------
    /**
     * Ajoute une catégorie de places (nom, prix, quantité) à la liste
     * des catégories en cours de création pour l'événement.
     * <p>
     * Effectue plusieurs contrôles :
     * <ul>
     *     <li>le nom de catégorie ne doit pas être vide,</li>
     *     <li>le prix et la quantité doivent être strictement positifs.</li>
     * </ul>
     * Affiche des messages d'erreur dans {@code creationFeedbackLabel} si besoin.
     */
    @FXML
    private void ajouterCategorie() {
        String nomCategorie = recupererNomCategorieSaisi();
        if (estVide(nomCategorie)) {
            creationFeedbackLabel.setText("Indiquez un nom de catégorie.");
            return;
        }

        double prix = prixSpinner.getValue();
        int quantite = quantiteSpinner.getValue();
        if (prix <= 0 || quantite <= 0) {
            creationFeedbackLabel.setText("Le prix et la quantité doivent être positifs.");
            return;
        }

        categoriesEnCreation.add(new CategoriePlaceDefinition(nomCategorie.trim(), prix, quantite));
        creationFeedbackLabel.setText("");
    }

    /**
     * Récupère le nom de catégorie choisi ou saisi par l'utilisateur.
     * <p>
     * Si la valeur sélectionnée dans la combo est "Personnalisée",
     * le texte vient du champ de saisie libre, sinon on renvoie
     * directement la valeur sélectionnée.
     *
     * @return nom de la catégorie souhaitée
     */
    private String recupererNomCategorieSaisi() {
        String choix = categorieCombo.getValue();
        if ("Personnalisée".equals(choix)) {
            return categoriePersonnaliseeField.getText();
        }
        return choix;
    }

    /**
     * Supprime de la liste la catégorie actuellement sélectionnée
     * dans la {@link ListView} des catégories en cours de création.
     */
    @FXML
    private void supprimerCategorie() {
        CategoriePlaceDefinition selection = categoriesListView.getSelectionModel().getSelectedItem();
        if (selection != null) {
            categoriesEnCreation.remove(selection);
        }
    }

    /**
     * Vide complètement la liste des catégories en cours de création.
     */
    @FXML
    private void viderCategories() {
        categoriesEnCreation.clear();
    }

    // ------------------------------------------------------------------------
    /**
     * Tente de créer un nouvel événement avec les informations saisies
     * dans le formulaire de création.
     * <p>
     * Étapes principales :
     * <ul>
     *     <li>Vérifier que l'utilisateur est un organisateur.</li>
     *     <li>Contrôler les champs obligatoires (nom, date, heure, lieu, intervenant).</li>
     *     <li>Vérifier la présence d'au moins une catégorie de places.</li>
     *     <li>Construire l'objet {@link Evenement} correspondant au type choisi.</li>
     *     <li>Appeler le service pour l'enregistrer en base (événement + places).</li>
     * </ul>
     * Affiche un message de succès ou d'erreur dans {@code creationFeedbackLabel}.
     */
    @FXML
    private void creerEvenement() {
        creationFeedbackLabel.setStyle("-fx-text-fill: #cc0000;");

        if (!(utilisateurConnecte instanceof Organisateur organisateur)) {
            creationFeedbackLabel.setText("Seul un organisateur peut créer un événement.");
            return;
        }

        String nom = nomField.getText();
        String type = typeCombo.getValue();
        LocalDate date = datePicker.getValue();
        LocalTime heure = lireHeure();
        String lieu = lieuField.getText();
        String specialGuest = specialGuestField.getText();

        if (estVide(nom) || date == null || heure == null || estVide(lieu) || estVide(specialGuest)) {
            creationFeedbackLabel.setText("Tous les champs doivent être remplis.");
            return;
        }

        if (categoriesEnCreation.isEmpty()) {
            creationFeedbackLabel.setText("Ajoutez au moins une catégorie de places.");
            return;
        }

        LocalDateTime dateEvenement = LocalDateTime.of(date, heure);
        Evenement evenement = construireEvenement(type, nom.trim(), dateEvenement, lieu.trim(), specialGuest.trim(), organisateur);

        try {
            evenementService.creerEvenement(evenement, new ArrayList<>(categoriesEnCreation));
            creationFeedbackLabel.setStyle("-fx-text-fill: #1b8a3d;");
            creationFeedbackLabel.setText("Événement créé avec succès !");
            reinitialiserFormulaire();
            rechargerEvenements();
        } catch (SQLException e) {
            creationFeedbackLabel.setStyle("-fx-text-fill: #cc0000;");
            creationFeedbackLabel.setText("Erreur : " + e.getMessage());
        }
    }

    /**
     * Construit une instance concrète d'{@link Evenement} (Concert, Spectacle
     * ou Conference) en fonction du type choisi.
     *
     * @param type          type d'événement ("Concert", "Spectacle", "Conference")
     * @param nom           nom de l'événement
     * @param date          date et heure de l'événement
     * @param lieu          lieu de l'événement
     * @param specialGuest  artiste / intervenant / troupe
     * @param organisateur  organisateur propriétaire de l'événement
     * @return une instance de {@link Concert}, {@link Spectacle} ou {@link Conference}
     */
    private Evenement construireEvenement(String type, String nom, LocalDateTime date, String lieu,
                                          String specialGuest, Organisateur organisateur) {
        return switch (type) {
            case "Spectacle" -> new Spectacle(0, nom, date, lieu, organisateur, specialGuest, null);
            case "Conference" -> new Conference(0, nom, date, lieu, organisateur, specialGuest, null);
            default -> new Concert(0, nom, date, lieu, organisateur, specialGuest, null);
        };
    }

    /**
     * Lit et convertit le texte saisi dans le champ heure en {@link LocalTime}.
     * <p>
     * Si le format est invalide (non conforme à HH:mm), un message d'erreur
     * est affiché et la méthode renvoie {@code null}.
     *
     * @return l'heure saisie ou {@code null} si le format est incorrect
     */
    private LocalTime lireHeure() {
        try {
            return LocalTime.parse(heureField.getText());
        } catch (DateTimeParseException e) {
            creationFeedbackLabel.setText("Format d'heure invalide (HH:mm).");
            return null;
        }
    }

    /**
     * Réinitialise tous les champs du formulaire de création d'événement
     * aux valeurs par défaut (date de demain, heure par défaut, champs vides,
     * liste de catégories en cours de création vidée).
     */
    private void reinitialiserFormulaire() {
        nomField.clear();
        datePicker.setValue(LocalDate.now().plusDays(1));
        heureField.setText(HEURE_PAR_DEFAUT);
        lieuField.clear();
        specialGuestField.clear();
        categorieCombo.getSelectionModel().selectFirst();
        categoriePersonnaliseeField.clear();
        categoriesEnCreation.clear();
    }

    // ------------------------------------------------------------------------
    /**
     * Affiche les statistiques pour l'événement sélectionné, uniquement
     * si l'utilisateur connecté est un organisateur.
     * <p>
     * Les informations affichées sont :
     * <ul>
     *     <li>nombre de tickets vendus / capacité totale,</li>
     *     <li>chiffre d'affaires total,</li>
     *     <li>taux de remplissage par catégorie de place.</li>
     * </ul>
     *
     * @param evenement événement sélectionné dans le tableau
     */
    private void afficherStatistiques(Evenement evenement) {
        if (!(utilisateurConnecte instanceof Organisateur) || evenement == null) {
            statTotalTicketsLabel.setText("");
            statChiffreAffairesLabel.setText("");
            statCategorieListView.getItems().clear();
            return;
        }

        EvenementStats stats = evenementService.calculerStatistiques(evenement);
        statTotalTicketsLabel.setText("Tickets vendus : " + stats.getTotalTicketsVendues()
                + " / " + evenement.getCapaciteTotale());
        statChiffreAffairesLabel.setText(String.format(Locale.FRANCE,
                "Chiffre d'affaires : %.2f €", stats.getChiffreAffaires()));

        List<String> lignes = new ArrayList<>();
        for (Map.Entry<String, Double> entry : stats.getTauxRemplissageParCategorie().entrySet()) {
            lignes.add(String.format(Locale.FRANCE, "%s : %.1f %%", entry.getKey(), entry.getValue()));
        }
        lignes.sort(String::compareToIgnoreCase);
        statCategorieListView.getItems().setAll(lignes);
    }

    /**
     * Affiche une boîte de dialogue d'erreur JavaFX avec un titre
     * et un message fourni.
     *
     * @param titre   titre de la fenêtre d'erreur
     * @param message message détaillé à afficher
     */
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle(titre);
        alert.showAndWait();
    }

    /**
     * Remplace la scène actuelle par l'écran de connexion.
     * <p>
     * Utilisé quand l'utilisateur veut se reconnecter (changer de compte).
     * Charge le fichier {@code login-view.fxml} et replace la scène du stage
     * courant par la scène de connexion.
     */
    @FXML
    private void retournerALaConnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/login-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) evenementsTable.getScene().getWindow();
            stage.setScene(new Scene(root, 700, 500));
            stage.setTitle("Connexion");
            stage.centerOnScreen();
        } catch (IOException e) {
            afficherErreur("Erreur de navigation", "Impossible de revenir à la connexion : " + e.getMessage());
        }
    }

    /**
     * Ouvre l'écran de réservation pour l'événement sélectionné
     * côté client, en transmettant l'utilisateur et l'événement.
     * <p>
     * Étapes :
     * <ul>
     *     <li>Vérifie que l'utilisateur connecté est un {@link Client}.</li>
     *     <li>Vérifie qu'un événement est sélectionné dans le tableau.</li>
     *     <li>Contrôle qu'il reste des places disponibles pour cet événement.</li>
     *     <li>Charge la vue {@code reservation-view.fxml}.</li>
     *     <li>Passe le client et l'événement sélectionné au {@link ReservationController}.</li>
     *     <li>Remplace la scène courante par l'écran de réservation.</li>
     * </ul>
     * En cas de problème, un message d'erreur lisible est affiché.
     */
    @FXML
    private void ouvrirReservationPourSelection() {
        // Seuls les clients réservent
        if (!(utilisateurConnecte instanceof Client client)) {
            afficherErreur("Réservation impossible", "Seuls les clients peuvent réserver des tickets.");
            return;
        }

        Evenement selection = evenementsTable.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Aucun événement sélectionné", "Veuillez d'abord choisir un événement dans la liste.");
            return;
        }

        // 🔎 Vérifier s'il reste des places avant d'ouvrir la fenêtre de réservation
        if (selection.getNombrePlacesDisponibles() <= 0) {
            afficherErreur(
                    "Plus de places disponibles",
                    "Toutes les places pour cet événement ont déjà été réservées.\n"
                            + "Veuillez choisir un autre événement."
            );
            return; // on ne redirige pas vers la fenêtre de réservation
        }

        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/reservation-view.fxml"));
            Parent root = loader.load();

            Object ctrl = loader.getController();
            if (ctrl instanceof ReservationController reservationController) {
                reservationController.setClient(client);
                reservationController.preselectEvent(selection);
            }

            Stage stage = (Stage) evenementsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Réservation - " + selection.getNom());
            stage.centerOnScreen();
        } catch (IOException e) {
            afficherErreur("Erreur d'ouverture", "Impossible d'ouvrir l'écran de réservation : " + e.getMessage());
        }
    }
}
