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
    private boolean isEmbeddedInDashboard = false;
    private UserDashboardController dashboardController;

    public void setSourceController(ChercherVolController controller) {
        this.sourceController = controller;
    }

    public void setDashboardController(UserDashboardController controller) {
        this.dashboardController = controller;
        this.isEmbeddedInDashboard = (controller != null);
    }

    public void setFlights(List<Flight> flights, int requiredPassengers) {
        this.requiredSeats = requiredPassengers;

        resultCountLabel.setText(flights.size() + " flights found");

        flightsContainer.getChildren().clear();

        if (flights.isEmpty()) {
            displayNoFlightsMessage();
            return;
        }

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

        VBox flightBox = new VBox();
        flightBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");
        flightBox.setSpacing(10);
        flightBox.setPadding(new Insets(15));

        HBox topSection = new HBox();
        topSection.setAlignment(Pos.CENTER_LEFT);
        topSection.setSpacing(10);

        ImageView airlineImageView = new ImageView();
        airlineImageView.setFitHeight(40);
        airlineImageView.setFitWidth(80);
        airlineImageView.setPreserveRatio(true);

        try {
            String imageUrl = flight.getImage_url();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Try loading as a file path
                File file = new File(imageUrl);
                if (file.exists()) {
                    Image airlineImage = new Image(file.toURI().toString());
                    airlineImageView.setImage(airlineImage);
                } else {

                    try {
                        Image airlineImage = new Image(imageUrl);
                        airlineImageView.setImage(airlineImage);
                    } catch (Exception e) {
                        System.out.println("Error loading image as URL: " + e.getMessage());

                        try {
                            Image airlineImage = new Image(getClass().getResourceAsStream(imageUrl));
                            airlineImageView.setImage(airlineImage);
                        } catch (Exception ex) {
                            System.out.println("Error loading image from resources: " + ex.getMessage());

                            airlineImageView.setImage(new Image("https://via.placeholder.com/80x40?text=" + flight.getAirline().substring(0, Math.min(flight.getAirline().length(), 3))));
                        }
                    }
                }
            } else {

                airlineImageView.setImage(new Image("https://via.placeholder.com/80x40?text=" + flight.getAirline().substring(0, Math.min(flight.getAirline().length(), 3))));
            }
        } catch (Exception e) {
            System.out.println("Error loading airline image: " + e.getMessage());
            try {

                airlineImageView.setImage(new Image("https://via.placeholder.com/80x40?text=Air"));
            } catch (Exception ex) {
                System.out.println("Error loading placeholder image: " + ex.getMessage());
            }
        }

        Label airlineLabel = new Label(flight.getAirline());
        airlineLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        Label flightNumberLabel = new Label("Flight " + flight.getFlight_number());
        flightNumberLabel.setStyle("-fx-text-fill: #757575;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topSection.getChildren().addAll(airlineImageView, airlineLabel, spacer, flightNumberLabel);

        HBox routeSection = new HBox();
        routeSection.setAlignment(Pos.CENTER);
        routeSection.setSpacing(10);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String departureTime = timeFormat.format(flight.getDeparture_Time());
        String arrivalTime = timeFormat.format(flight.getArrival_Time());

        Label departureLabel = new Label(flight.getDeparture() + "\n" + departureTime);
        departureLabel.setStyle("-fx-font-size: 14; -fx-text-alignment: center;");

        Label flightDurationLabel = new Label(formatDuration(flight.getFlight_duration()));
        flightDurationLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #757575;");

        Label destinationLabel = new Label(flight.getDestination() + "\n" + arrivalTime);
        destinationLabel.setStyle("-fx-font-size: 14; -fx-text-alignment: center;");

        Region flightLine = new Region();
        flightLine.setStyle("-fx-background-color: #BDBDBD; -fx-min-height: 1; -fx-pref-height: 1; -fx-max-height: 1;");
        HBox.setHgrow(flightLine, Priority.ALWAYS);

        VBox durationBox = new VBox(flightDurationLabel);
        durationBox.setAlignment(Pos.CENTER);

        routeSection.getChildren().addAll(departureLabel, flightLine, destinationLabel);
        routeSection.getChildren().add(1, durationBox);

        HBox bottomSection = new HBox();
        bottomSection.setAlignment(Pos.CENTER_LEFT);
        bottomSection.setSpacing(15);

        Label priceLabel = new Label(String.format("$%.2f", flight.getPrice()));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        priceLabel.setStyle("-fx-text-fill: #FF6F00;");

        Label seatsLabel = new Label(flight.getAvailable_seats() + " seats available");

        if (requiredSeats > 0 && flight.getAvailable_seats() < requiredSeats) {
            seatsLabel.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
        } else {
            seatsLabel.setStyle("-fx-text-fill: #4CAF50;");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Label dateLabel = new Label(dateFormat.format(flight.getFlight_date()));
        dateLabel.setStyle("-fx-text-fill: #616161;");

        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-background-color: #FF9E0C; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        confirmButton.setPrefWidth(120);
        confirmButton.setPrefHeight(30);

        if (requiredSeats > 0 && flight.getAvailable_seats() < requiredSeats) {
            confirmButton.setDisable(true);
            confirmButton.setStyle("-fx-background-color: #BDBDBD; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        }

        confirmButton.setOnAction(e -> handleFlightConfirmation(flight));

        Region spacerBottom = new Region();
        HBox.setHgrow(spacerBottom, Priority.ALWAYS);

        bottomSection.getChildren().addAll(priceLabel, seatsLabel, dateLabel, spacerBottom, confirmButton);

        flightBox.getChildren().addAll(topSection, routeSection, bottomSection);

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
            if (isEmbeddedInDashboard && dashboardController != null) {
                // If we're embedded in dashboard, go back to flight search within dashboard
                dashboardController.reserveFlight(event);
            } else {
                // Standalone mode, load ChercherVol.fxml as a separate scene
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Flight Search");
            }
        } catch (IOException e) {
            System.out.println("Error returning to search screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleFlightConfirmation(Flight flight) {

        System.out.println("Flight confirmed: " + flight.getFlight_number());

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