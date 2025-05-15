package controllers;

import entities.Client;
import entities.ReservationVehicule;
import entities.Vehicule;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.ServiceClient;
import services.ServiceReservationVehicule;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;

public class ReservationFormController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField pickupPlaceField;
    @FXML
    private TextField returnPlaceField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label vehicleLabel;
    @FXML
    private Label pricePerDayLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Text validationText;

    private Vehicule vehicle;
    private ServiceClient clientService = new ServiceClient();
    private ServiceReservationVehicule reservationService = new ServiceReservationVehicule();
    private Consumer<Void> onFormClosed;

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{8,}");
    }

    @FXML
    public void initialize() {
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(1));

        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        pickupPlaceField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        returnPlaceField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());

        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updatePriceAndDuration());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updatePriceAndDuration());
    }

    public void setVehicle(Vehicule vehicle) {
        this.vehicle = vehicle;
        vehicleLabel.setText("Véhicule: " + vehicle.getType());
        pricePerDayLabel.setText(String.format("Prix par jour: %.2f TND", vehicle.getPrix()));
        updatePriceAndDuration();
    }

    private void updatePriceAndDuration() {
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
            long days = ChronoUnit.DAYS.between(startDatePicker.getValue(), endDatePicker.getValue());
            if (days < 0) {
                durationLabel.setText("Dates invalides");
                totalPriceLabel.setText("0.00 TND");
                return;
            }
            durationLabel.setText(days + " jour(s)");
            double totalPrice = days * vehicle.getPrix();
            totalPriceLabel.setText(String.format("%.2f TND", totalPrice));
        }
        validateForm();
    }

    private void validateForm() {
        StringBuilder errors = new StringBuilder();
        boolean isValid = true;

        if (nameField.getText().trim().isEmpty()) {
            errors.append("- Le nom est requis\n");
            isValid = false;
        }

        if (emailField.getText().trim().isEmpty()) {
            errors.append("- L'email est requis\n");
            isValid = false;
        } else if (!isValidEmail(emailField.getText().trim())) {
            errors.append("- Format d'email invalide\n");
            isValid = false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            errors.append("- Le téléphone est requis\n");
            isValid = false;
        } else if (!isValidPhone(phoneField.getText().trim())) {
            errors.append("- Le numéro de téléphone doit contenir au moins 8 chiffres\n");
            isValid = false;
        }

        if (pickupPlaceField.getText().trim().isEmpty()) {
            errors.append("- Le lieu de prise est requis\n");
            isValid = false;
        }

        if (returnPlaceField.getText().trim().isEmpty()) {
            errors.append("- Le lieu de retour est requis\n");
            isValid = false;
        }

        if (startDatePicker.getValue() == null) {
            errors.append("- La date de début est requise\n");
            isValid = false;
        }

        if (endDatePicker.getValue() == null) {
            errors.append("- La date de fin est requise\n");
            isValid = false;
        } else if (startDatePicker.getValue() != null &&
                endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            errors.append("- La date de fin doit être après la date de début\n");
            isValid = false;
        }

        validationText.setVisible(!isValid);
        validationText.setText(errors.toString());
        validationText.setStyle("-fx-fill: #e74c3c;");
    }

    @FXML
    private void handleSubmit() {
        if (validationText.isVisible()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Formulaire invalide",
                    "Veuillez corriger les erreurs avant de soumettre.");
            return;
        }

        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String pickupPlace = pickupPlaceField.getText().trim();
            String returnPlace = returnPlaceField.getText().trim();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            long days = ChronoUnit.DAYS.between(startDate, endDate);
            double totalPrice = days * vehicle.getPrix();

            Client client = new Client(
                    0,                                   // id_client
                    name.split(" ")[0],                  // nom
                    name.contains(" ") ? name.substring(name.indexOf(" ") + 1) : "", // prenom
                    email,                               // email
                    Integer.parseInt(phone),             // numero_telephone
                    "01/01/2000",                       // date_de_naissance (provide a default valid date)
                    "defaultPassword",                   // mot_de_passe (can't be null)
                    null,                                // profilePicture (will default to "/images/default_profile.png")
                    "USER",                              // role (required, default to "USER")
                    null                                 // gender
            );
            clientService.ajouter(client);

            Integer clientId = clientService.getClientIdByEmail(email);
            if (clientId == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Création du client",
                        "Impossible de créer le client.");
                return;
            }

            ReservationVehicule reservation = new ReservationVehicule(
                    0, vehicle.getId(), clientId, pickupPlace, returnPlace, startDate, endDate);
            reservation.setClientEmail(email);
            reservationService.ajouter(reservation);

            showConfirmation(reservation, totalPrice);

            if (onFormClosed != null) {
                onFormClosed.accept(null);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de base de données",
                    "Une erreur est survenue: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format invalide",
                    "Le numéro de téléphone doit contenir uniquement des chiffres.");
        }
    }

    public void setOnFormClosed(Consumer<Void> onFormClosed) {
        this.onFormClosed = onFormClosed;
    }

    private void showConfirmation(ReservationVehicule reservation, double totalPrice) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Réservation Confirmée");
        confirm.setHeaderText("Votre réservation a été enregistrée avec succès!");
        confirm.setContentText(String.format(
                "Détails de la réservation:\n" +
                        "Véhicule: %s\n" +
                        "Date de début: %s\n" +
                        "Date de fin: %s\n" +
                        "Prix total: %.2f TND\n\n" +
                        "Voulez-vous voir toutes vos réservations?",
                vehicle.toString(),
                reservation.getDateDebut(),
                reservation.getDateFin(),
                totalPrice
        ));

        ButtonType viewReservationsButton = new ButtonType("Voir les réservations");
        ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(viewReservationsButton, closeButton);

        confirm.showAndWait().ifPresent(response -> {
            if (response == viewReservationsButton) {
                openManageReservations();
            }
            if (onFormClosed != null) {
                onFormClosed.accept(null);
            }
        });
    }

    @FXML
    private void handleCancel() {
        if (onFormClosed != null) {
            onFormClosed.accept(null);
        }
    }

    private void openManageReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ManageReservations.fxml"));
            BorderPane managePane = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(managePane, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/styles/med.css").toExternalForm());
            stage.setTitle("Gérer les Réservations");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger la gestion des réservations: " + e.getMessage());
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