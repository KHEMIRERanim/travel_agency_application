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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.Timestamp;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class RealTimeFlightsController {

    @FXML
    private WebView webView;
    @FXML
    private Button backButton;

    private Client currentClient;
    private ChercherVolController sourceController;
    private UserDashboardController dashboardController;
    private boolean isEmbeddedInDashboard = false;
    private int requiredSeats = 1;

    public void setClient(Client client) {
        this.currentClient = client != null ? client : new Client("Test", "User", "test@example.com", 1, "1234567890", "1990-01-01");
        System.out.println("Client set: " + (currentClient != null ? currentClient.getNom() : "null"));
    }

    public void setSourceController(ChercherVolController controller) {
        this.sourceController = controller;
    }

    public void setDashboardController(UserDashboardController controller) {
        this.dashboardController = controller;
        this.isEmbeddedInDashboard = (controller != null);
        System.out.println("DashboardController set: " + (dashboardController != null ? "not null" : "null"));
    }

    public void setRequiredSeats(int seats) {
        this.requiredSeats = seats;
        System.out.println("Required seats set: " + seats);
    }

    @FXML
    public void initialize() {
        WebEngine engine = webView.getEngine();
        String htmlPath = getClass().getResource("/real-time-flights.html").toExternalForm();
        if (htmlPath == null) {
            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier non trouvé",
                    "Impossible de charger real-time-flights.html"));
            return;
        }

        engine.loadContent("<html><body><h1>Chargement...</h1></body></html>");

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                System.out.println("WebView loaded successfully, setting up bridge...");
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("javafxBridge", new JavaFXBridge());
                System.out.println("JavaFX bridge initialized successfully");
            } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                System.out.println("Failed to load WebView: " + engine.getLoadWorker().getException());
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec du chargement",
                        "Impossible de charger la page: " + engine.getLoadWorker().getException().getMessage());
            }
        });

        engine.setOnError(event -> System.out.println("WebView error: " + event.getMessage()));

        engine.load(htmlPath);
    }

    public class JavaFXBridge {
        public void confirmFlight(String flightDataJson) {
            System.out.println("confirmFlight called with data: " + flightDataJson);
            Platform.runLater(() -> {
                try {
                    // Show JavaFX alert
                    showAlert(Alert.AlertType.INFORMATION, "Confirmation", "Clic sur Confirmer",
                            "Données reçues depuis JavaScript: " + flightDataJson);

                    // Parse JSON
                    JSONObject json = new JSONObject(flightDataJson);

                    // Create Flight object
                    Flight flight = new Flight(
                            json.getString("flight_id").hashCode(),
                            json.has("flight_duration") ? json.getInt("flight_duration") : 120,
                            json.getString("flight_number"),
                            json.has("available_seats") ? json.getInt("available_seats") : 50,
                            json.getString("departure"),
                            json.getString("arrival"),
                            json.getString("airline"),
                            parseTimestamp(json.getString("departure_time")),
                            parseTimestamp(json.getString("arrival_time")),
                            Date.valueOf(json.getString("flight_date").substring(0, 10)),
                            json.has("price") ? json.getDouble("price") : 200.00,
                            null
                    );
                    System.out.println("Created Flight: number=" + flight.getFlight_number());

                    // Validate client
                    if (currentClient == null) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun client connecté",
                                "Veuillez vous connecter.");
                        return;
                    }

                    // Validate seats
                    if (flight.getAvailable_seats() < requiredSeats) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Sièges insuffisants",
                                "Nombre de sièges insuffisant.");
                        return;
                    }

                    // Load FXML
                    System.out.println("Attempting to load /FlightBookingConfirmation.fxml");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FlightBookingConfirmation.fxml"));
                    if (loader.getLocation() == null) {
                        throw new IOException("FXML file '/FlightBookingConfirmation.fxml' not found");
                    }
                    Parent root = loader.load();
                    System.out.println("FlightBookingConfirmation.fxml loaded successfully");

                    // Set client and flight
                    FlightBookingController controller = loader.getController();
                    controller.setClient(currentClient);
                    controller.setFlight(flight, requiredSeats);

                    // Navigate
                    Stage stage = (Stage) webView.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Confirmation de la réservation");
                    stage.show();
                    System.out.println("Navigated to FlightBookingConfirmation.fxml");

                } catch (IOException e) {
                    System.err.println("IOException: " + e.getMessage());
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                            "Impossible de charger la page: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Unexpected error: " + e.getMessage());
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur inattendue",
                            "Une erreur s'est produite: " + e.getMessage());
                }
            });
        }

        private Timestamp parseTimestamp(String timeStr) {
            try {
                if (timeStr.contains("T")) {
                    return Timestamp.valueOf(timeStr.replace("T", " ").replace("Z", "").substring(0, 19));
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return new Timestamp(format.parse(timeStr).getTime());
            } catch (ParseException | IllegalArgumentException e) {
                System.out.println("Error parsing timestamp: " + e.getMessage());
                return new Timestamp(System.currentTimeMillis());
            }
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
            Parent root = loader.load();
            ChercherVolController controller = loader.getController();
            controller.setClient(currentClient);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Recherche de vol");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation",
                    "Impossible de revenir à la recherche: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}