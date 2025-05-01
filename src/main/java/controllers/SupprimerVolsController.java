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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
    private VBox flightsContainer;

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

            // Ajouter chaque vol dans un bloc vertical
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
        flightBlock.setStyle("-fx-background-color: white; -fx-border-color: #039BE5; -fx-border-radius: 10; -fx-background-radius: 10;");

        // En-tête avec numéro de vol et compagnie aérienne
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setSpacing(10);

        Label flightNumberLabel = new Label("Vol " + flight.getFlight_number());
        flightNumberLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        Label airlineLabel = new Label("| " + flight.getAirline());
        airlineLabel.setFont(Font.font("System", 16));

        headerBox.getChildren().addAll(flightNumberLabel, airlineLabel);

        // Image du vol
        ImageView flightImage = new ImageView();
        try {
            Image image = new Image(flight.getImage_url());
            flightImage.setImage(image);
        } catch (Exception e) {
            // Image par défaut en cas d'erreur
            try {
                Image defaultImage = new Image("https://via.placeholder.com/400x225?text=Avion");
                flightImage.setImage(defaultImage);
            } catch (Exception ex) {
                // Ignorer
            }
        }

        // Configurer la taille de l'image
        flightImage.setFitWidth(500);
        flightImage.setFitHeight(280);
        flightImage.setPreserveRatio(true);
        flightImage.setSmooth(true);

        // Informations du vol
        VBox infoBox = new VBox();
        infoBox.setSpacing(5);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label routeLabel = new Label(flight.getDeparture() + " → " + flight.getDestination());
        routeLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        Label dateLabel = new Label("Date: " + flight.getFlight_date());
        dateLabel.setFont(Font.font("System", 14));

        Label timeLabel = new Label("Départ: " + flight.getDeparture_Time() + " | Arrivée: " + flight.getArrival_Time());
        timeLabel.setFont(Font.font("System", 14));

        Label durationLabel = new Label("Durée: " + flight.getFlight_duration() + " minutes");
        durationLabel.setFont(Font.font("System", 14));

        Label priceLabel = new Label("Prix: " + flight.getPrice() + " €");
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        infoBox.getChildren().addAll(routeLabel, dateLabel, timeLabel, durationLabel, priceLabel);

        // Bouton de suppression
        Button deleteButton = new Button("Supprimer ce vol");
        deleteButton.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
        deleteButton.setPrefWidth(200);

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
        flightBlock.getChildren().addAll(headerBox, flightImage, infoBox, deleteButton);

        return flightBlock;
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}