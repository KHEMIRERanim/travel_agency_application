package controllers;

import entities.Flight;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceFlight;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ChercherVolController {

    @FXML
    private TextField departurechercher;

    @FXML
    private TextField destinationchercher;

    @FXML
    private DatePicker flightdatechercher;

    @FXML
    private TextField adultchercher;

    @FXML
    private TextField childchercher;

    @FXML
    private Button btnRechercherVol;

    @FXML
    void recherchervol(ActionEvent event) {
        try {
            // Gather search parameters
            String departure = departurechercher.getText().trim();
            String destination = destinationchercher.getText().trim();
            Date flightDate = null;
            if (flightdatechercher.getValue() != null) {
                flightDate = Date.valueOf(flightdatechercher.getValue());
            }

            // Parse passenger counts
            int adultCount = 0;
            int childCount = 0;

            try {
                adultCount = Integer.parseInt(adultchercher.getText().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid adult count: " + e.getMessage());
            }

            try {
                childCount = Integer.parseInt(childchercher.getText().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid child count: " + e.getMessage());
            }

            int totalPassengers = adultCount + childCount;

            // Search for flights
            ServiceFlight serviceFlight = new ServiceFlight();
            List<Flight> allFlights = serviceFlight.recuperer();

            // Filter flights based on search criteria
            final String finalDeparture = departure;
            final String finalDestination = destination;
            final Date finalFlightDate = flightDate;
            final int finalTotalPassengers = totalPassengers;

            List<Flight> filteredFlights = allFlights.stream()
                    .filter(flight -> (finalDeparture.isEmpty() || flight.getDeparture().equalsIgnoreCase(finalDeparture)))
                    .filter(flight -> (finalDestination.isEmpty() || flight.getDestination().equalsIgnoreCase(finalDestination)))
                    .filter(flight -> (finalFlightDate == null || flight.getFlight_date().equals(finalFlightDate)))
                    .filter(flight -> (finalTotalPassengers == 0 || flight.getAvailable_seats() >= finalTotalPassengers))
                    .collect(Collectors.toList());

            // Open the results window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FlightSearchResults.fxml"));
            Parent root = loader.load();

            // Pass the filtered flights to the results controller
            FlightSearchResultsController resultsController = loader.getController();
            resultsController.setFlights(filteredFlights, finalTotalPassengers);

            // Show the results window
            Stage stage = new Stage();
            stage.setTitle("Flight Search Results");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window (optional)
            // Stage currentStage = (Stage) btnRechercherVol.getScene().getWindow();
            // currentStage.close();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("FXML loading error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
