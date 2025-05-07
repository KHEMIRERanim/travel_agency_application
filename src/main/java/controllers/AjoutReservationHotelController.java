package controllers;

import entities.ReservationHotel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import services.ServiceReservationHotel;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AjoutReservationHotelController implements Initializable {

    @FXML private DatePicker checkinPicker;
    @FXML private DatePicker checkoutPicker;
    @FXML private Label errorLabel;
    @FXML private Button submitBtn;
    private final ServiceReservationHotel serviceReservationHotel = new ServiceReservationHotel();
    private int hotelId;
    private int clientId;

    public void setContext(int hotelId, int clientId) {
        this.hotelId = hotelId;
        this.clientId = clientId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorLabel.setText("");
    }

    @FXML
    private void onSubmit() throws SQLException {
        LocalDate checkin = checkinPicker.getValue();
        LocalDate checkout = checkoutPicker.getValue();
        LocalDate today = LocalDate.now();
        if (checkin == null || checkout == null) {
            errorLabel.setText("Veuillez sélectionner les deux dates.");
            return;
        }
        if (checkin.isBefore(today)) {
            errorLabel.setText("La date d'arrivée doit être aujourd'hui ou plus tard.");
            return;
        }
        if (!checkout.isAfter(checkin)) {
            errorLabel.setText("La date de départ doit être après la date d'arrivée.");
            return;
        }
        ReservationHotel reservation = new ReservationHotel(
                hotelId,
                clientId,
                checkin.toString(),
                checkout.toString()
        );
        serviceReservationHotel.ajouter(reservation);
        Stage stage = (Stage) submitBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) submitBtn.getScene().getWindow();
        stage.close();
    }
}
