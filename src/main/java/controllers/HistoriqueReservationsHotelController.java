package controllers;

import entities.Hotels;
import entities.ReservationHotel;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import services.ServiceHotel;
import services.ServiceReservationHotel;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HistoriqueReservationsHotelController implements Initializable {

    @FXML private TableView<ReservationHotel> table;
    @FXML private TableColumn<ReservationHotel, String> hotelNameCol;
    @FXML private TableColumn<ReservationHotel, String> hotelPriceCol;
    @FXML private TableColumn<ReservationHotel, String> checkinCol;
    @FXML private TableColumn<ReservationHotel, String> checkoutCol;
    @FXML private TableColumn<ReservationHotel, String> colPrixTotal;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int clientId;
    private final ServiceReservationHotel reservationService = new ServiceReservationHotel();
    private final ServiceHotel hotelService = new ServiceHotel();

    public void setClientId(int clientId) throws SQLException {
        this.clientId = clientId;
        loadData();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hotelNameCol.setCellValueFactory(cellData -> {
            try {
                Hotels h = hotelService.recuperer().get(cellData.getValue().getHotel_id());
                return new ReadOnlyStringWrapper(h.getNom_hotel());
            } catch (Exception e) {
                return new ReadOnlyStringWrapper("Inconnu");
            }
        });

        hotelPriceCol.setCellValueFactory(cellData -> {
            try {
                Hotels h = hotelService.recuperer().get(cellData.getValue().getHotel_id());
                return new ReadOnlyStringWrapper(String.format("%.2f", h.getPrix()));
            } catch (Exception e) {
                return new ReadOnlyStringWrapper("0.00");
            }
        });

        checkinCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCheckin_date()));
        checkoutCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCheckout_date()));

        colPrixTotal.setCellValueFactory(cellData -> {
            try {
                LocalDate checkin = LocalDate.parse(cellData.getValue().getCheckin_date(), formatter);
                LocalDate checkout = LocalDate.parse(cellData.getValue().getCheckout_date(), formatter);
                long nbNuits = ChronoUnit.DAYS.between(checkin, checkout);
                Hotels hotel = hotelService.recuperer().get(cellData.getValue().getHotel_id());
                double total = hotel.getPrix() * nbNuits;
                return new ReadOnlyStringWrapper(String.format("%.2f TND", total));
            } catch (Exception e) {
                return new ReadOnlyStringWrapper("Erreur");
            }
        });
    }

    private void loadData() throws SQLException {
        List<ReservationHotel> filteredReservations = reservationService.recuperer()
                .stream()
                .filter(r -> r.getId_utilisateur() == clientId)
                .collect(Collectors.toList());

        table.setItems(FXCollections.observableArrayList(filteredReservations));
    }
}
