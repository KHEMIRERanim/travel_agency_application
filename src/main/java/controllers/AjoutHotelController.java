package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import entities.Hotels;
import services.ServiceHotel;

import java.sql.SQLException;

public class AjoutHotelController {
    @FXML private TextField nomField;
    @FXML private TextField destinationField;
    @FXML private CheckBox wifiCheck;
    @FXML private CheckBox piscineCheck;
    @FXML private TextField prixField;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private Label errorLabel;

    private ServiceHotel service = new ServiceHotel();
    private Hotels hotelToEdit = null;

    public void setHotelForEdit(Hotels hotel) {
        this.hotelToEdit = hotel;

        nomField.setText(hotel.getNom_hotel());
        destinationField.setText(hotel.getDestination());
        prixField.setText(String.valueOf(hotel.getPrix()));
        typeChoice.setValue(hotel.getType_chambre());
        wifiCheck.setSelected(hotel.isWifi());
        piscineCheck.setSelected(hotel.isPiscine());
    }

    @FXML
    public void initialize() {
        typeChoice.getItems().addAll("Single", "Double", "Twin");
    }

    @FXML
    private void onSubmit() throws SQLException {
        errorLabel.setText("");

        if (nomField.getText().isBlank() || destinationField.getText().isBlank()
                || prixField.getText().isBlank() || typeChoice.getValue() == null) {
            errorLabel.setText("Tous les champs sont obligatoires.");
            return;
        }

        double prix;
        try {
            prix = Double.parseDouble(prixField.getText());
            if (prix < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            errorLabel.setText("Prix invalide. Entrez un nombre positif.");
            return;
        }

        if (hotelToEdit != null) {
            hotelToEdit.setNom_hotel(nomField.getText());
            hotelToEdit.setDestination(destinationField.getText());
            hotelToEdit.setPrix(prix);
            hotelToEdit.setType_chambre(typeChoice.getValue());
            hotelToEdit.setWifi(wifiCheck.isSelected());
            hotelToEdit.setPiscine(piscineCheck.isSelected());

            service.modifier(hotelToEdit);
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();
        } else {

            try {
                Hotels hotel = new Hotels(
                        nomField.getText(),
                        destinationField.getText(),
                        prix,
                        typeChoice.getValue(),
                        "Disponible",
                        wifiCheck.isSelected(),
                        piscineCheck.isSelected()
                );
                service.ajouter(hotel);
                Stage stage = (Stage) nomField.getScene().getWindow();
                stage.close();
            } catch (Exception ex) {
                errorLabel.setText("Erreur lors de l'ajout : " + ex.getMessage());
            }
        }

    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}