package controllers;
import services.ServiceFlight;
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
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class RealTimeFlightsController {

    @FXML
    private WebView webView;
    @FXML
    private Button backButton;
    @FXML
    private Button fetchFlightsButton;

    private Client currentClient;
    private ChercherVolController sourceController;
    private UserDashboardController dashboardController;
    private boolean isEmbeddedInDashboard = false;
    private int requiredSeats = 1;
    private String departure;
    private String destination;
    private Date flightDate;

    private static final String API_KEY = "b54ed24b5ede2e30cbeeb4202c6b5cb4"; // Remplacez par votre clé API AviationStack
    private static final String API_URL = "http://api.aviationstack.com/v1/flights?access_key=" + API_KEY;

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

    public void setSearchCriteria(String departure, String destination, Date flightDate) {
        this.departure = departure != null ? departure.trim() : "";
        this.destination = destination != null ? destination.trim() : "";
        this.flightDate = flightDate;
        System.out.println("Search criteria set: departure=" + this.departure + ", destination=" + this.destination + ", flightDate=" + this.flightDate);
    }

    @FXML
    public void initialize() {
        WebEngine engine = webView.getEngine();
        engine.loadContent("");
        System.out.println("WebView reset");

        String htmlPath = getClass().getResource("/real-time-flights.html").toExternalForm();
        System.out.println("HTML path: " + htmlPath);
        if (htmlPath == null) {
            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier non trouvé",
                    "Impossible de charger real-time-flights.html"));
            return;
        }

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                System.out.println("WebView loaded successfully, setting up bridge...");
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("javafxBridge", new JavaFXBridge());
                System.out.println("JavaFX bridge initialized successfully: " + (window.getMember("javafxBridge") != null));
                fetchRealTimeFlights(null);
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

    @FXML
    public void fetchRealTimeFlights(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                StringBuilder depUrl = new StringBuilder(API_URL);
                depUrl.append("&dep_iata=TUN");
                StringBuilder arrUrl = new StringBuilder(API_URL);
                arrUrl.append("&arr_iata=TUN");

                if (!departure.isEmpty() && !departure.equalsIgnoreCase("TUN")) {
                    depUrl.append("&dep_iata=").append(departure);
                    arrUrl.append("&dep_iata=").append(departure);
                }
                if (!destination.isEmpty() && !destination.equalsIgnoreCase("TUN")) {
                    depUrl.append("&arr_iata=").append(destination);
                    arrUrl.append("&arr_iata=").append(destination);
                }

                System.out.println("Departure API URL: " + depUrl.toString());
                String depResponse = callAviationStackAPI(depUrl.toString());
                System.out.println("Departure API Response: " + depResponse);

                System.out.println("Arrival API URL: " + arrUrl.toString());
                String arrResponse = callAviationStackAPI(arrUrl.toString());
                System.out.println("Arrival API Response: " + arrResponse);

                JSONObject depJsonResponse = new JSONObject(depResponse);
                JSONArray depFlights = depJsonResponse.getJSONArray("data");

                JSONObject arrJsonResponse = new JSONObject(arrResponse);
                JSONArray arrFlights = arrJsonResponse.getJSONArray("data");

                JSONArray flights = new JSONArray();
                for (int i = 0; i < depFlights.length(); i++) {
                    flights.put(depFlights.getJSONObject(i));
                }
                for (int i = 0; i < arrFlights.length(); i++) {
                    JSONObject flight = arrFlights.getJSONObject(i);
                    boolean isDuplicate = false;
                    for (int j = 0; j < flights.length(); j++) {
                        if (flights.getJSONObject(j).getJSONObject("flight").optString("iata", "")
                                .equals(flight.getJSONObject("flight").optString("iata", ""))) {
                            isDuplicate = true;
                            break;
                        }
                    }
                    if (!isDuplicate) {
                        flights.put(flight);
                    }
                }

                if (flights.length() == 0) {
                    System.out.println("No flights returned from API, adding mock data...");
                    JSONObject mockFlight1 = new JSONObject();
                    mockFlight1.put("flight", new JSONObject().put("iata", "TU123"));
                    mockFlight1.put("departure", new JSONObject()
                            .put("iata", "TUN")
                            .put("scheduled", "2025-05-13T10:00:00+00:00"));
                    mockFlight1.put("arrival", new JSONObject()
                            .put("iata", "CDG")
                            .put("scheduled", "2025-05-13T12:30:00+00:00"));
                    mockFlight1.put("airline", new JSONObject().put("name", "Tunisair"));
                    mockFlight1.put("flight_date", "2025-05-13");
                    flights.put(mockFlight1);

                    JSONObject mockFlight2 = new JSONObject();
                    mockFlight2.put("flight", new JSONObject().put("iata", "AF456"));
                    mockFlight2.put("departure", new JSONObject()
                            .put("iata", "JFK")
                            .put("scheduled", "2025-05-13T15:00:00+00:00"));
                    mockFlight2.put("arrival", new JSONObject()
                            .put("iata", "TUN")
                            .put("scheduled", "2025-05-13T23:00:00+00:00"));
                    mockFlight2.put("airline", new JSONObject().put("name", "Air France"));
                    mockFlight2.put("flight_date", "2025-05-13");
                    flights.put(mockFlight2);
                }
                System.out.println("Total flights received: " + flights.length());

                JSONArray tunisFlights = new JSONArray();
                for (int i = 0; i < flights.length(); i++) {
                    JSONObject flight = flights.getJSONObject(i);
                    JSONObject departure = flight.getJSONObject("departure");
                    JSONObject arrival = flight.getJSONObject("arrival");
                    String depIata = departure.optString("iata", "");
                    String arrIata = arrival.optString("iata", "");
                    if (depIata.equalsIgnoreCase("TUN") || arrIata.equalsIgnoreCase("TUN")) {
                        String flightNumber = flight.getJSONObject("flight").optString("iata", "N/A");
                        flight.put("flight_number", flightNumber);
                        flight.put("departure_airport", depIata);
                        flight.put("arrival_airport", arrIata);
                        flight.put("airline_name", flight.getJSONObject("airline").optString("name", "N/A"));
                        flight.put("departure_time", departure.optString("scheduled", "N/A"));
                        flight.put("arrival_time", arrival.optString("scheduled", "N/A"));
                        flight.put("flight_date", flight.optString("flight_date", "N/A"));
                        flight.put("price", 200.0);
                        flight.put("available_seats", 50);
                        flight.put("flight_duration", 120);
                        tunisFlights.put(flight);
                    }
                }
                System.out.println("Flights after TUN filter: " + tunisFlights.length());

                JSONArray filteredFlights = tunisFlights;
                System.out.println("Flights after date filter: " + filteredFlights.length());

                JSONArray finalFlights = new JSONArray();
                for (int i = 0; i < filteredFlights.length(); i++) {
                    JSONObject flight = filteredFlights.getJSONObject(i);
                    if (flight.getInt("available_seats") >= requiredSeats) {
                        finalFlights.put(flight);
                    }
                }
                System.out.println("Final flights after seat filter: " + finalFlights.length());

                String flightsJson = finalFlights.toString();
                System.out.println("Flights JSON sent to WebView: " + flightsJson);
                WebEngine engine = webView.getEngine();
                JSObject window = (JSObject) engine.executeScript("window");
                window.call("displayFlights", flightsJson);

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur API",
                        "Impossible de récupérer les vols: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur inattendue",
                        "Une erreur s'est produite: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private String callAviationStackAPI(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                errorResponse.append(errorLine);
            }
            errorReader.close();
            throw new IOException("Échec de la requête API, code: " + responseCode + ", message: " + errorResponse.toString());
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
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

    public class JavaFXBridge {
        public void confirmFlight(String flightDataJson) {
            System.out.println("Java: confirmFlight method reached with data: " + (flightDataJson != null ? flightDataJson.substring(0, Math.min(100, flightDataJson.length())) + "..." : "null"));
            Platform.runLater(() -> {
                try {
                    if (flightDataJson == null || flightDataJson.trim().isEmpty()) {
                        throw new IllegalArgumentException("JSON data is null or empty");
                    }

                    System.out.println("Parsing JSON...");
                    JSONObject json = new JSONObject(flightDataJson);
                    System.out.println("JSON parsed: " + json.toString());

                    String[] requiredFields = {"flight_number", "departure_airport", "arrival_airport", "airline_name",
                            "departure_time", "arrival_time", "flight_date", "price", "available_seats", "flight_duration"};
                    for (String field : requiredFields) {
                        if (!json.has(field)) {
                            throw new IllegalArgumentException("Missing required field: " + field);
                        }
                    }

                    String flightNumber = json.getString("flight_number");
                    String depTimeStr = json.getString("departure_time");
                    String arrTimeStr = json.getString("arrival_time");
                    Timestamp departureTime = parseTimestamp(depTimeStr);
                    Timestamp arrivalTime = parseTimestamp(arrTimeStr);
                    System.out.println("Departure time: " + departureTime + ", Arrival time: " + arrivalTime);

                    String flightDateStr = json.getString("flight_date");
                    Date flightDate;
                    try {
                        flightDate = Date.valueOf(flightDateStr.substring(0, 10));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid flight date format: " + flightDateStr);
                    }
                    System.out.println("Flight date: " + flightDate);

                    int flightDuration = json.getInt("flight_duration");
                    double price = json.getDouble("price");
                    int availableSeats = json.getInt("available_seats");
                    System.out.println("Duration: " + flightDuration + ", Price: " + price + ", Seats: " + availableSeats);

                    // Création initiale de l'objet Flight avec flight_id = 0
                    Flight flight = new Flight(
                            0, // flight_id sera mis à jour après vérification
                            flightDuration,
                            flightNumber,
                            availableSeats,
                            json.getString("departure_airport"),
                            json.getString("arrival_airport"),
                            json.getString("airline_name"),
                            departureTime,
                            arrivalTime,
                            flightDate,
                            price,
                            "/img.png"
                    );
                    System.out.println("Flight created with temporary ID: " + flight);

                    ServiceFlight flightService = new ServiceFlight();
                    Flight existingFlight = flightService.findByFlightNumber(flightNumber);
                    if (existingFlight == null) {
                        // Ajouter le vol s'il n'existe pas
                        flight = flightService.addRealTimeFlight(flight); // Use addRealTimeFlight instead of addFlight
                        System.out.println("New flight added with ID: " + flight.getFlight_id());
                    } else {
                        // Utiliser le vol existant
                        flight = existingFlight;
                        System.out.println("Existing flight found with ID: " + flight.getFlight_id());
                    }

                    if (currentClient == null) {
                        System.out.println("No client connected");
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun client connecté",
                                "Veuillez vous connecter pour effectuer une réservation.");
                        return;
                    }

                    if (flight.getAvailable_seats() < requiredSeats) {
                        System.out.println("Insufficient seats: available=" + flight.getAvailable_seats() + ", required=" + requiredSeats);
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Sièges insuffisants",
                                "Le nombre de sièges disponibles est insuffisant pour votre réservation.");
                        return;
                    }

                    System.out.println("Attempting to load /FlightBookingConfirmation.fxml");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FlightBookingConfirmation.fxml"));
                    if (loader.getLocation() == null) {
                        throw new IOException("FXML file '/FlightBookingConfirmation.fxml' not found in classpath");
                    }
                    Parent root = loader.load();
                    System.out.println("FlightBookingConfirmation.fxml loaded successfully");

                    FlightBookingController controller = loader.getController();
                    if (controller == null) {
                        throw new IllegalStateException("FlightBookingController is null");
                    }
                    controller.setClient(currentClient);
                    controller.setFlight(flight, requiredSeats);
                    System.out.println("Controller configured with client and flight");

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
}