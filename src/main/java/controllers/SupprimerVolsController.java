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

            flightsContainer.getChildren().clear();


            List<Flight> flights = serviceFlight.recuperer();

            if (flights.isEmpty()) {
                Label noFlightsLabel = new Label("Aucun vol disponible");
                noFlightsLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
                flightsContainer.getChildren().add(noFlightsLabel);
                return;
            }


            for (Flight flight : flights) {
                flightsContainer.getChildren().add(createFlightBlock(flight));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des vols", e.getMessage());
        }
    }

    private VBox createFlightBlock(Flight flight) {

        VBox flightBlock = new VBox();
        flightBlock.setAlignment(Pos.CENTER);
        flightBlock.setSpacing(10);
        flightBlock.setPadding(new Insets(15));
        flightBlock.setPrefWidth(320);
        flightBlock.setPrefHeight(400);
        flightBlock.setStyle("-fx-background-color: #f0f9ff; -fx-border-color: #039BE5; -fx-border-radius: 10; -fx-background-radius: 10;");


        ImageView flightImage = new ImageView();
        try {
            Image image = new Image(flight.getImage_url());
            flightImage.setImage(image);
        } catch (Exception e) {

            try {
                Image defaultImage = new Image("https://via.placeholder.com/200x150?text=Avion");
                flightImage.setImage(defaultImage);
            } catch (Exception ex) {

            }
        }


        flightImage.setFitWidth(200);
        flightImage.setFitHeight(150);
        flightImage.setPreserveRatio(true);
        flightImage.setSmooth(true);


        Label destinationLabel = new Label(flight.getDestination());
        destinationLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        destinationLabel.setAlignment(Pos.CENTER);


        Label dateLabel = new Label(flight.getFlight_date().toString());
        dateLabel.setFont(Font.font("System", 16));
        dateLabel.setAlignment(Pos.CENTER);


        Label priceLabel = new Label("$" + flight.getPrice());
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        priceLabel.setAlignment(Pos.CENTER);


        Label flightInfoLabel = new Label("Vol " + flight.getFlight_number() + " | " +
                flight.getDeparture() + " → " + flight.getDestination());
        flightInfoLabel.setFont(Font.font("System", 12));
        flightInfoLabel.setAlignment(Pos.CENTER);


        Label timeLabel = new Label(formatTime(flight.getDeparture_Time().toString()) + " - " +
                formatTime(flight.getArrival_Time().toString()) + " (" +
                flight.getFlight_duration() + " min)");
        timeLabel.setFont(Font.font("System", 12));
        timeLabel.setAlignment(Pos.CENTER);


        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
        deleteButton.setPrefWidth(120);


        deleteButton.setOnAction(event -> {
            try {
                serviceFlight.supprimer(flight);
                statusLabel.setText("Vol " + flight.getFlight_number() + " supprimé avec succès");
                loadFlights();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression", e.getMessage());
            }
        });


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


    private String formatTime(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "00:00";
        }

        try {

            String[] parts = timestamp.split(" ");
            if (parts.length > 1) {
                String time = parts[1];
                return time.substring(0, 5);
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