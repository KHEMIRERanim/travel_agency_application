package controllers;

import entities.Flight;
import services.ServiceFlight;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherVolsController implements Initializable {

    @FXML
    private FlowPane flightsContainer;

    @FXML
    private Label statusLabel;

    // Main flight listing pane
    @FXML
    private AnchorPane mainPane;

    // Edit flight pane
    @FXML
    private AnchorPane editPane;

    // Edit form fields
    @FXML
    private TextField editFlightNumber;

    @FXML
    private TextField editDeparture;

    @FXML
    private TextField editDestination;

    @FXML
    private TextField editDepartureTime;

    @FXML
    private TextField editArrivalTime;

    @FXML
    private DatePicker editFlightDate;

    @FXML
    private TextField editFlightDuration;

    @FXML
    private TextField editAvailableSeats;

    @FXML
    private TextField editAirline;

    @FXML
    private TextField editPrice;

    @FXML
    private Button btnSaveChanges;

    @FXML
    private Button btnReturnToList;

    @FXML
    private Label editStatusLabel;

    private final ServiceFlight serviceFlight = new ServiceFlight();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private Flight currentFlight; // Store the current flight being edited

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadFlights();

        // Set up the return button action
        btnReturnToList.setOnAction(event -> showFlightListView());

        // Set up the save changes button action
        btnSaveChanges.setOnAction(event -> saveFlightChanges());
    }

    private void loadFlights() {
        try {
            List<Flight> flights = serviceFlight.recuperer();
            flightsContainer.getChildren().clear();

            if (flights.isEmpty()) {
                statusLabel.setText("Aucun vol trouvé dans la base de données.");
            } else {
                for (Flight flight : flights) {
                    flightsContainer.getChildren().add(createFlightCard(flight));
                }
                statusLabel.setText(flights.size() + " vols trouvés");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de la récupération des vols: " + e.getMessage());
            statusLabel.setText("Erreur lors du chargement des vols");
        }
    }

    private VBox createFlightCard(Flight flight) {
        VBox card = new VBox();
        card.setPrefWidth(320);
        card.setPrefHeight(380);
        card.setStyle("-fx-background-color: white; -fx-border-color: #039BE5; -fx-border-radius: 8; " +
                "-fx-padding: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 5);");
        card.setSpacing(10);

        // Animation à l'entrée de la souris - agrandir la carte
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), card);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);

        // Animation à la sortie de la souris - retour à la taille normale
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), card);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        // Ajout des événements de souris pour activer les animations
        card.setOnMouseEntered(e -> scaleIn.play());
        card.setOnMouseExited(e -> scaleOut.play());

        // Image section
        ImageView flightImage = new ImageView();
        flightImage.setFitWidth(300);
        flightImage.setFitHeight(150);
        flightImage.setPreserveRatio(true);
        flightImage.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 4;");

        try {
            if (flight.getImage_url() != null && !flight.getImage_url().isEmpty()) {
                // Détecter si c'est un chemin local ou une URL
                String imagePath = flight.getImage_url();

                // Construire le chemin absolu si c'est un chemin relatif au projet
                if (!imagePath.startsWith("http")) {
                    // Récupérer le répertoire du projet
                    String projectDir = System.getProperty("user.dir");
                    File file = new File(projectDir, "src/" + imagePath);

                    if (file.exists()) {
                        imagePath = file.toURI().toString();
                    } else {
                        System.err.println("Image non trouvée: " + file.getAbsolutePath());
                        imagePath = "https://via.placeholder.com/300x150?text=Not+Found";
                    }
                }

                // Charger l'image avec le chemin corrigé
                flightImage.setImage(new Image(imagePath));
            } else {
                flightImage.setImage(new Image("https://via.placeholder.com/300x150?text=No+Image"));
            }
        } catch (Exception e) {
            flightImage.setImage(new Image("https://via.placeholder.com/300x150?text=Error"));
            System.err.println("Erreur de chargement de l'image: " + e.getMessage());
        }

        // Flight number and airline
        HBox header = new HBox(10);
        Label flightNumberLabel = new Label(flight.getFlight_number());
        flightNumberLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        Label airlineLabel = new Label(flight.getAirline());
        airlineLabel.setFont(Font.font("System", 14));
        airlineLabel.setStyle("-fx-text-fill: #555;");
        header.getChildren().addAll(flightNumberLabel, airlineLabel);

        // Route information
        HBox routeBox = new HBox(10);
        Label departureLabel = new Label(flight.getDeparture());
        departureLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label arrowLabel = new Label(" → ");
        arrowLabel.setFont(Font.font("System", 14));
        Label destinationLabel = new Label(flight.getDestination());
        destinationLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        routeBox.getChildren().addAll(departureLabel, arrowLabel, destinationLabel);

        // Time and date information
        String departureTime = flight.getDeparture_Time().toLocalDateTime().toLocalTime().format(timeFormatter);
        String arrivalTime = flight.getArrival_Time().toLocalDateTime().toLocalTime().format(timeFormatter);
        String date = flight.getFlight_date().toLocalDate().format(dateFormatter);

        Label timeLabel = new Label(departureTime + " - " + arrivalTime);
        timeLabel.setFont(Font.font("System", 14));

        Label dateLabel = new Label("Date: " + date);
        dateLabel.setFont(Font.font("System", 14));

        // Price information
        Label priceLabel = new Label("Prix: " + flight.getPrice() + " €");
        priceLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-font-size: 14px;");

        // Boutons d'action - Conteneur
        VBox buttonsContainer = new VBox(10);

        // Bouton de modification
        Button modifyButton = new Button("Modifier");
        modifyButton.setStyle("-fx-background-color: #039BE5; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
        modifyButton.setPrefWidth(280);

        // Animation pour le bouton modifier
        ScaleTransition modifyScaleIn = new ScaleTransition(Duration.millis(150), modifyButton);
        modifyScaleIn.setToX(1.1);
        modifyScaleIn.setToY(1.1);

        ScaleTransition modifyScaleOut = new ScaleTransition(Duration.millis(150), modifyButton);
        modifyScaleOut.setToX(1.0);
        modifyScaleOut.setToY(1.0);

        modifyButton.setOnMouseEntered(e -> modifyScaleIn.play());
        modifyButton.setOnMouseExited(e -> modifyScaleOut.play());

        // Action pour modifier un vol - Changé pour afficher la vue de modification
        modifyButton.setOnAction(event -> showEditFlightView(flight));

        // Bouton de suppression
        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
        deleteButton.setPrefWidth(280);

        // Animation pour le bouton supprimer
        ScaleTransition deleteScaleIn = new ScaleTransition(Duration.millis(150), deleteButton);
        deleteScaleIn.setToX(1.1);
        deleteScaleIn.setToY(1.1);

        ScaleTransition deleteScaleOut = new ScaleTransition(Duration.millis(150), deleteButton);
        deleteScaleOut.setToX(1.0);
        deleteScaleOut.setToY(1.0);

        deleteButton.setOnMouseEntered(e -> deleteScaleIn.play());
        deleteButton.setOnMouseExited(e -> deleteScaleOut.play());

        // Action de suppression
        deleteButton.setOnAction(event -> {
            try {
                serviceFlight.supprimer(flight);
                statusLabel.setText("Vol " + flight.getFlight_number() + " supprimé avec succès");
                loadFlights(); // Recharger les vols après la suppression
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du vol: " + e.getMessage());
            }
        });

        // Ajouter les boutons au conteneur
        buttonsContainer.getChildren().addAll(modifyButton, deleteButton);

        // Add all components to the card
        card.getChildren().addAll(
                flightImage,
                header,
                routeBox,
                timeLabel,
                dateLabel,
                priceLabel,
                buttonsContainer
        );

        return card;
    }

    // Méthode pour afficher la vue de modification et masquer la liste des vols
    private void showEditFlightView(Flight flight) {
        this.currentFlight = flight;

        // Remplir les champs avec les données du vol
        editFlightNumber.setText(flight.getFlight_number());
        editDeparture.setText(flight.getDeparture());
        editDestination.setText(flight.getDestination());

        // Formatage de l'heure de départ et d'arrivée
        LocalTime departureTime = flight.getDeparture_Time().toLocalDateTime().toLocalTime();
        LocalTime arrivalTime = flight.getArrival_Time().toLocalDateTime().toLocalTime();
        editDepartureTime.setText(departureTime.format(timeFormatter));
        editArrivalTime.setText(arrivalTime.format(timeFormatter));

        // Date du vol
        editFlightDate.setValue(flight.getFlight_date().toLocalDate());

        // Autres champs
        editFlightDuration.setText(String.valueOf(flight.getFlight_duration()));
        editAvailableSeats.setText(String.valueOf(flight.getAvailable_seats()));
        editAirline.setText(flight.getAirline());
        editPrice.setText(String.valueOf(flight.getPrice()));

        // Afficher la vue d'édition et masquer la liste
        mainPane.setVisible(false);
        mainPane.setManaged(false);
        editPane.setVisible(true);
        editPane.setManaged(true);
    }

    // Méthode pour afficher la liste des vols et masquer la vue de modification
    private void showFlightListView() {
        // Réinitialiser le statut
        editStatusLabel.setText("");

        // Recharger la liste des vols
        loadFlights();

        // Afficher la liste et masquer l'édition
        mainPane.setVisible(true);
        mainPane.setManaged(true);
        editPane.setVisible(false);
        editPane.setManaged(false);
    }

    // Méthode pour enregistrer les modifications du vol
    private void saveFlightChanges() {
        try {
            // Récupérer les valeurs des champs
            String flightNumber = editFlightNumber.getText();
            String departure = editDeparture.getText();
            String destination = editDestination.getText();

            // Vérifier et parser l'heure de départ et d'arrivée
            LocalTime depTime;
            LocalTime arrTime;
            try {
                depTime = LocalTime.parse(editDepartureTime.getText(), timeFormatter);
                arrTime = LocalTime.parse(editArrivalTime.getText(), timeFormatter);
            } catch (DateTimeParseException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de format",
                        "Format d'heure invalide. Utilisez le format HH:mm");
                return;
            }

            // Vérifier la date
            LocalDate flightDate = editFlightDate.getValue();
            if (flightDate == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une date");
                return;
            }

            // Créer les timestamps
            LocalDateTime departureDateTime = LocalDateTime.of(flightDate, depTime);
            LocalDateTime arrivalDateTime = LocalDateTime.of(flightDate, arrTime);
            Timestamp departureTimestamp = Timestamp.valueOf(departureDateTime);
            Timestamp arrivalTimestamp = Timestamp.valueOf(arrivalDateTime);

            // Parser les valeurs numériques
            int duration, availableSeats;
            double price;
            try {
                duration = Integer.parseInt(editFlightDuration.getText());
                availableSeats = Integer.parseInt(editAvailableSeats.getText());
                price = Double.parseDouble(editPrice.getText());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de format",
                        "Format numérique invalide pour la durée, les places ou le prix");
                return;
            }

            String airline = editAirline.getText();

            // Mettre à jour l'objet vol
            currentFlight.setFlight_number(flightNumber);
            currentFlight.setDeparture(departure);
            currentFlight.setDestination(destination);
            currentFlight.setDeparture_Time(departureTimestamp);
            currentFlight.setArrival_Time(arrivalTimestamp);
            currentFlight.setFlight_date(java.sql.Date.valueOf(flightDate));
            currentFlight.setFlight_duration(duration);
            currentFlight.setAvailable_seats(availableSeats);
            currentFlight.setAirline(airline);
            currentFlight.setPrice(price);

            // Enregistrer les modifications
            serviceFlight.modifier(currentFlight);

            // Afficher un message de succès
            editStatusLabel.setText("Vol modifié avec succès!");
            editStatusLabel.setStyle("-fx-text-fill: green;");

            // Retourner à la liste des vols après un court délai
            new Thread(() -> {
                try {
                    Thread.sleep(1500); // Attendre 1.5 secondes
                    javafx.application.Platform.runLater(this::showFlightListView);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL",
                    "Erreur lors de la modification du vol: " + e.getMessage());
            editStatusLabel.setText("Erreur lors de la modification");
            editStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}