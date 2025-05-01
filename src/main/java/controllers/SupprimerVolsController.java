package controllers;

import entities.Flight;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import services.ServiceFlight;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class SupprimerVolsController implements Initializable {

    @FXML
    private FlowPane flightsContainer;

    @FXML
    private Label statusLabel;

    private ServiceFlight serviceFlight;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceFlight = new ServiceFlight();
        loadFlights();
    }

    private void loadFlights() {
        try {
            // Effacer les vols précédents
            flightsContainer.getChildren().clear();

            // Charger tous les vols
            List<Flight> flights = serviceFlight.recuperer();

            if (flights.isEmpty()) {
                Label noFlightsLabel = new Label("Aucun vol disponible");
                noFlightsLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
                flightsContainer.getChildren().add(noFlightsLabel);
                return;
            }

            // Ajouter chaque vol dans un bloc
            for (Flight flight : flights) {
                flightsContainer.getChildren().add(createFlightBlock(flight));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des vols", e.getMessage());
        }
    }

    private VBox createFlightBlock(Flight flight) {
        // Conteneur principal pour un vol
        VBox flightBlock = new VBox();
        flightBlock.setAlignment(Pos.CENTER);
        flightBlock.setSpacing(10);
        flightBlock.setPadding(new Insets(15));
        flightBlock.setPrefWidth(320); // Largeur fixe pour les blocs (un peu plus petit)
        flightBlock.setPrefHeight(400); // Hauteur fixe pour uniformité
        flightBlock.setStyle("-fx-background-color: #f0f9ff; -fx-border-color: #039BE5; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Image du vol (taille moyenne)
        ImageView flightImage = new ImageView();
        try {
            Image image = new Image(flight.getImage_url());
            flightImage.setImage(image);
        } catch (Exception e) {
            // Image par défaut en cas d'erreur
            try {
                Image defaultImage = new Image("https://via.placeholder.com/200x150?text=Avion");
                flightImage.setImage(defaultImage);
            } catch (Exception ex) {
                // Ignorer si même l'image par défaut échoue
            }
        }

        // Configurer la taille de l'image (moyenne)
        flightImage.setFitWidth(200);
        flightImage.setFitHeight(150);
        flightImage.setPreserveRatio(true);
        flightImage.setSmooth(true);

        // Destination - en gras et plus grand
        Label destinationLabel = new Label(flight.getDestination());
        destinationLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        destinationLabel.setAlignment(Pos.CENTER);

        // Date
        Label dateLabel = new Label(flight.getFlight_date().toString());
        dateLabel.setFont(Font.font("System", 16));
        dateLabel.setAlignment(Pos.CENTER);

        // Prix
        Label priceLabel = new Label("$" + flight.getPrice());
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        priceLabel.setAlignment(Pos.CENTER);

        // Informations de vol (numéro, départ -> arrivée)
        Label flightInfoLabel = new Label("Vol " + flight.getFlight_number() + " | " +
                flight.getDeparture() + " → " + flight.getDestination());
        flightInfoLabel.setFont(Font.font("System", 12));
        flightInfoLabel.setAlignment(Pos.CENTER);

        // Horaires
        Label timeLabel = new Label(formatTime(flight.getDeparture_Time().toString()) + " - " +
                formatTime(flight.getArrival_Time().toString()) + " (" +
                flight.getFlight_duration() + " min)");
        timeLabel.setFont(Font.font("System", 12));
        timeLabel.setAlignment(Pos.CENTER);

        // Bouton de suppression
        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
        deleteButton.setPrefWidth(120);

        // Action du bouton supprimer
        deleteButton.setOnAction(event -> {
            try {
                serviceFlight.supprimer(flight);
                statusLabel.setText("Vol " + flight.getFlight_number() + " supprimé avec succès");
                loadFlights(); // Recharger la liste des vols
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression", e.getMessage());
            }
        });

        // Ajouter tous les éléments au bloc
        flightBlock.getChildren().addAll(
                flightImage,
                destinationLabel,
                dateLabel,
                priceLabel,
                flightInfoLabel,
                timeLabel,
                deleteButton
        );

        return flightBlock;
    }

    // Méthode pour formater l'heure à partir d'un timestamp (prend juste HH:mm)
    private String formatTime(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "00:00";
        }

        try {
            // Format typique: yyyy-MM-dd HH:mm:ss.0
            String[] parts = timestamp.split(" ");
            if (parts.length > 1) {
                String time = parts[1];
                return time.substring(0, 5); // prend juste HH:mm
            }
            return "00:00";
        } catch (Exception e) {
            return "00:00";
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}