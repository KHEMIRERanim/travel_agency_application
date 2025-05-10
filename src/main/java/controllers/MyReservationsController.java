package controllers;

import entities.Client;
import entities.Flight;
import entities.ReservationVol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ServiceFlight;
import services.ServiceReservationVol;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class MyReservationsController {

    @FXML
    private GridPane reservationsContainer;

    @FXML
    private Button backButton;

    private Client currentClient;
    private ServiceReservationVol reservationService = new ServiceReservationVol();
    private ServiceFlight flightService = new ServiceFlight();
    private UserDashboardController dashboardController; // Added for dashboard integration
    private boolean isEmbeddedInDashboard = false; // Added for dashboard integration

    public void setClient(Client client) {
        this.currentClient = client;
        loadReservations();
    }

    public void setDashboardController(UserDashboardController controller) {
        this.dashboardController = controller;
        this.isEmbeddedInDashboard = (controller != null);
    }

    private void loadReservations() {
        try {
            if (currentClient == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun client connecté", "Veuillez vous connecter pour voir vos réservations.");
                return;
            }

            List<ReservationVol> reservations = reservationService.recupererReservationsByClientId(currentClient.getId_client());
            reservationsContainer.getChildren().clear();

            if (reservations == null || reservations.isEmpty()) {
                Label noReservationsLabel = new Label("Aucune réservation trouvée.");
                noReservationsLabel.setFont(Font.font("System", 16));
                reservationsContainer.add(noReservationsLabel, 0, 0, 5, 1); // Span across all 5 columns
            } else {
                for (int i = 0; i < reservations.size(); i++) {
                    ReservationVol reservation = reservations.get(i);
                    VBox card = createReservationCard(reservation);
                    int row = i / 5; // Division entière pour déterminer la ligne (5 cartes par ligne)
                    int col = i % 5; // Modulo pour déterminer la colonne (0 à 4)
                    reservationsContainer.add(card, col, row);
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération des réservations", e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createReservationCard(ReservationVol reservation) {
        VBox card = new VBox();
        card.setPrefWidth(180); // Réduit pour s'adapter à 5 colonnes
        card.setPrefHeight(420);
        card.setStyle("-fx-background-color: white; -fx-border-color: #039BE5; -fx-border-radius: 0; " +
                "-fx-padding: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 5);");
        card.setSpacing(10);

        // Animation à l'entrée de la souris
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), card);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);

        // Animation à la sortie de la souris
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), card);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        card.setOnMouseEntered(e -> scaleIn.play());
        card.setOnMouseExited(e -> scaleOut.play());

        // Image section
        ImageView reservationImage = new ImageView();
        reservationImage.setFitWidth(160); // Réduit pour correspondre à la largeur de la carte
        reservationImage.setFitHeight(120); // Ajusté pour l'esthétique
        reservationImage.setPreserveRatio(true);
        reservationImage.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 4;");

        try {
            reservationImage.setImage(new Image(getClass().getResource("/images/img.png").toExternalForm()));
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image /images/img.png : " + e.getMessage());
            reservationImage.setImage(new Image("https://via.placeholder.com/160x120?text=Image+Non+Trouvée"));
        }

        // Informations sur la réservation
        Label passengerName = new Label("Passager: " + reservation.getPassenger_name());
        passengerName.setFont(Font.font("System", FontWeight.BOLD, 14)); // Police réduite
        passengerName.setWrapText(true); // Permettre le retour à la ligne

        Label date = new Label("Date: " + (reservation.getReservationvol_date() != null ? new SimpleDateFormat("dd/MM/yyyy").format(reservation.getReservationvol_date()) : "N/A"));
        date.setFont(Font.font("System", 12)); // Police réduite
        date.setStyle("-fx-text-fill: #555;");
        date.setWrapText(true);

        Label price = new Label("Prix: $" + String.format("%.2f", reservation.getPrice()));
        price.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-font-size: 12px;");
        price.setWrapText(true);

        Label status = new Label("Statut: " + reservation.getStatus());
        status.setFont(Font.font("System", 12));
        status.setWrapText(true);

        // Boutons d'action - Conteneur
        VBox buttonsContainer = new VBox(8); // Espacement réduit

        // Bouton de modification
        Button modifyButton = new Button("Modifier");
        modifyButton.setStyle("-fx-background-color: #039BE5; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 6 10; -fx-background-radius: 5;"); // Padding réduit
        modifyButton.setPrefWidth(160); // Réduit

        ScaleTransition modifyScaleIn = new ScaleTransition(Duration.millis(150), modifyButton);
        modifyScaleIn.setToX(1.1);
        modifyScaleIn.setToY(1.1);

        ScaleTransition modifyScaleOut = new ScaleTransition(Duration.millis(150), modifyButton);
        modifyScaleOut.setToX(1.0);
        modifyScaleOut.setToY(1.0);

        modifyButton.setOnMouseEntered(e -> modifyScaleIn.play());
        modifyButton.setOnMouseExited(e -> modifyScaleOut.play());
        modifyButton.setOnAction(e -> modifyReservation(reservation));

        // Bouton de suppression
        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 6 10; -fx-background-radius: 5;");
        deleteButton.setPrefWidth(160);

        ScaleTransition deleteScaleIn = new ScaleTransition(Duration.millis(150), deleteButton);
        deleteScaleIn.setToX(1.1);
        deleteScaleIn.setToY(1.1);

        ScaleTransition deleteScaleOut = new ScaleTransition(Duration.millis(150), deleteButton);
        deleteScaleOut.setToX(1.0);
        deleteScaleOut.setToY(1.0);

        deleteButton.setOnMouseEntered(e -> deleteScaleIn.play());
        deleteButton.setOnMouseExited(e -> deleteScaleOut.play());
        deleteButton.setOnAction(e -> deleteReservation(reservation));

        // Bouton pour afficher le billet
        Button ticketButton = new Button("Afficher le billet");
        ticketButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 6 10; -fx-background-radius: 5;");
        ticketButton.setPrefWidth(160);

        ScaleTransition ticketScaleIn = new ScaleTransition(Duration.millis(150), ticketButton);
        ticketScaleIn.setToX(1.1);
        ticketScaleIn.setToY(1.1);

        ScaleTransition ticketScaleOut = new ScaleTransition(Duration.millis(150), ticketButton);
        ticketScaleOut.setToX(1.0);
        ticketScaleOut.setToY(1.0);

        ticketButton.setOnMouseEntered(e -> ticketScaleIn.play());
        ticketButton.setOnMouseExited(e -> ticketScaleOut.play());
        ticketButton.setOnAction(e -> showTicket(reservation));

        // Ajouter les boutons au conteneur
        buttonsContainer.getChildren().addAll(modifyButton, deleteButton, ticketButton);

        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(
                reservationImage,
                passengerName,
                date,
                price,
                status,
                buttonsContainer
        );

        return card;
    }

    private void showTicket(ReservationVol reservation) {
        try {
            Flight flight = flightService.getFlightById(reservation.getFlight_id());
            if (flight == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Vol non trouvé", "Impossible de récupérer les détails du vol.");
                return;
            }

            Stage ticketStage = new Stage();
            ticketStage.setTitle("Billet de vol");

            VBox ticketContainer = new VBox(20);
            ticketContainer.setPadding(new Insets(20));
            ticketContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #E3F2FD, #BBDEFB); " +
                    "-fx-border-color: #0288D1; -fx-border-width: 2; -fx-border-radius: 10; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
            ticketContainer.setPrefWidth(400);
            ticketContainer.setPrefHeight(500);

            Label title = new Label("Billet de Vol");
            title.setFont(Font.font("System", FontWeight.BOLD, 24));
            title.setStyle("-fx-text-fill: #01579B;");

            Label passengerLabel = new Label("Passager: " + reservation.getPassenger_name());
            passengerLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            passengerLabel.setStyle("-fx-text-fill: #1A237E;");

            Label flightNumber = new Label("Numéro de vol: " + flight.getFlight_number());
            flightNumber.setFont(Font.font("System", 14));
            flightNumber.setStyle("-fx-text-fill: #263238;");

            Label departure = new Label("Départ: " + flight.getDeparture());
            departure.setFont(Font.font("System", 14));
            departure.setStyle("-fx-text-fill: #263238;");

            Label destination = new Label("Destination: " + flight.getDestination());
            destination.setFont(Font.font("System", 14));
            destination.setStyle("-fx-text-fill: #263238;");

            Label dateLabel = new Label("Date: " + new SimpleDateFormat("dd/MM/yyyy").format(flight.getFlight_date()));
            dateLabel.setFont(Font.font("System", 14));
            dateLabel.setStyle("-fx-text-fill: #263238;");

            Label departureTime = new Label("Heure de départ: " + new SimpleDateFormat("HH:mm").format(flight.getDeparture_Time()));
            departureTime.setFont(Font.font("System", 14));
            departureTime.setStyle("-fx-text-fill: #263238;");

            Label arrivalTime = new Label("Heure d'arrivée: " + new SimpleDateFormat("HH:mm").format(flight.getArrival_Time()));
            arrivalTime.setFont(Font.font("System", 14));
            arrivalTime.setStyle("-fx-text-fill: #263238;");

            Label airline = new Label("Compagnie: " + flight.getAirline());
            airline.setFont(Font.font("System", 14));
            airline.setStyle("-fx-text-fill: #263238;");

            Label priceLabel = new Label("Prix: $" + String.format("%.2f", reservation.getPrice()));
            priceLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            priceLabel.setStyle("-fx-text-fill: #2E7D32;");

            Button closeButton = new Button("Fermer");
            closeButton.setStyle("-fx-background-color: #0288D1; -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
            closeButton.setOnAction(e -> ticketStage.close());

            ticketContainer.getChildren().addAll(
                    title,
                    passengerLabel,
                    flightNumber,
                    departure,
                    destination,
                    dateLabel,
                    departureTime,
                    arrivalTime,
                    airline,
                    priceLabel,
                    closeButton
            );

            Scene ticketScene = new Scene(ticketContainer);
            ticketStage.setScene(ticketScene);
            ticketStage.show();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération des détails du vol", e.getMessage());
            e.printStackTrace();
        }
    }

    private void modifyReservation(ReservationVol reservation) {
        showAlert(Alert.AlertType.INFORMATION, "Modification", "Fonction de modification non implémentée encore.", "Veuillez ajouter la logique de modification.");
    }

    private void deleteReservation(ReservationVol reservation) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réservation ?");
        confirmationAlert.setContentText("La réservation sera marquée comme supprimée.");

        ButtonType buttonTypeYes = new ButtonType("Oui");
        ButtonType buttonTypeNo = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeYes) {
                try {
                    reservationService.updateReservationStatus(reservation, "Deleted");
                    reservation.setStatus("Deleted");

                    for (javafx.scene.Node node : reservationsContainer.getChildren()) {
                        if (node instanceof VBox) {
                            VBox card = (VBox) node;
                            for (javafx.scene.Node child : card.getChildren()) {
                                if (child instanceof Label && ((Label) child).getText().startsWith("Statut: ")) {
                                    ((Label) child).setText("Statut: Deleted");
                                    break;
                                }
                            }
                            for (javafx.scene.Node child : card.getChildren()) {
                                if (child instanceof VBox) {
                                    for (javafx.scene.Node button : ((VBox) child).getChildren()) {
                                        if (button instanceof Button) {
                                            ((Button) button).setDisable(true);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation marquée comme supprimée.", "");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour du statut", e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Annulé", "Suppression annulée.", "");
            }
        });
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            if (isEmbeddedInDashboard && dashboardController != null) {
                // Load ChercherVol.fxml into the dashboard's content area
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
                Parent root = loader.load();
                ChercherVolController controller = loader.getController();
                controller.setClient(currentClient);
                controller.setDashboardController(dashboardController);
                dashboardController.loadContentToArea(root);
            } else {
                // Load ChercherVol.fxml in a new stage
                Stage stage = (Stage) backButton.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
                Parent root = loader.load();
                ChercherVolController controller = loader.getController();
                controller.setClient(currentClient);
                stage.setScene(new Scene(root));
                stage.setTitle("Recherche de vol");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement FXML", "Impossible de charger la page de recherche : " + e.getMessage());
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