package controllers;

import entities.ReservationVehicule;
import entities.Vehicule;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import services.ServiceVehicule;

public class ViewReservationController {
    @FXML
    private Label clientEmailLabel;
    @FXML
    private Label vehicleTypeLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label dateLabel;

    private ReservationVehicule reservation;
    private Stage stage;

    public void setReservationVehicule(ReservationVehicule reservation) {
        this.reservation = reservation;
        if (reservation != null) {
            clientEmailLabel.setText(reservation.getClientEmail() != null ? reservation.getClientEmail() : "N/A");
            vehicleTypeLabel.setText(getVehicleType(reservation.getVehiculeId()));
            locationLabel.setText(String.format("%s à %s", reservation.getLieuPrise(), reservation.getLieuRetour()));
            dateLabel.setText(String.format("%s - %s",
                    reservation.getDateDebut() != null ? reservation.getDateDebut().toString() : "N/A",
                    reservation.getDateFin() != null ? reservation.getDateFin().toString() : "N/A"));
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void closePopup() {
        if (stage != null) {
            stage.close();
        }
    }

    private String getVehicleType(int vehiculeId) {
        try {
            Vehicule vehicule = new ServiceVehicule().getById(vehiculeId);
            return vehicule != null ? vehicule.getType() : "Inconnu";
        } catch (Exception e) {
            return "Erreur";
        }
    }
}