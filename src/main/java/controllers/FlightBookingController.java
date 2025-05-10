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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ServiceFlight;
import services.ServiceReservationVol;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

            // Rendre les champs non éditables
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
            // Image de la compagnie aérienne
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

            // Détails du vol
            airlineLabel.setText(selectedFlight.getAirline());
            flightNumberLabel.setText("Flight " + selectedFlight.getFlight_number());

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            departureLabel.setText(selectedFlight.getDeparture() + "\n" + timeFormat.format(selectedFlight.getDeparture_Time()));
            destinationLabel.setText(selectedFlight.getDestination() + "\n" + timeFormat.format(selectedFlight.getArrival_Time()));
            durationLabel.setText(formatDuration(selectedFlight.getFlight_duration()));
            priceLabel.setText(String.format("$%.2f", selectedFlight.getPrice() * requiredPassengers));
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
            // Vérifier si le client et le vol sont définis
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

            // Créer une réservation
            ServiceReservationVol reservationService = new ServiceReservationVol();
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            ReservationVol reservation = new ReservationVol(
                    currentClient.getId_client(),
                    "Confirmed",
                    currentDate,
                    selectedFlight.getPrice() * requiredPassengers,
                    currentClient.getPrenom() + " " + currentClient.getNom(),
                    selectedFlight.getFlight_id()
            );

            // Ajouter la réservation à la base de données
            reservationService.ajouter(reservation);

            // Mettre à jour le nombre de sièges disponibles
            ServiceFlight flightService = new ServiceFlight();
            selectedFlight.setAvailable_seats(selectedFlight.getAvailable_seats() - requiredPassengers);
            flightService.modifier(selectedFlight);

            // Afficher une alerte de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation confirmée",
                    "Votre réservation a été ajoutée avec succès !");

            // Rediriger vers le tableau de bord
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

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}