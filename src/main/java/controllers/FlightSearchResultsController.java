package controllers;

import entities.Flight;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class FlightSearchResultsController {

    @FXML
    private VBox flightsContainer;

    @FXML
    private Label resultCountLabel;

    @FXML
    private Button backButton;

    private int requiredSeats;
    private ChercherVolController sourceController;

    public void setSourceController(ChercherVolController controller) {
        this.sourceController = controller;
    }

    public void setFlights(List<Flight> flights, int requiredPassengers) {
        this.requiredSeats = requiredPassengers;

        // Update result count label
        resultCountLabel.setText(flights.size() + " flights found");

        // Clear existing content
        flightsContainer.getChildren().clear();

        // Check if no flights were found
        if (flights.isEmpty()) {
            displayNoFlightsMessage();
            return;
        }

        // Create a VBox for each flight
        for (Flight flight : flights) {
            VBox flightBox = createFlightBox(flight);
            flightsContainer.getChildren().add(flightBox);
        }
    }

    private void displayNoFlightsMessage() {
        VBox messageBox = new VBox();
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setSpacing(20);
        messageBox.setPadding(new Insets(40));
        messageBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        Label headerLabel = new Label("No matching flights found");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        headerLabel.setStyle("-fx-text-fill: #F44336;");

        Label suggestionLabel = new Label("Please try different search criteria or dates.");
        suggestionLabel.setFont(Font.font("System", 16));
        suggestionLabel.setStyle("-fx-text-fill: #616161;");

        // Add an image if you want
        ImageView noResultsImage = new ImageView();
        try {
            File file = new File("src/main/resources/ImageVol/no_results.png");
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                noResultsImage.setImage(image);
                noResultsImage.setFitHeight(120);
                noResultsImage.setFitWidth(120);
                noResultsImage.setPreserveRatio(true);
                messageBox.getChildren().add(noResultsImage);
            }
        } catch (Exception e) {
            System.out.println("Error loading no results image: " + e.getMessage());
        }

        messageBox.getChildren().addAll(headerLabel, suggestionLabel);
        flightsContainer.getChildren().add(messageBox);
    }

    private VBox createFlightBox(Flight flight) {
        // Create a styled container for each flight
        VBox flightBox = new VBox();
        flightBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");
        flightBox.setSpacing(10);
        flightBox.setPadding(new Insets(15));

        // Top section with airline and flight number
        HBox topSection = new HBox();
        topSection.setAlignment(Pos.CENTER_LEFT);
        topSection.setSpacing(10);

        // Try to load the airline image if available
        ImageView airlineImageView = new ImageView();
        try {
            String imageUrl = flight.getImage_url();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                File file = new File(imageUrl);
                if (file.exists()) {
                    Image airlineImage = new Image(file.toURI().toString());
                    airlineImageView.setImage(airlineImage);
                    airlineImageView.setFitHeight(40);
                    airlineImageView.setFitWidth(80);
                    airlineImageView.setPreserveRatio(true);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading airline image: " + e.getMessage());
        }

        // Add airline name and flight number
        Label airlineLabel = new Label(flight.getAirline());
        airlineLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        Label flightNumberLabel = new Label("Flight " + flight.getFlight_number());
        flightNumberLabel.setStyle("-fx-text-fill: #757575;");

        topSection.getChildren().addAll(airlineImageView, airlineLabel, new Region(), flightNumberLabel);
        HBox.setHgrow(topSection.getChildren().get(2), Priority.ALWAYS);

        // Middle section with route and times
        HBox routeSection = new HBox();
        routeSection.setAlignment(Pos.CENTER);
        routeSection.setSpacing(10);

        // Format times
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String departureTime = timeFormat.format(flight.getDeparture_Time());
        String arrivalTime = timeFormat.format(flight.getArrival_Time());

        // Display route information
        Label departureLabel = new Label(flight.getDeparture() + "\n" + departureTime);
        departureLabel.setStyle("-fx-font-size: 14; -fx-text-alignment: center;");

        Label flightDurationLabel = new Label(formatDuration(flight.getFlight_duration()));
        flightDurationLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #757575;");

        Label destinationLabel = new Label(flight.getDestination() + "\n" + arrivalTime);
        destinationLabel.setStyle("-fx-font-size: 14; -fx-text-alignment: center;");

        // Create a line connecting departure and destination
        Region flightLine = new Region();
        flightLine.setStyle("-fx-background-color: #BDBDBD; -fx-min-height: 1; -fx-pref-height: 1; -fx-max-height: 1;");
        HBox.setHgrow(flightLine, Priority.ALWAYS);

        routeSection.getChildren().addAll(departureLabel, flightLine, destinationLabel);

        // Add flight duration above the line
        VBox durationBox = new VBox(flightDurationLabel);
        durationBox.setAlignment(Pos.CENTER);
        routeSection.getChildren().add(1, durationBox);

        // Bottom section with price, seats, and confirm button
        HBox bottomSection = new HBox();
        bottomSection.setAlignment(Pos.CENTER_LEFT);
        bottomSection.setSpacing(15);

        // Format price
        Label priceLabel = new Label(String.format("$%.2f", flight.getPrice()));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        priceLabel.setStyle("-fx-text-fill: #FF6F00;");

        // Available seats info with appropriate styling based on availability
        Label seatsLabel = new Label(flight.getAvailable_seats() + " seats available");

        // Change the color of the seats label based on availability
        if (requiredSeats > 0 && flight.getAvailable_seats() < requiredSeats) {
            seatsLabel.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
        } else {
            seatsLabel.setStyle("-fx-text-fill: #4CAF50;");
        }

        // Date information
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Label dateLabel = new Label(dateFormat.format(flight.getFlight_date()));
        dateLabel.setStyle("-fx-text-fill: #616161;");

        // Add a confirm button
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-background-color: #FF9E0C; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        confirmButton.setPrefWidth(120);
        confirmButton.setPrefHeight(30);

        // Disable confirm button if not enough seats available
        if (requiredSeats > 0 && flight.getAvailable_seats() < requiredSeats) {
            confirmButton.setDisable(true);
            confirmButton.setStyle("-fx-background-color: #BDBDBD; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        }

        confirmButton.setOnAction(e -> handleFlightConfirmation(flight));

        // Add a spacer region to push the confirm button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bottomSection.getChildren().addAll(priceLabel, seatsLabel, dateLabel, spacer, confirmButton);

        // Add all sections to flight box
        flightBox.getChildren().addAll(topSection, routeSection, bottomSection);

        // Add a warning message if seats are insufficient
        if (requiredSeats > 0 && flight.getAvailable_seats() < requiredSeats) {
            Label warningLabel = new Label("Not enough seats available for your party of " + requiredSeats);
            warningLabel.setStyle("-fx-text-fill: #F44336; -fx-font-style: italic;");
            flightBox.getChildren().add(warningLabel);
        }

        return flightBox;
    }

    private String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%dh %02dm", hours, mins);
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            // Load the search view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
            Parent root = loader.load();

            // Replace the current scene with the search scene
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Flight Search");
        } catch (IOException e) {
            System.out.println("Error returning to search screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleFlightConfirmation(Flight flight) {
        // Implement flight booking logic here
        System.out.println("Flight confirmed: " + flight.getFlight_number());

        // You could open a new booking window or dialog here
        // For now, just show a simple alert
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Flight Confirmation");
        alert.setHeaderText("Booking Confirmed");
        alert.setContentText("You have selected flight " + flight.getFlight_number() + " from " +
                flight.getDeparture() + " to " + flight.getDestination() + " on " +
                new SimpleDateFormat("dd MMM yyyy").format(flight.getFlight_date()) +
                " for " + requiredSeats + " passenger(s).");
        alert.showAndWait();
    }
}