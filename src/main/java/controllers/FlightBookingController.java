package controllers;

import entities.Client;
import entities.Flight;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FlightBookingController {

    @FXML
    private Label clientNameLabel;

    @FXML
    private Label flightDetailsLabel;

    @FXML
    private Label passengersLabel;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Label reservationDateLabel;

    private Client currentClient;
    private Flight selectedFlight;
    private int requiredPassengers;

    public void setClient(Client client) {
        this.currentClient = client;
    }

    public void setFlight(Flight flight, int requiredPassengers) {
        this.selectedFlight = flight;
        this.requiredPassengers = requiredPassengers;
        displayReservationDetails();
    }

    private void displayReservationDetails() {
        if (currentClient != null) {
            clientNameLabel.setText("Client: " + currentClient.getPrenom() + " " + currentClient.getNom());
        } else {
            clientNameLabel.setText("Client: Non spécifié");
        }

        if (selectedFlight != null) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            flightDetailsLabel.setText(String.format("Vol: %s (%s) de %s à %s, le %s",
                    selectedFlight.getAirline(),
                    selectedFlight.getFlight_number(),
                    selectedFlight.getDeparture(),
                    selectedFlight.getDestination(),
                    dateFormat.format(selectedFlight.getFlight_date())));
            passengersLabel.setText("Passagers: " + requiredPassengers);
            totalPriceLabel.setText(String.format("Prix total: $%.2f", selectedFlight.getPrice() * requiredPassengers));
        } else {
            flightDetailsLabel.setText("Vol: Non spécifié");
            passengersLabel.setText("Passagers: 0");
            totalPriceLabel.setText("Prix total: $0.00");
        }

        reservationDateLabel.setText("Date de réservation: " + new SimpleDateFormat("dd MMM yyyy").format(new Date()));
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDashboard.fxml"));
            Parent root = loader.load();
            UserDashboardController controller = loader.getController();
            controller.setClient(currentClient);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord");
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors du retour au tableau de bord: " + e.getMessage());
            e.printStackTrace();
        }
    }
}