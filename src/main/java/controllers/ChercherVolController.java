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


            ServiceFlight serviceFlight = new ServiceFlight();
            List<Flight> allFlights = serviceFlight.recuperer();


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


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FlightSearchResults.fxml"));
            Parent root = loader.load();


            FlightSearchResultsController resultsController = loader.getController();
            resultsController.setFlights(filteredFlights, finalTotalPassengers);
            resultsController.setSourceController(this);


            Stage stage = (Stage) btnRechercherVol.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Flight Search Results");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("FXML loading error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void reloadSearchScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnRechercherVol.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Flight Search");
        } catch (IOException e) {
            System.out.println("Error reloading search scene: " + e.getMessage());
            e.printStackTrace();
        }
    }
}