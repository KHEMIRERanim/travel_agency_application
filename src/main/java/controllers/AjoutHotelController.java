package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import entities.Hotels;
import services.ServiceHotel;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.sql.SQLException;

public class AjoutHotelController {
    @FXML private TextField nomField;
    @FXML private TextField destinationField;
    @FXML private CheckBox wifiCheck;
    @FXML private CheckBox piscineCheck;
    @FXML private TextField prixField;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private Label errorLabel;
    @FXML private ImageView imageView;
    private byte[] imageBytes;

    private ServiceHotel service = new ServiceHotel();
    private Hotels hotelToEdit = null;
    private Runnable onHotelChanged;

    public void setOnHotelChanged(Runnable onHotelChanged) {
        this.onHotelChanged = onHotelChanged;
    }


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
            hotelToEdit.setImage(imageBytes);  // Set the image for editing

            service.modifier(hotelToEdit);
            if (onHotelChanged != null) onHotelChanged.run();
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
                        piscineCheck.isSelected(),
                        imageBytes  // Include the image while adding
                );
                service.ajouter(hotel);
                if (onHotelChanged != null) onHotelChanged.run();
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

    @FXML
    private void onChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Load the image and display it
                Image image = new Image(new FileInputStream(selectedFile));
                imageView.setImage(image);

                // Convert the image to byte[] for database storage
                imageBytes = convertImageToByteArray(selectedFile);

            } catch (IOException e) {
                errorLabel.setText("Erreur lors du chargement de l'image.");
            }
        }
    }

    private byte[] convertImageToByteArray(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }
        return bos.toByteArray();
    }

}