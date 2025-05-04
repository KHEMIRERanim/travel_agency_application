package controllers;

import entities.Flight;
import services.ServiceFlight;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherVolsController implements Initializable {

    @FXML
    private FlowPane flightsContainer;

    @FXML
    private Label statusLabel;

    private final ServiceFlight serviceFlight = new ServiceFlight();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadFlights();
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
        card.setPrefHeight(330);  // Augmenté pour accommoder le bouton
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

        // Bouton de suppression
        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
        deleteButton.setPrefWidth(280);

        // Animation pour le bouton
        ScaleTransition buttonScaleIn = new ScaleTransition(Duration.millis(150), deleteButton);
        buttonScaleIn.setToX(1.1);
        buttonScaleIn.setToY(1.1);

        ScaleTransition buttonScaleOut = new ScaleTransition(Duration.millis(150), deleteButton);
        buttonScaleOut.setToX(1.0);
        buttonScaleOut.setToY(1.0);

        deleteButton.setOnMouseEntered(e -> buttonScaleIn.play());
        deleteButton.setOnMouseExited(e -> buttonScaleOut.play());

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

        // Add all components to the card
        card.getChildren().addAll(
                flightImage,
                header,
                routeBox,
                timeLabel,
                dateLabel,
                priceLabel,
                deleteButton
        );

        return card;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}