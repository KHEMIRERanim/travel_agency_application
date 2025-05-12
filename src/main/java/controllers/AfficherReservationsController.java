package controllers;

import entities.ReservationVol;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import services.ServiceReservationVol;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AfficherReservationsController {

    @FXML
    private AnchorPane reservationsContainer;

    @FXML
    private TextField searchField;

    private ServiceReservationVol reservationService;
    private List<ReservationVol> reservations;

    @FXML
    public void initialize() {
        try {
            System.out.println("Initialisation de AfficherReservationsController...");
            reservationService = new ServiceReservationVol();
            reservations = new ArrayList<>();
            System.out.println("Chargement des réservations...");
            loadReservations();
            System.out.println("Initialisation terminée.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'initialisation", "Erreur lors de l'initialisation de la page", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadReservations() {
        try {
            reservations = reservationService.recuperer();
            reservationsContainer.getChildren().clear();
            if (reservations.isEmpty()) {
                Label noDataLabel = new Label("Aucune réservation trouvée.");
                noDataLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #64748B;");
                noDataLabel.setLayoutX(10);
                noDataLabel.setLayoutY(10);
                reservationsContainer.getChildren().add(noDataLabel);
            } else {
                double yPosition = 10.0;
                for (ReservationVol reservation : reservations) {
                    VBox reservationBox = createReservationBox(reservation);
                    reservationBox.setLayoutX(10);
                    reservationBox.setLayoutY(yPosition);
                    reservationsContainer.getChildren().add(reservationBox);
                    yPosition += reservationBox.getPrefHeight() + 15.0; // Espacement entre les réservations
                }
                reservationsContainer.setPrefHeight(yPosition + 10.0);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération des réservations", e.getMessage());
            reservationsContainer.getChildren().clear();
            Label errorLabel = new Label("Erreur lors du chargement des réservations.");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #64748B;");
            errorLabel.setLayoutX(10);
            errorLabel.setLayoutY(10);
            reservationsContainer.getChildren().add(errorLabel);
            e.printStackTrace();
        }
    }

    private VBox createReservationBox(ReservationVol reservation) {
        VBox vbox = new VBox();
        vbox.setSpacing(8.0); // Espacement entre les lignes (Passager, Date, Statut, Prix)
        vbox.setPadding(new Insets(15, 20, 15, 20)); // Padding équilibré
        vbox.setPrefHeight(150.0); // Hauteur augmentée pour accueillir la date
        vbox.setPrefWidth(750.0);
        vbox.setAlignment(Pos.TOP_LEFT);
        vbox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E7FF; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);");

        // Effet de survol
        vbox.setOnMouseEntered(event -> vbox.setStyle("-fx-background-color: #F5F7FA; -fx-border-color: #E0E7FF; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"));
        vbox.setOnMouseExited(event -> vbox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E7FF; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"));

        // Passager
        HBox passengerBox = new HBox(10.0); // Espacement entre libellé et valeur
        passengerBox.setAlignment(Pos.CENTER_LEFT);
        Label passengerLabel = new Label("Passager: ");
        passengerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280; -fx-font-weight: bold;");
        Label passengerValue = new Label(reservation.getPassenger_name());
        passengerValue.setStyle("-fx-font-size: 16px; -fx-text-fill: #1F2937;");
        passengerBox.getChildren().addAll(passengerLabel, passengerValue);

        // Date de réservation
        HBox dateBox = new HBox(10.0);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        Label dateLabel = new Label("Date: ");
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280; -fx-font-weight: bold;");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = reservation.getReservationvol_date() != null ? dateFormat.format(reservation.getReservationvol_date()) : "N/A";
        Label dateValue = new Label(formattedDate);
        dateValue.setStyle("-fx-font-size: 16px; -fx-text-fill: #1F2937;");
        dateBox.getChildren().addAll(dateLabel, dateValue);

        // Statut
        HBox statusBox = new HBox(10.0);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Label statusLabel = new Label("Statut: ");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280; -fx-font-weight: bold;");
        Label statusValue = new Label(reservation.getStatus());
        String statusColor = reservation.getStatus().equalsIgnoreCase("Confirmed") ? "#10B981" :
                reservation.getStatus().equalsIgnoreCase("Deleted") ? "#EF4444" :
                        reservation.getStatus().equalsIgnoreCase("Modified") ? "#F59E0B" : "#1F2937";
        statusValue.setStyle("-fx-font-size: 16px; -fx-text-fill: " + statusColor + "; -fx-font-weight: bold;");
        statusBox.getChildren().addAll(statusLabel, statusValue);

        // Prix
        HBox priceBox = new HBox(10.0);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        Label priceLabel = new Label("Prix: ");
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280; -fx-font-weight: bold;");
        Label priceValue = new Label(String.format("%.2f", reservation.getPrice()));
        priceValue.setStyle("-fx-font-size: 16px; -fx-text-fill: #1F2937;");
        priceBox.getChildren().addAll(priceLabel, priceValue);

        vbox.getChildren().addAll(passengerBox, dateBox, statusBox, priceBox);
        return vbox;
    }

    @FXML
    private void refreshList() {
        loadReservations();
        searchField.clear();
    }

    @FXML
    private void filterReservations() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadReservations();
            return;
        }

        reservationsContainer.getChildren().clear();
        double yPosition = 10.0;
        for (ReservationVol reservation : reservations) {
            String reservationString = String.format(
                    "Passager: %s | Date: %s | Statut: %s | Prix: %.2f",
                    reservation.getPassenger_name(),
                    reservation.getReservationvol_date() != null ? new SimpleDateFormat("dd/MM/yyyy").format(reservation.getReservationvol_date()) : "N/A",
                    reservation.getStatus(),
                    reservation.getPrice()
            );
            if (reservationString.toLowerCase().contains(searchText)) {
                VBox reservationBox = createReservationBox(reservation);
                reservationBox.setLayoutX(10);
                reservationBox.setLayoutY(yPosition);
                reservationsContainer.getChildren().add(reservationBox);
                yPosition += reservationBox.getPrefHeight() + 15.0; // Espacement entre les réservations
            }
        }

        if (reservationsContainer.getChildren().isEmpty()) {
            Label noMatchLabel = new Label("Aucune réservation correspondante trouvée.");
            noMatchLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #64748B;");
            noMatchLabel.setLayoutX(10);
            noMatchLabel.setLayoutY(10);
            reservationsContainer.getChildren().add(noMatchLabel);
        } else {
            reservationsContainer.setPrefHeight(yPosition + 10.0);
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