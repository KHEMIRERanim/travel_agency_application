package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.web.WebView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

import entities.Hotels;
import services.ServiceHotel;

import java.io.*;
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
    @FXML private HBox noteBox;

    private final Image STAR_FULL = new Image(getClass().getResource("/images/star.png").toExternalForm());
    private final Image STAR_EMPTY = new Image(getClass().getResource("/images/star_empty.png").toExternalForm());
    private int currentNote = 1;
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
        this.currentNote = hotel.getNote();
        drawStars();
        byte[] hotelImage = hotel.getImage();
        if (hotelImage != null && hotelImage.length > 0) {
            imageBytes = hotelImage;
            Image image = new Image(new ByteArrayInputStream(hotelImage));
            imageView.setImage(image);
        }
    }

    @FXML
    public void initialize() {
        typeChoice.getItems().addAll("Single", "Double", "Twin");
        drawStars();
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
            hotelToEdit.setImage(imageBytes);
            hotelToEdit.setNote(currentNote);

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
                        imageBytes,
                        currentNote
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
                Image image = new Image(new FileInputStream(selectedFile));
                imageView.setImage(image);
                imageBytes = convertImageToByteArray(selectedFile);
            } catch (IOException e) {
                errorLabel.setText("Erreur lors du chargement de l'image.");
            }
        }
    }

    @FXML
    private void onOpenMap() {
        try {
            Stage mapStage = new Stage();
            mapStage.initModality(Modality.APPLICATION_MODAL);
            mapStage.setTitle("Sélectionner une destination");

            WebView webView = new WebView();
            webView.setPrefSize(800, 600);

            String htmlContent =
                    """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="utf-8" />
                        <title>Carte interactive</title>
                        <style>
                            html, body {
                                height: 100%;
                                margin: 0;
                                padding: 0;
                            }
                            #map {
                                position: absolute;
                                top: 0; left: 0;
                                right: 0; bottom: 0;
                                z-index: 0;
                            }
                            #info {
                                position: absolute;
                                bottom: 10px;
                                left: 10px;
                                background: rgba(255, 255, 255, 0.9);
                                padding: 10px 14px;
                                border-radius: 6px;
                                font-family: Arial, sans-serif;
                                font-size: 14px;
                                color: #333;
                                z-index: 1000;
                                box-shadow: 0 2px 8px rgba(0,0,0,0.15);
                            }
                        </style>
                        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.3/dist/leaflet.css" />
                        <script src="https://unpkg.com/leaflet@1.9.3/dist/leaflet.js"></script>
                    </head>
                    <body>
                        <div id="map"></div>
                        <div id="info">Cliquez sur une position pour choisir une destination</div>
                        <script>
                            var selectedCountry = "";
                            var map = L.map('map').setView([34.0, 9.0], 5);

                            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                                maxZoom: 19,
                                attribution: '&copy; OpenStreetMap contributors'
                            }).addTo(map);

                            var marker = null;

                            map.on('click', function(e) {
                                var latlng = e.latlng;

                                fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${latlng.lat}&lon=${latlng.lng}`)//appel api
                                    .then(response => response.json())
                                    .then(data => {
                                        if (data && data.address) {
                                            selectedCountry = data.address.city || data.address.town || data.address.village || data.address.country || "";
                                            document.getElementById("info").innerHTML = "Destination sélectionnée : <strong>" + selectedCountry + "</strong>";
                                            if (marker) {
                                                marker.setLatLng(latlng);
                                            } else {
                                                marker = L.marker(latlng).addTo(map);
                                            }
                                        }
                                    })
                                    .catch(() => {
                                        selectedCountry = "";
                                        document.getElementById("info").innerHTML = "<span style='color:red'>Erreur lors de la récupération du nom de l'endroit.</span>";
                                    });
                            });
                        </script>
                    </body>
                    </html>
                    """;

            webView.getEngine().loadContent(htmlContent);

            Button selectButton = new Button("Sélectionner cette destination");
            selectButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px;");
            selectButton.setPrefWidth(200);

            VBox root = new VBox();
            root.getChildren().add(webView);
            root.getChildren().add(selectButton);
            VBox.setMargin(selectButton, new javafx.geometry.Insets(10, 0, 10, 10));

            selectButton.setOnAction(e -> {
                Object result = webView.getEngine().executeScript("selectedCountry");
                if (result != null && !result.toString().isEmpty()) {
                    destinationField.setText(result.toString());
                    mapStage.close();
                } else {
                    webView.getEngine().executeScript(
                            "document.getElementById('info').innerHTML = '<span style=\"color:red\">Veuillez d\\'abord sélectionner un pays!</span>';"
                    );
                }
            });

            Scene scene = new Scene(root, 800, 650);
            mapStage.setScene(scene);
            mapStage.show();
        } catch (Exception e) {
            errorLabel.setText("Erreur lors de l'ouverture de la carte: " + e.getMessage());
            e.printStackTrace();
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

    private void drawStars() {
        noteBox.getChildren().clear();
        for (int i = 1; i <= 5; i++) {
            ImageView star = new ImageView(i <= currentNote ? STAR_FULL : STAR_EMPTY);
            star.setFitWidth(20);
            star.setFitHeight(20);
            final int rating = i;
            star.setCursor(Cursor.HAND);
            star.setOnMouseClicked(e -> {
                currentNote = rating;
                drawStars();
            });
            noteBox.getChildren().add(star);
        }
    }
}
