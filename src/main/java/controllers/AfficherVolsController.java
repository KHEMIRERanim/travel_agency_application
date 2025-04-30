package controllers;

import entities.Flight;
import services.ServiceFlight;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherVolsController implements Initializable {

    @FXML
    private VBox flightsContainer;

    private final ServiceFlight serviceFlight = new ServiceFlight();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadFlights();
    }

    private void loadFlights() {
        try {
            List<Flight> flights = serviceFlight.recuperer(); // Fetch flights from ServiceFlight
            flightsContainer.getChildren().clear();

            if (flights.isEmpty()) {
                Label emptyLabel = new Label("Aucun vol trouvé dans la base de données.");
                emptyLabel.setStyle("-fx-text-fill: #555; -fx-font-size: 14px;");
                flightsContainer.getChildren().add(emptyLabel);
            } else {
                for (Flight flight : flights) {
                    flightsContainer.getChildren().add(createFlightCard(flight)); // Create a card for each flight
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de la récupération des vols: " + e.getMessage());
        }
    }

    private VBox createFlightCard(Flight flight) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 5);");
        card.setSpacing(10);

        HBox mainContent = new HBox(15);

        // Image section
        VBox imageSection = new VBox();
        imageSection.setPrefWidth(200);
        imageSection.setStyle("-fx-alignment: center;");

        ImageView flightImage = new ImageView();
        flightImage.setFitWidth(180);
        flightImage.setFitHeight(120);
        flightImage.setPreserveRatio(true);

        try {
            if (flight.getImage_url() != null && !flight.getImage_url().isEmpty()) {
                flightImage.setImage(new Image(flight.getImage_url()));
            } else {
                flightImage.setImage(new Image("https://via.placeholder.com/180x120?text=No+Image"));
            }
        } catch (Exception e) {
            flightImage.setImage(new Image("https://via.placeholder.com/180x120?text=Error"));
            System.err.println("Erreur de chargement de l'image: " + e.getMessage());
        }

        imageSection.getChildren().add(flightImage);

        // Info section
        VBox infoSection = new VBox(5);
        HBox.setHgrow(infoSection, Priority.ALWAYS);

        // Header
        HBox header = new HBox(10);
        Label flightNumberLabel = new Label(flight.getFlight_number());
        flightNumberLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        Label airlineLabel = new Label(flight.getAirline());
        airlineLabel.setFont(Font.font("System", 14));
        airlineLabel.setStyle("-fx-text-fill: #555;");
        Label idLabel = new Label("ID: " + flight.getFlight_id());
        idLabel.setStyle("-fx-text-fill: #999;");
        header.getChildren().addAll(flightNumberLabel, airlineLabel, idLabel);

        // Route
        HBox routeBox = new HBox(10);
        Label departureLabel = new Label(flight.getDeparture());
        departureLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label arrowLabel = new Label(" → ");
        arrowLabel.setFont(Font.font("System", 14));
        Label destinationLabel = new Label(flight.getDestination());
        destinationLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        routeBox.getChildren().addAll(departureLabel, arrowLabel, destinationLabel);

        // Times
        HBox timeBox = new HBox(10);
        String departureTime = flight.getDeparture_Time().toLocalDateTime().toLocalTime().format(timeFormatter);
        String arrivalTime = flight.getArrival_Time().toLocalDateTime().toLocalTime().format(timeFormatter);
        String date = flight.getFlight_date().toLocalDate().format(dateFormatter);
        Label timeLabel = new Label(departureTime + " - " + arrivalTime);
        timeLabel.setFont(Font.font("System", 14));
        Label dateLabel = new Label("Date: " + date);
        dateLabel.setFont(Font.font("System", 14));
        timeBox.getChildren().addAll(timeLabel, dateLabel);

        // Info
        HBox infoBox = new HBox(20);
        Label durationLabel = new Label("Durée: " + flight.getFlight_duration() + " min");
        Label seatsLabel = new Label("Places: " + flight.getAvailable_seats());
        Label priceLabel = new Label("Prix: " + flight.getPrice() + " €");
        priceLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
        infoBox.getChildren().addAll(durationLabel, seatsLabel, priceLabel);

        infoSection.getChildren().addAll(header, routeBox, timeBox, infoBox);
        mainContent.getChildren().addAll(imageSection, infoSection);
        card.getChildren().add(mainContent);

        return card;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
