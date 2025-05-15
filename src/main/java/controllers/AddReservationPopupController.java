package controllers;

import entities.Client;
import entities.ReservationVehicule;
import entities.Vehicule;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceClient;
import services.ServiceReservationVehicule;
import services.ServiceVehicule;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.function.Consumer;

public class AddReservationPopupController {
    @FXML
    private ComboBox<Client> clientComboBox;

    @FXML
    private ComboBox<Vehicule> vehicleComboBox;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    private ServiceClient serviceClient = new ServiceClient();
    private ServiceVehicule serviceVehicule = new ServiceVehicule();
    private ServiceReservationVehicule serviceReservation = new ServiceReservationVehicule();
    private Consumer<Void> refreshCallback;

    @FXML
    public void initialize() {
        try {
            clientComboBox.setItems(FXCollections.observableArrayList(serviceClient.recuperer()));
            vehicleComboBox.setItems(FXCollections.observableArrayList(serviceVehicule.getAll()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", "Impossible de charger les données: " + e.getMessage());
        }
    }

    @FXML
    void saveReservation(ActionEvent event) {
        Client client = clientComboBox.getValue();
        Vehicule vehicle = vehicleComboBox.getValue();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (client == null || vehicle == null || dateDebut == null || dateFin == null || dateFin.isBefore(dateDebut)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Validation", "Veuillez sélectionner un client, un véhicule et des dates valides.");
            return;
        }

        try {
            ReservationVehicule reservation = new ReservationVehicule();
            reservation.setClientId(client.getId_client()); // Use client ID
            reservation.setVehiculeId(vehicle.getId()); // Use vehicle ID
            reservation.setDateDebut(dateDebut);
            reservation.setDateFin(dateFin);
            serviceReservation.ajouter(reservation);

            if (refreshCallback != null) {
                refreshCallback.accept(null);
            }

            closePopup(event);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation ajoutée", "La réservation a été ajoutée avec succès.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de base de données", "Une erreur est survenue: " + e.getMessage());
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