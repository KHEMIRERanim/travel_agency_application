package controllers;

import entities.Flight;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    public void setFlights(List<Flight> flights, int requiredPassengers) {
        this.requiredSeats = requiredPassengers;

        // Update result count label
        resultCountLabel.setText(flights.size() + " flights found");

        // Clear existing content
        flightsContainer.getChildren().clear();

        // Create a VBox for each flight
        for (Flight flight : flights) {
            VBox flightBox = createFlightBox(flight);
            flightsContainer.getChildren().add(flightBox);
        }

        // If no flights found, display a message
        if (flights.isEmpty()) {
            Label noResultsLabel = new Label("No flights matching your criteria were found.");
            noResultsLabel.setStyle("-fx-text-fill: #616161; -fx-font-size: 16px;");
            flightsContainer.getChildren().add(noResultsLabel);
        }
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

        // Show available seats
        Label seatsLabel = new Label(flight.getAvailable_seats() + " seats available");
        seatsLabel.setStyle("-fx-text-fill: #616161;");

        // Date information
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Label dateLabel = new Label(dateFormat.format(flight.getFlight_date()));
        dateLabel.setStyle("-fx-text-fill: #616161;");

        // Add a confirm button
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-background-color: #FF9E0C; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        confirmButton.setPrefWidth(120);
        confirmButton.setPrefHeight(30);
        confirmButton.setOnAction(e -> handleFlightConfirmation(flight));

        // Add a spacer region to push the confirm button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bottomSection.getChildren().addAll(priceLabel, seatsLabel, dateLabel, spacer, confirmButton);

        // Add all sections to flight box
        flightBox.getChildren().addAll(topSection, routeSection, bottomSection);

        return flightBox;
    }

    private String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%dh %02dm", hours, mins);
    }

    @FXML
    void goBack(ActionEvent event) {
        // Close this window
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
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