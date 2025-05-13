package controllers;

import entities.Client;
import entities.Flight;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
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
    private Button btnmyreservations;

    @FXML
    private Button btnRealTimeFlights;

    @FXML
    private Button btnWeather;

    private UserDashboardController dashboardController;
    private boolean isEmbeddedInDashboard = false;
    private Client currentClient;

    public void setDashboardController(UserDashboardController controller) {
        this.dashboardController = controller;
        this.isEmbeddedInDashboard = (controller != null);
    }

    public void setClient(Client client) {
        this.currentClient = client;
    }

    @FXML
    public void initialize() {
        departurechercher.setText("");
        destinationchercher.setText("");
        adultchercher.setText("1");
        childchercher.setText("0");
        checkFxmlResource("/RealTimeFlights.fxml", "RealTimeFlights.fxml");
        checkFxmlResource("/MyReservations.fxml", "MyReservations.fxml");
        checkFxmlResource("/FlightSearchResults.fxml", "FlightSearchResults.fxml");
        checkFxmlResource("/ChercherVol.fxml", "ChercherVol.fxml");
        checkHtmlResource("/weather.html", "weather.html");
    }

    private void checkFxmlResource(String resourcePath, String resourceName) {
        if (getClass().getResource(resourcePath) == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Ressource manquante",
                    "Le fichier " + resourceName + " est introuvable.");
        }
    }

    private void checkHtmlResource(String resourcePath, String resourceName) {
        if (getClass().getResource(resourcePath) == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Ressource manquante",
                    "Le fichier " + resourceName + " est introuvable.");
        }
    }

    @FXML
    void recherchervol(ActionEvent event) {
        try {
            String departure = departurechercher.getText().trim();
            String destination = destinationchercher.getText().trim();
            Date flightDate = flightdatechercher.getValue() != null ? Date.valueOf(flightdatechercher.getValue()) : null;

            int adultCount = parsePassengerCount(adultchercher.getText().trim(), "adultes");
            if (adultCount == -1) return;

            int childCount = parsePassengerCount(childchercher.getText().trim(), "enfants");
            if (childCount == -1) return;

            int totalPassengers = adultCount + childCount;

            if (departure.isEmpty() && destination.isEmpty() && flightDate == null && totalPassengers == 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Critères insuffisants",
                        "Veuillez spécifier au moins un critère de recherche.");
                return;
            }

            ServiceFlight serviceFlight = new ServiceFlight();
            List<Flight> allFlights = serviceFlight.recuperer();

            final String finalDeparture = departure;
            final String finalDestination = destination;
            final Date finalFlightDate = flightDate;
            final int finalTotalPassengers = totalPassengers;

            List<Flight> filteredFlights = allFlights.stream()
                    .filter(flight -> finalDeparture.isEmpty() || flight.getDeparture().equalsIgnoreCase(finalDeparture))
                    .filter(flight -> finalDestination.isEmpty() || flight.getDestination().equalsIgnoreCase(finalDestination))
                    .filter(flight -> finalFlightDate == null || flight.getFlight_date().equals(finalFlightDate))
                    .filter(flight -> finalTotalPassengers == 0 || flight.getAvailable_seats() >= finalTotalPassengers)
                    .collect(Collectors.toList());

            java.net.URL fxmlLocation = getClass().getResource("/FlightSearchResults.fxml");
            if (fxmlLocation == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier FXML non trouvé",
                        "Le fichier /FlightSearchResults.fxml n'a pas été trouvé dans les ressources.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            FlightSearchResultsController resultsController = loader.getController();
            resultsController.setFlights(filteredFlights, finalTotalPassengers);
            resultsController.setSourceController(this);
            resultsController.setClient(currentClient);

            if (isEmbeddedInDashboard && dashboardController != null) {
                resultsController.setDashboardController(dashboardController);
                dashboardController.loadContentToArea(root);
            } else {
                Stage stage = (Stage) btnRechercherVol.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Résultats de recherche de vols");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de base de données",
                    "Une erreur est survenue lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement FXML",
                    "Une erreur est survenue lors du chargement de la page : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void showMyReservations(ActionEvent event) {
        try {
            if (currentClient == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun client connecté",
                        "Veuillez vous connecter pour voir vos réservations.");
                return;
            }

            java.net.URL fxmlLocation = getClass().getResource("/MyReservations.fxml");
            if (fxmlLocation == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier FXML non trouvé",
                        "Le fichier /MyReservations.fxml n'a pas été trouvé dans les ressources.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            MyReservationsController controller = loader.getController();
            controller.setClient(currentClient);

            if (isEmbeddedInDashboard && dashboardController != null) {
                controller.setDashboardController(dashboardController);
                dashboardController.loadContentToArea(root);
            } else {
                Stage stage = (Stage) btnmyreservations.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Mes Réservations");
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement FXML",
                    "Impossible de charger la page des réservations : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void showRealTimeFlights(ActionEvent event) {
        try {
            int adultCount = parsePassengerCount(adultchercher.getText().trim(), "adultes");
            if (adultCount == -1) return;

            int childCount = parsePassengerCount(childchercher.getText().trim(), "enfants");
            if (childCount == -1) return;

            int totalPassengers = adultCount + childCount;
            if (totalPassengers <= 0) totalPassengers = 1;

            // Récupérer les critères de recherche
            String departure = departurechercher.getText().trim();
            String destination = destinationchercher.getText().trim();
            Date flightDate = flightdatechercher.getValue() != null ? Date.valueOf(flightdatechercher.getValue()) : null;

            java.net.URL fxmlLocation = getClass().getResource("/RealTimeFlights.fxml");
            if (fxmlLocation == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier FXML non trouvé",
                        "Le fichier /RealTimeFlights.fxml n'a pas été trouvé dans les ressources.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            RealTimeFlightsController controller = loader.getController();
            controller.setClient(currentClient);
            controller.setSourceController(this);
            controller.setRequiredSeats(totalPassengers);
            // Passer les critères de recherche
            controller.setSearchCriteria(departure, destination, flightDate);

            if (isEmbeddedInDashboard && dashboardController != null) {
                controller.setDashboardController(dashboardController);
                dashboardController.loadContentToArea(root);
            } else {
                Stage stage = (Stage) btnRealTimeFlights.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Vols en temps réel en Tunisie");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement FXML",
                    "Impossible de charger la page des vols en temps réel : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void showWeather(ActionEvent event) {
        try {
            // Create WebView to display weather.html
            WebView webView = new WebView();
            java.net.URL htmlLocation = getClass().getResource("/weather.html");
            if (htmlLocation == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier HTML non trouvé",
                        "Le fichier /weather.html n'a pas été trouvé dans les ressources.");
                return;
            }
            webView.getEngine().load(htmlLocation.toExternalForm());

            // Create a new scene
            Scene scene = new Scene(webView, 400, 300);

            if (isEmbeddedInDashboard && dashboardController != null) {
                dashboardController.loadContentToArea(webView);
            } else {
                Stage stage = (Stage) btnWeather.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Météo en Temps Réel - Tunis");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur inattendue",
                    "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reloadSearchScene() {
        try {
            java.net.URL fxmlLocation = getClass().getResource("/ChercherVol.fxml");
            if (fxmlLocation == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier FXML non trouvé",
                        "Le fichier /ChercherVol.fxml n'a pas été trouvé dans les ressources.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Stage stage = (Stage) btnRechercherVol.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Recherche de vol");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement FXML",
                    "Impossible de recharger la scène de recherche : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int parsePassengerCount(String input, String fieldName) {
        try {
            int count = Integer.parseInt(input.trim());
            if (count < 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Nombre de " + fieldName + " invalide",
                        "Le nombre de " + fieldName + " ne peut pas être négatif.");
                return -1;
            }
            return count;
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Nombre de " + fieldName + " invalide",
                    "Veuillez entrer un nombre valide pour " + fieldName + " : " + e.getMessage());
            return -1;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}