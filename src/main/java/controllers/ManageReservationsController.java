package controllers;

import entities.ReservationVehicule;
import entities.Vehicule;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import services.ServiceReservationVehicule;
import services.ServiceVehicule;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ManageReservationsController {
    @FXML
    private VBox reservationsVBox;

    @FXML
    private Label totalReservationsLabel;

    private ServiceReservationVehicule reservationService = new ServiceReservationVehicule();
    private ServiceVehicule vehicleService = new ServiceVehicule();

    @FXML
    public void initialize() {
        loadReservations();
    }

    private void loadReservations() {
        try {
            reservationsVBox.getChildren().clear();
            List<ReservationVehicule> reservations = reservationService.getAll();
            totalReservationsLabel.setText(String.format("%d réservation(s) trouvée(s)", reservations.size()));

            for (ReservationVehicule reservation : reservations) {
                reservationsVBox.getChildren().add(createReservationCard(reservation));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de base de données", e.getMessage());
        }
    }

    private HBox createReservationCard(ReservationVehicule reservation) {
        HBox card = new HBox(15);
        card.getStyleClass().add("reservation-card");
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);

        try {
            Vehicule vehicle = vehicleService.getById(reservation.getVehiculeId());

            ImageView vehicleImage = new ImageView();
            String imagePath = vehicle != null && vehicle.getImagePath() != null ? vehicle.getImagePath() : "placeholder.jpg";
            String resourcePath = "/images/" + imagePath;
            try {
                Image image = new Image(getClass().getResource(resourcePath).toExternalForm());
                vehicleImage.setImage(image);
            } catch (Exception e) {
                System.err.println("Erreur de chargement de l'image: " + resourcePath);
                Image placeholder = new Image(getClass().getResource("/images/placeholder.jpg").toExternalForm());
                vehicleImage.setImage(placeholder);
            }
            vehicleImage.setFitWidth(120);
            vehicleImage.setFitHeight(120);
            vehicleImage.setPreserveRatio(true);
            vehicleImage.getStyleClass().add("reservation-image");

            VBox detailsBox = new VBox(8);
            detailsBox.setAlignment(Pos.CENTER_LEFT);
            detailsBox.getStyleClass().add("reservation-details");

            Label vehicleLabel = new Label(vehicle != null ? vehicle.getType() : "Véhicule inconnu");
            vehicleLabel.getStyleClass().add("vehicle-type-label");

            long days = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin());
            double totalPrice = vehicle != null ? days * vehicle.getPrix() : 0.0;
            Label priceLabel = new Label(String.format("Prix total: %.2f TND", totalPrice));
            priceLabel.getStyleClass().add("price-label");

            Label datesLabel = new Label(String.format("Du %s au %s",
                    reservation.getDateDebut() != null ? reservation.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non spécifié",
                    reservation.getDateFin() != null ? reservation.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non spécifié"));
            datesLabel.getStyleClass().add("dates-label");

            Label locationsLabel = new Label(String.format("De %s à %s",
                    reservation.getLieuPrise(), reservation.getLieuRetour()));
            locationsLabel.getStyleClass().add("locations-label");

            Label emailLabel = new Label("Client: " + (reservation.getClientEmail() != null ? reservation.getClientEmail() : "Non spécifié"));
            emailLabel.getStyleClass().add("email-label");

            detailsBox.getChildren().addAll(
                    vehicleLabel,
                    priceLabel,
                    datesLabel,
                    locationsLabel,
                    emailLabel
            );

            VBox actionsBox = new VBox(10);
            actionsBox.setAlignment(Pos.CENTER_RIGHT);
            actionsBox.getStyleClass().add("actions-box");

            Button modifyButton = new Button("Modifier");
            modifyButton.getStyleClass().add("modify-button");
            modifyButton.setOnAction(event -> handleModify(reservation));

            Button deleteButton = new Button("Supprimer");
            deleteButton.getStyleClass().add("delete-button");
            deleteButton.setOnAction(event -> handleDelete(reservation));

            actionsBox.getChildren().addAll(modifyButton, deleteButton);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            card.getChildren().addAll(vehicleImage, detailsBox, spacer, actionsBox);

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du véhicule: " + e.getMessage());
            Label errorLabel = new Label("Erreur lors du chargement de la réservation");
            card.getChildren().add(errorLabel);
        }

        return card;
    }

    @FXML
    private void handleRefresh() {
        loadReservations();
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) reservationsVBox.getScene().getWindow();
        stage.close();
    }

    private void handleModify(ReservationVehicule reservation) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier la Réservation");
        dialog.setHeaderText("Modifier les détails de la réservation");
        dialog.getDialogPane().getStyleClass().add("custom-dialog");

        VBox content = new VBox(10);
        content.getStyleClass().add("dialog-content");

        TextField pickupField = new TextField(reservation.getLieuPrise());
        pickupField.setPromptText("Lieu de prise");
        pickupField.getStyleClass().add("dialog-field");

        TextField returnField = new TextField(reservation.getLieuRetour());
        returnField.setPromptText("Lieu de retour");
        returnField.getStyleClass().add("dialog-field");

        DatePicker startDatePicker = new DatePicker(reservation.getDateDebut());
        startDatePicker.setPromptText("Date de début");
        startDatePicker.getStyleClass().add("dialog-field");

        DatePicker endDatePicker = new DatePicker(reservation.getDateFin());
        endDatePicker.setPromptText("Date de fin");
        endDatePicker.getStyleClass().add("dialog-field");

        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateDates(startDatePicker, endDatePicker));
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateDates(startDatePicker, endDatePicker));

        content.getChildren().addAll(
                new Label("Lieu de prise:"), pickupField,
                new Label("Lieu de retour:"), returnField,
                new Label("Date de début:"), startDatePicker,
                new Label("Date de fin:"), endDatePicker
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(false);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Dates invalides", "Les dates de début et de fin doivent être spécifiées.");
                        return;
                    }

                    reservation.setLieuPrise(pickupField.getText().trim());
                    reservation.setLieuRetour(returnField.getText().trim());
                    reservation.setDateDebut(startDatePicker.getValue());
                    reservation.setDateFin(endDatePicker.getValue());

                    reservationService.modifier(reservation);
                    loadReservations();

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Modification réussie",
                            "La réservation a été modifiée avec succès.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Modification impossible",
                            "Une erreur est survenue lors de la modification: " + e.getMessage());
                }
            }
        });
    }

    private void handleDelete(ReservationVehicule reservation) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réservation ?");
        confirmation.setContentText("Cette action est irréversible.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    reservationService.supprimer(reservation);
                    loadReservations();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Suppression réussie",
                            "La réservation a été supprimée avec succès.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Suppression impossible",
                            "Une erreur est survenue lors de la suppression: " + e.getMessage());
                }
            }
        });
    }

    private void validateDates(DatePicker startPicker, DatePicker endPicker) {
        if (startPicker.getValue() != null && endPicker.getValue() != null) {
            boolean isValid = !endPicker.getValue().isBefore(startPicker.getValue());
            startPicker.setStyle(isValid ? "" : "-fx-border-color: red;");
            endPicker.setStyle(isValid ? "" : "-fx-border-color: red;");

            Button okButton = (Button) startPicker.getScene().getWindow().getScene()
                    .lookup(".dialog-pane .button-bar .button:first-child");
            if (okButton != null) {
                okButton.setDisable(!isValid);
            }
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