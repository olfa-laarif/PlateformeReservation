package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import org.example.model.*;
import org.example.service.EvenementService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Contrôleur "Gestion des événements".
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
    @FXML
    public void initialize() {
        configurerGestionAffichage();
        configurerFormulaireCreation();
        configurerFiltres();
        configurerTableau();
        rechargerEvenements();
    }

    public void initData(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
        boolean estOrganisateur = utilisateur instanceof Organisateur;
        creationPane.setVisible(estOrganisateur);
        statsPane.setVisible(estOrganisateur);
        if(statInfoLabel !=null)
        { statInfoLabel.setVisible(estOrganisateur); }


    }

    // ------------------------------------------------------------------------
    private void configurerGestionAffichage() {
        creationPane.managedProperty().bind(creationPane.visibleProperty());
        statsPane.managedProperty().bind(statsPane.visibleProperty());
        if(statInfoLabel!=null)
        {
            statInfoLabel.managedProperty().bind(statInfoLabel.visibleProperty());
        }
        categoriesListView.setItems(categoriesEnCreation);
        categoriesListView.setPlaceholder(new Label("Ajoutez une catégorie de places"));
    }

    private void configurerFormulaireCreation() {
        configurerSpinners();
        configurerTypeCombo();
        configurerCategorieCombo();
        datePicker.setValue(LocalDate.now().plusDays(1));
        heureField.setText(HEURE_PAR_DEFAUT);
        creationFeedbackLabel.setText("");
    }

    private void configurerSpinners() {
        DoubleSpinnerValueFactory prixFactory = new DoubleSpinnerValueFactory(5, 1000, 50, 5);
        prixSpinner.setValueFactory(prixFactory);

        IntegerSpinnerValueFactory quantiteFactory = new IntegerSpinnerValueFactory(1, 1000, 50, 1);
        quantiteSpinner.setValueFactory(quantiteFactory);
    }

    private void configurerTypeCombo() {
        typeCombo.getItems().setAll("Concert", "Spectacle", "Conference");
        typeCombo.getSelectionModel().selectFirst();
        typeCombo.valueProperty().addListener((obs, oldValue, newValue) -> mettreAJourLibelleSpecialGuest(newValue));
        mettreAJourLibelleSpecialGuest(typeCombo.getValue());
    }

    private void mettreAJourLibelleSpecialGuest(String typeEvenement) {
        if ("Conference".equals(typeEvenement)) {
            specialGuestLabel.setText("Intervenant principal");
        } else if ("Spectacle".equals(typeEvenement)) {
            specialGuestLabel.setText("Troupe / Compagnie");
        } else {
            specialGuestLabel.setText("Artiste principal");
        }
    }

    private void configurerCategorieCombo() {
        categoriePersonnaliseeField.setDisable(true);
        categorieCombo.valueProperty().addListener((obs, oldValue, newValue) ->
                categoriePersonnaliseeField.setDisable(!"Personnalisée".equals(newValue)));
        chargerCategoriesDepuisBdd();
    }

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
    private void configurerFiltres() {
        filtreTypeCombo.getItems().setAll("Tous", "Concert", "Spectacle", "Conference");
        filtreTypeCombo.getSelectionModel().selectFirst();

        filtreTypeCombo.valueProperty().addListener((obs, o, n) -> appliquerFiltres());
        filtreLieuField.textProperty().addListener((obs, o, n) -> appliquerFiltres());
        filtreGuestField.textProperty().addListener((obs, o, n) -> appliquerFiltres());
    }

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
    }

    private void rechargerEvenements() {
        try {
            evenements.setAll(evenementService.chargerEvenements());
            evenements.sort(Comparator.comparing(Evenement::getDateEvent));
            appliquerFiltres();
        } catch (SQLException e) {
            afficherErreur("Erreur de chargement", e.getMessage());
        }
    }

    private void appliquerFiltres() {
        List<Evenement> resultat = new ArrayList<>();
        for (Evenement evt : evenements) {
            if (correspondFiltre(evt)) {
                resultat.add(evt);
            }
        }
        evenementsAffiches.setAll(resultat);
    }

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

    private boolean estVide(String texte) {
        return texte == null || texte.isBlank();
    }

    // ------------------------------------------------------------------------
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

    private String recupererNomCategorieSaisi() {
        String choix = categorieCombo.getValue();
        if ("Personnalisée".equals(choix)) {
            return categoriePersonnaliseeField.getText();
        }
        return choix;
    }

    @FXML
    private void supprimerCategorie() {
        CategoriePlaceDefinition selection = categoriesListView.getSelectionModel().getSelectedItem();
        if (selection != null) {
            categoriesEnCreation.remove(selection);
        }
    }

    @FXML
    private void viderCategories() {
        categoriesEnCreation.clear();
    }

    // ------------------------------------------------------------------------
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

    private Evenement construireEvenement(String type, String nom, LocalDateTime date, String lieu,
                                          String specialGuest, Organisateur organisateur) {
        return switch (type) {
            case "Spectacle" -> new Spectacle(0, nom, date, lieu, organisateur, specialGuest, null);
            case "Conference" -> new Conference(0, nom, date, lieu, organisateur, specialGuest, null);
            default -> new Concert(0, nom, date, lieu, organisateur, specialGuest, null);
        };
    }

    private LocalTime lireHeure() {
        try {
            return LocalTime.parse(heureField.getText());
        } catch (DateTimeParseException e) {
            creationFeedbackLabel.setText("Format d'heure invalide (HH:mm).");
            return null;
        }
    }

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

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle(titre);
        alert.showAndWait();
    }
}
