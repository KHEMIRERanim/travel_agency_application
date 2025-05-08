package controllers;

import entities.Client;
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

    // Référence au UserDashboardController si ce contrôleur est intégré dans le tableau de bord
    private UserDashboardController dashboardController;

    // Indicateur pour déterminer si ce contrôleur est intégré dans le tableau de bord
    private boolean isEmbeddedInDashboard = false;

    // Ajout : Attribut pour stocker le client connecté
    private Client currentClient;

    // Méthode pour définir la référence au UserDashboardController
    public void setDashboardController(UserDashboardController controller) {
        this.dashboardController = controller;
        this.isEmbeddedInDashboard = (controller != null);
    }

    // Ajout : Méthode pour définir le client
    public void setClient(Client client) {
        this.currentClient = client;
    }

    @FXML
    void recherchervol(ActionEvent event) {
        try {
            // Récupérer les paramètres de recherche
            String departure = departurechercher.getText().trim();
            String destination = destinationchercher.getText().trim();
            Date flightDate = null;
            if (flightdatechercher.getValue() != null) {
                flightDate = Date.valueOf(flightdatechercher.getValue());
            }

            // Analyser le nombre de passagers
            int adultCount = 0;
            int childCount = 0;

            try {
                adultCount = Integer.parseInt(adultchercher.getText().trim());
            } catch (NumberFormatException e) {
                System.out.println("Nombre d'adultes invalide : " + e.getMessage());
            }

            try {
                childCount = Integer.parseInt(childchercher.getText().trim());
            } catch (NumberFormatException e) {
                System.out.println("Nombre d'enfants invalide : " + e.getMessage());
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
            resultsController.setClient(currentClient); // Ajout : Passer le client
            // Passer le contrôleur du tableau de bord au contrôleur des résultats si intégré dans le tableau de bord
            if (isEmbeddedInDashboard && dashboardController != null) {
                resultsController.setDashboardController(dashboardController);

                // Charger les résultats dans la zone de contenu du tableau de bord
                dashboardController.loadContentToArea(root);
            } else {
                // Mode autonome - remplacer la scène actuelle
                Stage stage = (Stage) btnRechercherVol.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Résultats de recherche de vols");
            }

        } catch (SQLException e) {
            System.out.println("Erreur de base de données : " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Erreur de chargement FXML : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reloadSearchScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnRechercherVol.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Recherche de vol");
        } catch (IOException e) {
            System.out.println("Erreur lors du rechargement de la scène de recherche : " + e.getMessage());
            e.printStackTrace();
        }
    }
}