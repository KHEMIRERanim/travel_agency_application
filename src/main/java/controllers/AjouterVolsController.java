package controllers;
import java.sql.*;
import entities.Flight;
import services.ServiceFlight;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AjouterVolsController {

    @FXML
    private TextField AddFlightnumber;
    @FXML
    private TextField AddDeparture;
    @FXML
    private TextField AddDestination;
    @FXML
    private TextField AddDeparture_Time;
    @FXML
    private TextField AddArrivalTime;
    @FXML
    private DatePicker AddFlightDate;
    @FXML
    private TextField AddFlightDuration;
    @FXML
    private TextField AddAvailableSeats;
    @FXML
    private TextField AddAirline;
    @FXML
    private TextField AddPrice;

    // Remplacer le TextField par un Button et un Label
    @FXML
    private Button btnChooseImage;
    @FXML
    private Label lblImagePath;

    // Variable pour stocker le chemin de l'image sélectionnée
    private String selectedImagePath = "";

    ServiceFlight serviceFlight = new ServiceFlight();

    @FXML
    void initialize() {
        // Initialiser le label pour afficher "Aucune image sélectionnée"
        if (lblImagePath != null) {
            lblImagePath.setText("Aucune image sélectionnée");
        }
    }

    @FXML
    void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");

        // Configurer les filtres pour n'afficher que les fichiers image
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Fichiers image", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);

        // Ouvrir le sélecteur de fichier
        File selectedFile = fileChooser.showOpenDialog(btnChooseImage.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Obtenir le chemin absolu du fichier sélectionné
                selectedImagePath = selectedFile.getAbsolutePath();

                // Option 1: Utiliser directement le chemin local
                // lblImagePath.setText(selectedImagePath);

                // Option 2: Copier l'image dans un dossier du projet et utiliser un chemin relatif
                String projectDir = System.getProperty("user.dir");
                String imagesDir = projectDir + "/src/resources/images/";

                // Créer le répertoire s'il n'existe pas
                File directory = new File(imagesDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Copier le fichier dans le dossier des images
                String fileName = selectedFile.getName();
                Path sourcePath = selectedFile.toPath();
                Path targetPath = Paths.get(imagesDir + fileName);
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Stocker le chemin relatif pour la base de données
                selectedImagePath = "resources/images/" + fileName;

                // Mettre à jour le label pour afficher le nom du fichier
                lblImagePath.setText(fileName);

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Erreur lors de la sélection de l'image: " + e.getMessage());
            }
        }
    }

    @FXML
    void AddFlight() {
        try {
            String flightNumber = AddFlightnumber.getText();
            String departure = AddDeparture.getText();
            String destination = AddDestination.getText();
            String airline = AddAirline.getText();
            String imageUrl = selectedImagePath; // Utiliser le chemin de l'image sélectionnée

            if (flightNumber.isEmpty() || departure.isEmpty() || destination.isEmpty() || airline.isEmpty() ||
                    AddFlightDuration.getText().isEmpty() || AddAvailableSeats.getText().isEmpty() || AddPrice.getText().isEmpty() ||
                    AddDeparture_Time.getText().isEmpty() || AddArrivalTime.getText().isEmpty() || AddFlightDate.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            // Vérifier si une image a été sélectionnée
            if (selectedImagePath.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Attention",
                        "Aucune image n'a été sélectionnée. Voulez-vous continuer sans image?");
                // Vous pouvez ajouter une logique pour confirmer ou annuler ici si nécessaire
            }

            int flightDuration = Integer.parseInt(AddFlightDuration.getText());
            int availableSeats = Integer.parseInt(AddAvailableSeats.getText());
            double price = Double.parseDouble(AddPrice.getText());

            LocalDate flightDateLocal = AddFlightDate.getValue();
            Date flightDate = Date.valueOf(flightDateLocal);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            LocalTime departureTimeLocal = LocalTime.parse(AddDeparture_Time.getText(), timeFormatter);
            LocalTime arrivalTimeLocal = LocalTime.parse(AddArrivalTime.getText(), timeFormatter);

            LocalDateTime departureDateTime = LocalDateTime.of(flightDateLocal, departureTimeLocal);
            LocalDateTime arrivalDateTime = LocalDateTime.of(flightDateLocal, arrivalTimeLocal);

            Timestamp departureTimestamp = Timestamp.valueOf(departureDateTime);
            Timestamp arrivalTimestamp = Timestamp.valueOf(arrivalDateTime);

            Flight flight = new Flight(
                    flightDuration,
                    flightNumber,
                    availableSeats,
                    departure,
                    destination,
                    airline,
                    arrivalTimestamp,
                    departureTimestamp,
                    flightDate,
                    price,
                    imageUrl
            );

            serviceFlight.ajouter(flight);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Vol ajouté avec succès !");

            clearFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Veuillez saisir des nombres valides pour la durée, les places disponibles et le prix.");
        } catch (java.time.format.DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format d'heure", "Veuillez saisir l'heure au format HH:mm (exemple : 12:30).");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de l'ajout du vol : " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    private void clearFields() {
        AddFlightnumber.clear();
        AddDeparture.clear();
        AddDestination.clear();
        AddDeparture_Time.clear();
        AddArrivalTime.clear();
        AddFlightDate.setValue(null);
        AddFlightDuration.clear();
        AddAvailableSeats.clear();
        AddAirline.clear();
        AddPrice.clear();
        // Réinitialiser l'image sélectionnée
        selectedImagePath = "";
        lblImagePath.setText("Aucune image sélectionnée");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}