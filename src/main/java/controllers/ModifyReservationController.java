package controllers;

import entities.ReservationVol;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceReservationVol;

import java.sql.SQLException;

public class ModifyReservationController {

    @FXML
    private TextField passengerNameField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private ReservationVol reservation;
    private MyReservationsController parentController;
    private ServiceReservationVol reservationService = new ServiceReservationVol();

    public void setReservation(ReservationVol reservation) {
        this.reservation = reservation;
        passengerNameField.setText(reservation.getPassenger_name());
    }

    public void setParentController(MyReservationsController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void saveChanges() {
        String newPassengerName = passengerNameField.getText().trim();
        if (newPassengerName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Champ vide", "Le nom du passager ne peut pas être vide.");
            return;
        }

        try {
            // Update the reservation object
            reservation.setPassenger_name(newPassengerName);
            reservation.setStatus("Modified");

            // Update the database
            reservationService.modifier(reservation);

            // Notify the parent controller to refresh the reservations
            if (parentController != null) {
                parentController.loadReservations();
            }

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation modifiée", "La réservation a été mise à jour avec succès.");
            closeWindow();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow(); // Use saveButton as a fallback
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