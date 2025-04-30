package controllers;

import entities.Flight;
import services.ServiceFlight;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ModifierVolController {

    @FXML
    private TextField searchFlightId;

    @FXML
    private TextField editFlightNumber;

    @FXML
    private TextField editDeparture;

    @FXML
    private TextField editDestination;

    @FXML
    private TextField editDepartureTime;

    @FXML
    private TextField editArrivalTime;

    @FXML
    private DatePicker editFlightDate;

    @FXML
    private TextField editFlightDuration;

    @FXML
    private TextField editAvailableSeats;

    @FXML
    private TextField editAirline;

    @FXML
    private TextField editPrice;

    @FXML
    private Label statusLabel;

    private ServiceFlight serviceFlight = new ServiceFlight();
    private Flight currentFlight;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnModify;

    @FXML
    void searchFlight() {
        try {
            int flightId = Integer.parseInt(searchFlightId.getText().trim());
            List<Flight> flights = serviceFlight.recuperer();

            boolean found = false;

            for (Flight flight : flights) {
                if (flight.getFlight_id() == flightId) {
                    currentFlight = flight;
                    populateFields(flight);
                    statusLabel.setText("Vol trouvé! Vous pouvez maintenant modifier les détails.");
                    found = true;
                    break;
                }
            }

            if (!found) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun vol trouvé avec cet ID.");
                clearFields();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Veuillez entrer un ID valide (nombre entier).");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de la récupération des vols: " + e.getMessage());
        }
    }

    private void populateFields(Flight flight) {
        editFlightNumber.setText(flight.getFlight_number());
        editDeparture.setText(flight.getDeparture());
        editDestination.setText(flight.getDestination());


        LocalDateTime departureDateTime = flight.getDeparture_Time().toLocalDateTime();
        editDepartureTime.setText(departureDateTime.toLocalTime().format(timeFormatter));


        LocalDateTime arrivalDateTime = flight.getArrival_Time().toLocalDateTime();
        editArrivalTime.setText(arrivalDateTime.toLocalTime().format(timeFormatter));


        editFlightDate.setValue(flight.getFlight_date().toLocalDate());

        editFlightDuration.setText(String.valueOf(flight.getFlight_duration()));
        editAvailableSeats.setText(String.valueOf(flight.getAvailable_seats()));
        editAirline.setText(flight.getAirline());
        editPrice.setText(String.valueOf(flight.getPrice()));
    }

    @FXML
    void modifyFlight() {
        if (currentFlight == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun vol n'a été sélectionné.");
            return;
        }

        try {

            if (editFlightNumber.getText().isEmpty() || editDeparture.getText().isEmpty() ||
                    editDestination.getText().isEmpty() || editAirline.getText().isEmpty() ||
                    editFlightDuration.getText().isEmpty() || editAvailableSeats.getText().isEmpty() ||
                    editPrice.getText().isEmpty() || editDepartureTime.getText().isEmpty() ||
                    editArrivalTime.getText().isEmpty() || editFlightDate.getValue() == null) {

                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
                return;
            }

            int flightDuration = Integer.parseInt(editFlightDuration.getText());
            int availableSeats = Integer.parseInt(editAvailableSeats.getText());
            double price = Double.parseDouble(editPrice.getText());

            LocalDate flightDateLocal = editFlightDate.getValue();
            Date flightDate = Date.valueOf(flightDateLocal);

            LocalTime departureTimeLocal = LocalTime.parse(editDepartureTime.getText(), timeFormatter);
            LocalTime arrivalTimeLocal = LocalTime.parse(editArrivalTime.getText(), timeFormatter);

            LocalDateTime departureDateTime = LocalDateTime.of(flightDateLocal, departureTimeLocal);
            LocalDateTime arrivalDateTime = LocalDateTime.of(flightDateLocal, arrivalTimeLocal);

            Timestamp departureTimestamp = Timestamp.valueOf(departureDateTime);
            Timestamp arrivalTimestamp = Timestamp.valueOf(arrivalDateTime);


            currentFlight.setFlight_number(editFlightNumber.getText());
            currentFlight.setDeparture(editDeparture.getText());
            currentFlight.setDestination(editDestination.getText());
            currentFlight.setAirline(editAirline.getText());
            currentFlight.setFlight_duration(flightDuration);
            currentFlight.setAvailable_seats(availableSeats);
            currentFlight.setPrice(price);
            currentFlight.setDeparture_Time(departureTimestamp);
            currentFlight.setArrival_Time(arrivalTimestamp);
            currentFlight.setFlight_date(flightDate);


            serviceFlight.modifier(currentFlight);

            statusLabel.setText("Vol modifié avec succès !");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Veuillez saisir des nombres valides pour la durée, les places disponibles et le prix.");
        } catch (java.time.format.DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format d'heure", "Veuillez saisir l'heure au format HH:mm (exemple : 12:30).");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de la modification du vol : " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    private void clearFields() {
        editFlightNumber.clear();
        editDeparture.clear();
        editDestination.clear();
        editDepartureTime.clear();
        editArrivalTime.clear();
        editFlightDate.setValue(null);
        editFlightDuration.clear();
        editAvailableSeats.clear();
        editAirline.clear();
        editPrice.clear();
        currentFlight = null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }}