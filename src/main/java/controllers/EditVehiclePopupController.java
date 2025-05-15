package controllers;

import entities.Vehicule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceVehicule;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.function.Consumer;

public class EditVehiclePopupController {
    @FXML
    private TextField typeField;

    @FXML
    private TextField lieuPriseField;

    @FXML
    private TextField lieuRetourField;

    @FXML
    private DatePicker dateLocationPicker;

    @FXML
    private DatePicker dateRetourPicker;

    @FXML
    private TextField imagePathField;

    @FXML
    private TextField prixField;

    private ServiceVehicule serviceVehicule = new ServiceVehicule();
    private Vehicule vehicle;
    private Consumer<Void> refreshCallback; // Add callback field

    @FXML
    public void initialize() {
        // Will be populated by setVehicle
    }

    public void setVehicle(Vehicule vehicle) {
        this.vehicle = vehicle;
        typeField.setText(vehicle.getType());
        lieuPriseField.setText(vehicle.getLieuPrise());
        lieuRetourField.setText(vehicle.getLieuRetour());
        dateLocationPicker.setValue(vehicle.getDateLocation());
        dateRetourPicker.setValue(vehicle.getDateRetour());
        imagePathField.setText(vehicle.getImagePath());
        prixField.setText(String.valueOf(vehicle.getPrix()));
    }

    // Add setter for the callback
    public void setRefreshCallback(Consumer<Void> callback) {
        this.refreshCallback = callback;
    }

    @FXML
    void saveVehicle(ActionEvent event) {
        try {
            // Validate inputs
            String type = typeField.getText().trim();
            String lieuPrise = lieuPriseField.getText().trim();
            String lieuRetour = lieuRetourField.getText().trim();
            LocalDate dateLocation = dateLocationPicker.getValue();
            LocalDate dateRetour = dateRetourPicker.getValue();
            String imagePath = imagePathField.getText().trim();
            String prixText = prixField.getText().trim();

            if (type.isEmpty() || lieuPrise.isEmpty() || lieuRetour.isEmpty() || prixText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Champs requis", "Tous les champs obligatoires doivent être remplis.");
                return;
            }

            double prix;
            try {
                prix = Double.parseDouble(prixText);
                if (prix <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Prix invalide", "Le prix doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Prix invalide", "Le prix doit être un nombre valide.");
                return;
            }

            if (dateLocation != null && dateRetour != null && dateRetour.isBefore(dateLocation)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Dates invalides", "La date de retour doit être après la date de location.");
                return;
            }

            // Update vehicle
            vehicle.setType(type);
            vehicle.setLieuPrise(lieuPrise);
            vehicle.setLieuRetour(lieuRetour);
            vehicle.setDateLocation(dateLocation);
            vehicle.setDateRetour(dateRetour);
            vehicle.setImagePath(imagePath);
            vehicle.setPrix(prix);

            // Save to database
            serviceVehicule.modifier(vehicle);

            // Close the popup
            closePopup(event);

            // Refresh the parent list using the callback
            if (refreshCallback != null) {
                refreshCallback.accept(null);
            }

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Véhicule modifié", "Le véhicule a été modifié avec succès.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de base de données", "Une erreur est survenue lors de la modification: " + e.getMessage());
        }
    }

    @FXML
    void closePopup(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}