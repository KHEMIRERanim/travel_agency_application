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
import java.sql.Date;
import java.sql.Timestamp;
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
        this.currentClient = client;
        System.out.println("Client set: " + (client != null ? client.toString() : "null"));
    }

    public void setSourceController(ChercherVolController controller) {
        this.sourceController = controller;
    }

    public void setDashboardController(UserDashboardController controller) {
        this.dashboardController = controller;
        this.isEmbeddedInDashboard = (controller != null);
        System.out.println("DashboardController set: " + (controller != null ? "not null" : "null"));
    }

    public void setRequiredSeats(int seats) {
        this.requiredSeats = seats;
        System.out.println("Required seats set: " + seats);
    }

    @FXML
    public void initialize() {
        WebEngine engine = webView.getEngine();
        // Réinitialiser le WebView
        engine.loadContent(""); // Vider le contenu initial
        System.out.println("WebView reset");

        String htmlPath = getClass().getResource("/real-time-flights.html").toExternalForm();
        System.out.println("HTML path: " + htmlPath);
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
                System.out.println("JavaFX bridge initialized successfully: " + (window.getMember("javafxBridge") != null));
            } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                System.out.println("Failed to load WebView: " + engine.getLoadWorker().getException());
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec du chargement",
                        "Impossible de charger la page: " + engine.getLoadWorker().getException().getMessage());
            }
        });

        engine.setOnError(event -> {
            System.out.println("WebView JS error: " + event.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur JavaScript", "Erreur dans le WebView",
                    "Erreur JavaScript: " + event.getMessage());
        });

        engine.load(htmlPath);
    }

    public class JavaFXBridge {
        public void confirmFlight(String flightDataJson) {
            System.out.println("Java: confirmFlight method reached with data: " + (flightDataJson != null ? flightDataJson.substring(0, Math.min(100, flightDataJson.length())) + "..." : "null"));
            Platform.runLater(() -> {
                try {
                    // Vérifier si JSON est valide
                    if (flightDataJson == null || flightDataJson.trim().isEmpty()) {
                        throw new IllegalArgumentException("JSON data is null or empty");
                    }

                    System.out.println("Parsing JSON...");
                    JSONObject json = new JSONObject(flightDataJson);
                    System.out.println("JSON parsed: " + json.toString());

                    // Vérifier les champs requis
                    String[] requiredFields = {"flight_id", "flight_number", "departure", "arrival", "airline",
                            "departure_time", "arrival_time", "flight_date", "price", "available_seats", "flight_duration"};
                    for (String field : requiredFields) {
                        if (!json.has(field)) {
                            throw new IllegalArgumentException("Missing required field: " + field);
                        }
                    }

                    // Forcer un flight_id qui existe dans la table flight (solution temporaire)
                    int flightId = 3; // Forcer flight_id = 3 (supposé existant dans la table flight)
                    System.out.println("Forcing flightId to: " + flightId + " to satisfy foreign key constraint");

                    // Parse times
                    String depTimeStr = json.getString("departure_time");
                    String arrTimeStr = json.getString("arrival_time");
                    Timestamp departureTime = parseTimestamp(depTimeStr);
                    Timestamp arrivalTime = parseTimestamp(arrTimeStr);
                    System.out.println("Departure time: " + departureTime + ", Arrival time: " + arrivalTime);

                    // Parse flight date
                    String flightDateStr = json.getString("flight_date");
                    Date flightDate;
                    try {
                        flightDate = Date.valueOf(flightDateStr.substring(0, 10));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid flight date format: " + flightDateStr);
                    }
                    System.out.println("Flight date: " + flightDate);

                    // Parse other fields
                    int flightDuration = json.getInt("flight_duration");
                    double price = json.getDouble("price");
                    int availableSeats = json.getInt("available_seats");
                    System.out.println("Duration: " + flightDuration + ", Price: " + price + ", Seats: " + availableSeats);

                    // Créer Flight object avec une image_url par défaut
                    Flight flight = new Flight(
                            flightId,
                            flightDuration,
                            json.getString("flight_number"),
                            availableSeats,
                            json.getString("departure"),
                            json.getString("arrival"),
                            json.getString("airline"),
                            departureTime,
                            arrivalTime,
                            flightDate,
                            price,
                            "/img.png" // Ajout d'une URL d'image par défaut
                    );
                    System.out.println("Flight created with image_url: " + flight);

                    // Validate client
                    if (currentClient == null) {
                        System.out.println("No client connected");
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun client connecté",
                                "Veuillez vous connecter pour effectuer une réservation.");
                        return;
                    }

                    // Validate seats
                    if (flight.getAvailable_seats() < requiredSeats) {
                        System.out.println("Insufficient seats: available=" + flight.getAvailable_seats() + ", required=" + requiredSeats);
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Sièges insuffisants",
                                "Le nombre de sièges disponibles est insuffisant pour votre réservation.");
                        return;
                    }

                    // Load FlightBookingConfirmation.fxml
                    System.out.println("Attempting to load /FlightBookingConfirmation.fxml");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FlightBookingConfirmation.fxml"));
                    if (loader.getLocation() == null) {
                        throw new IOException("FXML file '/FlightBookingConfirmation.fxml' not found in classpath");
                    }
                    Parent root = loader.load();
                    System.out.println("FlightBookingConfirmation.fxml loaded successfully");

                    // Set client and flight in controller
                    FlightBookingController controller = loader.getController();
                    if (controller == null) {
                        throw new IllegalStateException("FlightBookingController is null");
                    }
                    controller.setClient(currentClient);
                    controller.setFlight(flight, requiredSeats);
                    System.out.println("Controller configured with client and flight");

                    // Navigate to new scene
                    Stage stage = (Stage) webView.getScene().getWindow();
                    if (stage == null) {
                        throw new IllegalStateException("Stage is null");
                    }
                    System.out.println("Stage retrieved: " + stage);
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Confirmation de la réservation");
                    stage.show();
                    System.out.println("Navigated to FlightBookingConfirmation.fxml");

                } catch (IllegalArgumentException e) {
                    System.err.println("Validation error in confirmFlight: " + e.getMessage());
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Données invalides",
                            "Erreur de validation: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("IOException while loading FXML: " + e.getMessage());
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement FXML",
                            "Impossible de charger FlightBookingConfirmation.fxml: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Unexpected error in confirmFlight: " + e.getMessage());
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur inattendue",
                            "Une erreur s'est produite: " + e.getMessage());
                }
            });
        }

        private Timestamp parseTimestamp(String timeStr) {
            try {
                if (timeStr == null) {
                    throw new IllegalArgumentException("Timestamp is null");
                }
                if (timeStr.contains("T")) {
                    return Timestamp.valueOf(timeStr.replace("T", " ").replace("Z", "").substring(0, 19));
                } else {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    java.util.Date parsedDate = format.parse(timeStr);
                    return new Timestamp(parsedDate.getTime());
                }
            } catch (ParseException | IllegalArgumentException e) {
                System.err.println("Error parsing timestamp: " + timeStr + ", error: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de format de date",
                        "Impossible de parser la date: " + timeStr);
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
                    "Impossible de revenir à la recherche : " + e.getMessage());
            e.printStackTrace();
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