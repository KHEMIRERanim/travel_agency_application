package controllers;
import java.sql.*;
import entities.Flight;
import services.ServiceFlight;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AjouterVol {

    @FXML
    private TextField AddFlightnumber;
    @FXML
    private TextField AddDeparture;
    @FXML
    private TextField AddDestination;
    @FXML
    private TextField AddDeparture_Time;
    @FXML
    private TextField AddArrivalTime;
    @FXML
    private DatePicker AddFlightDate;
    @FXML
    private TextField AddFlightDuration;
    @FXML
    private TextField AddAvailableSeats;
    @FXML
    private TextField AddAirline;
    @FXML
    private TextField AddPrice;

    ServiceFlight serviceFlight = new ServiceFlight();

    @FXML
    void AddFlight() {
        try {
            String flightNumber = AddFlightnumber.getText();
            String departure = AddDeparture.getText();
            String destination = AddDestination.getText();
            String airline = AddAirline.getText();

            if (flightNumber.isEmpty() || departure.isEmpty() || destination.isEmpty() || airline.isEmpty() ||
                    AddFlightDuration.getText().isEmpty() || AddAvailableSeats.getText().isEmpty() || AddPrice.getText().isEmpty() ||
                    AddDeparture_Time.getText().isEmpty() || AddArrivalTime.getText().isEmpty() || AddFlightDate.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
                return;
            }

            int flightDuration = Integer.parseInt(AddFlightDuration.getText());
            int availableSeats = Integer.parseInt(AddAvailableSeats.getText());
            double price = Double.parseDouble(AddPrice.getText());

            LocalDate flightDateLocal = AddFlightDate.getValue();
            Date flightDate = Date.valueOf(flightDateLocal);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            LocalTime departureTimeLocal = LocalTime.parse(AddDeparture_Time.getText(), timeFormatter);
            LocalTime arrivalTimeLocal = LocalTime.parse(AddArrivalTime.getText(), timeFormatter);

            LocalDateTime departureDateTime = LocalDateTime.of(flightDateLocal, departureTimeLocal);
            LocalDateTime arrivalDateTime = LocalDateTime.of(flightDateLocal, arrivalTimeLocal);

            Timestamp departureTimestamp = Timestamp.valueOf(departureDateTime);
            Timestamp arrivalTimestamp = Timestamp.valueOf(arrivalDateTime);

            Flight flight = new Flight(
                    flightDuration,
                    flightNumber,
                    availableSeats,
                    departure,
                    destination,
                    airline,
                    arrivalTimestamp,
                    departureTimestamp,
                    flightDate,
                    price
            );

            serviceFlight.ajouter(flight);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Vol ajouté avec succès !");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Veuillez saisir des nombres valides pour la durée, les places disponibles et le prix.");
        } catch (java.time.format.DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format d'heure", "Veuillez saisir l'heure au format HH:mm (exemple : 12:30).");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de l'ajout du vol : " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
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
