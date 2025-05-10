package controllers;

import entities.ReservationHotel;
import entities.Hotels;
import entities.ReservationHotel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class RecureservController {

    @FXML
    private TextField clientNameField;

    @FXML
    private TextField hotelNameField;

    @FXML
    private TextField checkInDateField;

    @FXML
    private TextField checkOutDateField;

    @FXML
    private TextField totalAmountField;

    @FXML
    private Button generatePdfButton;

    private ReservationHotel reservation;
}

