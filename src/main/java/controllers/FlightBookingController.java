package controllers;

import entities.Client;
import entities.Flight;
import entities.ReservationVol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.ServiceFlight;
import services.ServiceReservationVol;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class FlightBookingController {

    @FXML
    private VBox flightDetailCard;

    @FXML
    private ImageView airlineImageView;

    @FXML
    private Label airlineLabel;

    @FXML
    private Label flightNumberLabel;

    @FXML
    private Label departureLabel;

    @FXML
    private Label durationLabel;

    @FXML
    private Label destinationLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label seatsLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label passengersLabel;

    @FXML
    private TextField tf_nom;

    @FXML
    private TextField tf_prenom;

    @FXML
    private TextField tf_email;

    @FXML
    private TextField tf_telephone;

    @FXML
    private TextField tf_dateNaissance;

    @FXML
    private TextField tf_passengers;

    @FXML
    private Label statusLabel;

    private Client currentClient;
    private Flight selectedFlight;
    private int requiredPassengers;
    private List<String> selectedSeats = new ArrayList<>();

    public void setClient(Client client) {
        this.currentClient = client;
        displayClientDetails();
    }

    public void setFlight(Flight flight, int requiredPassengers) {
        this.selectedFlight = flight;
        this.requiredPassengers = requiredPassengers;
        displayFlightDetails();
    }

    private void displayClientDetails() {
        if (currentClient != null) {
            tf_nom.setText(currentClient.getNom() != null ? currentClient.getNom() : "");
            tf_prenom.setText(currentClient.getPrenom() != null ? currentClient.getPrenom() : "");
            tf_email.setText(currentClient.getEmail() != null ? currentClient.getEmail() : "");
            tf_telephone.setText(currentClient.getNumero_telephone() != 0 ? String.valueOf(currentClient.getNumero_telephone()) : "");
            tf_dateNaissance.setText(currentClient.getDate_de_naissance() != null ? currentClient.getDate_de_naissance() : "");
            tf_passengers.setText(String.valueOf(requiredPassengers));

            tf_nom.setEditable(false);
            tf_prenom.setEditable(false);
            tf_email.setEditable(false);
            tf_telephone.setEditable(false);
            tf_dateNaissance.setEditable(false);
            tf_passengers.setEditable(false);
        } else {
            tf_nom.setText("");
            tf_prenom.setText("");
            tf_email.setText("");
            tf_telephone.setText("");
            tf_dateNaissance.setText("");
            tf_passengers.setText(String.valueOf(requiredPassengers));
            statusLabel.setText("Aucun client connecté");
        }
    }

    private void displayFlightDetails() {
        if (selectedFlight != null) {
            try {
                String imagePath = selectedFlight.getImage_url();
                if (imagePath != null && !imagePath.isEmpty()) {
                    if (!imagePath.startsWith("http")) {
                        String projectDir = System.getProperty("user.dir");
                        File file = new File(projectDir, "src/" + imagePath);
                        if (file.exists()) {
                            imagePath = file.toURI().toString();
                        } else {
                            imagePath = "https://via.placeholder.com/80x40?text=Not+Found";
                        }
                    }
                    airlineImageView.setImage(new Image(imagePath));
                } else {
                    airlineImageView.setImage(new Image("https://via.placeholder.com/80x40?text=No+Image"));
                }
            } catch (Exception e) {
                airlineImageView.setImage(new Image("https://via.placeholder.com/80x40?text=Error"));
            }

            airlineLabel.setText(selectedFlight.getAirline());
            flightNumberLabel.setText("Flight " + selectedFlight.getFlight_number());

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            departureLabel.setText(selectedFlight.getDeparture() + "\n" + timeFormat.format(selectedFlight.getDeparture_Time()));
            destinationLabel.setText(selectedFlight.getDestination() + "\n" + timeFormat.format(selectedFlight.getArrival_Time()));
            durationLabel.setText(formatDuration(selectedFlight.getFlight_duration()));
            double totalPrice = selectedFlight.getPrice() * requiredPassengers;
            priceLabel.setText(String.format("$%.2f", totalPrice)); // Ensure proper formatting
            seatsLabel.setText(selectedFlight.getAvailable_seats() + " seats available");
            dateLabel.setText(dateFormat.format(selectedFlight.getFlight_date()));
            passengersLabel.setText(requiredPassengers + " passenger(s)");
        } else {
            airlineLabel.setText("N/A");
            flightNumberLabel.setText("N/A");
            departureLabel.setText("N/A");
            destinationLabel.setText("N/A");
            durationLabel.setText("N/A");
            priceLabel.setText("$0.00");
            seatsLabel.setText("0 seats");
            dateLabel.setText("N/A");
            passengersLabel.setText("0 passenger(s)");
            statusLabel.setText("Aucun vol sélectionné");
        }
    }

    private String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%dh %02dm", hours, mins);
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FlightSearchResults.fxml"));
            Parent root = loader.load();
            FlightSearchResultsController controller = loader.getController();
            controller.setClient(currentClient);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Résultats de recherche de vols");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation",
                    "Impossible de retourner aux résultats de recherche: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void confirmBooking(ActionEvent event) {
        try {
            if (currentClient == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun client connecté",
                        "Veuillez vous connecter pour effectuer une réservation.");
                return;
            }
            if (selectedFlight == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun vol sélectionné",
                        "Veuillez sélectionner un vol pour continuer.");
                return;
            }
            if (selectedFlight.getAvailable_seats() < requiredPassengers) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Sièges insuffisants",
                        "Le nombre de sièges disponibles est insuffisant pour votre réservation.");
                return;
            }
            if (selectedSeats.size() != requiredPassengers) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Sièges non sélectionnés",
                        "Veuillez sélectionner exactement " + requiredPassengers + " siège(s) avant de confirmer.");
                return;
            }

            // Convert selectedSeats list to a comma-separated string
            String selectedSeatsString = String.join(",", selectedSeats);

            ServiceReservationVol reservationService = new ServiceReservationVol();
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            ReservationVol reservation = new ReservationVol(
                    currentClient.getId_client(),
                    "Confirmed",
                    currentDate,
                    selectedFlight.getPrice() * requiredPassengers,
                    currentClient.getPrenom() + " " + currentClient.getNom(),
                    selectedFlight.getFlight_id(),
                    selectedSeatsString // Include selected seats in the reservation
            );

            reservationService.ajouter(reservation);
            ServiceFlight flightService = new ServiceFlight();
            selectedFlight.setAvailable_seats(selectedFlight.getAvailable_seats() - requiredPassengers);
            flightService.modifier(selectedFlight);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation confirmée",
                    "Votre réservation a été ajoutée avec succès ! Sièges sélectionnés : " + selectedSeats.toString());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDashboard.fxml"));
            Parent root = loader.load();
            UserDashboardController controller = loader.getController();
            controller.setClient(currentClient);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord");
            stage.show();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la réservation",
                    "Impossible d'ajouter la réservation: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation",
                    "Impossible de retourner au tableau de bord: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void openSeatSelection(ActionEvent event) {
        Stage seatSelectionStage = new Stage();
        seatSelectionStage.initModality(Modality.APPLICATION_MODAL);
        seatSelectionStage.setTitle("Sélection des sièges");

        GridPane seatGrid = new GridPane();
        seatGrid.setHgap(5);
        seatGrid.setVgap(5);
        seatGrid.setPadding(new javafx.geometry.Insets(10));

        List<String> occupiedSeats = new ArrayList<>();
        Random random = new Random();
        int totalSeats = selectedFlight.getAvailable_seats() + 20;
        while (occupiedSeats.size() < 20 && occupiedSeats.size() < totalSeats) {
            int row = random.nextInt(10) + 1;
            int col = random.nextInt(10) + 1;
            String seat = row + "-" + col;
            if (!occupiedSeats.contains(seat)) {
                occupiedSeats.add(seat);
            }
        }

        List<Button> seatButtons = new ArrayList<>();
        List<String> tempSelectedSeats = new ArrayList<>(selectedSeats);

        for (int row = 1; row <= 10; row++) {
            for (int col = 1; col <= 10; col++) {
                String seat = row + "-" + col;
                Button seatButton = new Button();
                seatButton.setPrefSize(30, 30);

                if (occupiedSeats.contains(seat)) {
                    seatButton.setStyle("-fx-background-color: white; -fx-border-color: blue; -fx-border-width: 2;");
                    seatButton.setDisable(true);
                } else if (tempSelectedSeats.contains(seat)) {
                    seatButton.setStyle("-fx-background-color: yellow;");
                } else {
                    seatButton.setStyle("-fx-background-color: gray;");
                }

                seatButton.setOnAction(e -> {
                    if (tempSelectedSeats.contains(seat)) {
                        tempSelectedSeats.remove(seat);
                        seatButton.setStyle("-fx-background-color: gray;");
                    } else if (tempSelectedSeats.size() < requiredPassengers) {
                        tempSelectedSeats.add(seat);
                        seatButton.setStyle("-fx-background-color: yellow;");
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Attention", "Limite atteinte",
                                "Vous ne pouvez sélectionner que " + requiredPassengers + " siège(s).");
                    }
                });

                seatGrid.add(seatButton, col - 1, row - 1);
                seatButtons.add(seatButton);
            }
        }

        Button confirmButton = new Button("Select Seats");
        confirmButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black;");
        confirmButton.setOnAction(e -> {
            if (tempSelectedSeats.size() != requiredPassengers) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Nombre de sièges incorrect",
                        "Veuillez sélectionner exactement " + requiredPassengers + " siège(s).");
            } else {
                selectedSeats.clear();
                selectedSeats.addAll(tempSelectedSeats);
                seatSelectionStage.close();
            }
        });

        for (int row = 1; row <= 10; row++) {
            Label rowLabel = new Label(String.valueOf(row));
            seatGrid.add(rowLabel, 10, row - 1);
        }

        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(10));
        layout.getChildren().addAll(new Label("Choisissez vos sièges (" + requiredPassengers + " requis)"), seatGrid, confirmButton);

        Scene scene = new Scene(layout);
        seatSelectionStage.setScene(scene);
        seatSelectionStage.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}