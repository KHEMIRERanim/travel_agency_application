package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import entities.Hotels;
import services.ServiceHotel;

public class AjoutHotelController {
    @FXML private TextField nomField;
    @FXML private TextField destinationField;
    @FXML private CheckBox wifiCheck;
    @FXML private CheckBox piscineCheck;
    @FXML private TextField prixField;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private Label errorLabel;

    private ServiceHotel service = new ServiceHotel();

    @FXML
    public void initialize() {
        typeChoice.getItems().addAll("Single", "Double", "Twin");
    }

    @FXML
    private void onSubmit() {
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

        Hotels hotel = new Hotels(
                nomField.getText(),
                destinationField.getText(),
                prix,
                typeChoice.getValue(),
                "Disponible",
                wifiCheck.isSelected(),
                piscineCheck.isSelected()
        );

        try {
            service.ajouter(hotel);
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();
        } catch (Exception ex) {
            errorLabel.setText("Erreur lors de l'ajout : " + ex.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}