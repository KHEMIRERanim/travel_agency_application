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

public class AddVehiclePopupController {
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
    private Consumer<Void> refreshCallback;

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

            // Create new vehicle
            Vehicule vehicle = new Vehicule();
            vehicle.setType(type);
            vehicle.setLieuPrise(lieuPrise);
            vehicle.setLieuRetour(lieuRetour);
            vehicle.setDateLocation(dateLocation);
            vehicle.setDateRetour(dateRetour);
            vehicle.setImagePath(imagePath);
            vehicle.setPrix(prix);

            // Save to database
            serviceVehicule.ajouter(vehicle);

            // Refresh the parent list
            if (refreshCallback != null) {
                refreshCallback.accept(null);
            }

            // Close the popup
            closePopup(event);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Véhicule ajouté", "Le véhicule a été ajouté avec succès.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de base de données", "Une erreur est survenue lors de l'ajout: " + e.getMessage());
        }
    }

    @FXML
    void closePopup(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setRefreshCallback(Consumer<Void> callback) {
        this.refreshCallback = callback;
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}