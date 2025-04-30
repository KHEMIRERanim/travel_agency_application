package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.time.LocalDate;

public class ChercherVolController {

    @FXML
    private TextField departurechercher;

    @FXML
    private TextField destinationchercher;

    @FXML
    private TextField adultchercher;

    @FXML
    private TextField childchercher;

    @FXML
    private DatePicker flightdatechercher;

    @FXML
    private DatePicker returndatechercher;

    @FXML
    private FlowPane flowPane;

    @FXML
    public void recherchervol(ActionEvent event) {
        System.out.println("Recherche de vol lancée...");

        String departure = departurechercher.getText();
        String destination = destinationchercher.getText();
        String departureDate = (flightdatechercher.getValue() != null) ? flightdatechercher.getValue().toString() : "";
        String returnDate = (returndatechercher.getValue() != null) ? returndatechercher.getValue().toString() : "";
        String numAdults = adultchercher.getText();
        String numChildren = childchercher.getText();

        System.out.println("Champs saisis :");
        System.out.println("Départ : " + departure);
        System.out.println("Destination : " + destination);
        System.out.println("Date de départ : " + departureDate);
        System.out.println("Date de retour : " + returnDate);
        System.out.println("Adultes : " + numAdults);
        System.out.println("Enfants : " + numChildren);

        if (departure.isEmpty() || destination.isEmpty() || departureDate.isEmpty() || returnDate.isEmpty() || numAdults.isEmpty() || numChildren.isEmpty()) {
            showAlert("Veuillez remplir tous les champs.", AlertType.WARNING);
            System.out.println("Erreur : champs vides.");
            return;
        }

        if (isReturnDateBeforeDeparture(departureDate, returnDate)) {
            showAlert("La date de retour doit être postérieure à la date de départ.", AlertType.WARNING);
            System.out.println("Erreur : la date de retour est avant la date de départ.");
            return;
        }

        try {
            boolean flightFound = simulateFlightSearch(departure, destination, departureDate, returnDate, numAdults, numChildren);

            if (flightFound) {
                displaySearchResults(departure, destination, departureDate, returnDate);
                showAlert("Un vol correspondant a été trouvé !", AlertType.INFORMATION);
                System.out.println("Vol trouvé.");
            } else {
                showAlert("Aucun vol ne correspond aux critères.", AlertType.INFORMATION);
                System.out.println("Aucun vol trouvé.");
            }

        } catch (Exception e) {
            showAlert("Erreur lors de la recherche de vols. Veuillez vérifier la connexion à la base de données.", AlertType.ERROR);
            System.out.println("Exception : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Résultat de recherche de vol");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isReturnDateBeforeDeparture(String departureDate, String returnDate) {
        LocalDate depDate = LocalDate.parse(departureDate);
        LocalDate retDate = LocalDate.parse(returnDate);
        return retDate.isBefore(depDate);
    }

    private boolean simulateFlightSearch(String departure, String destination, String departureDate, String returnDate, String numAdults, String numChildren) throws Exception {
        if (departure.equalsIgnoreCase("DBERROR")) {
            throw new Exception("Erreur de base de données simulée.");
        }
        return !departure.equalsIgnoreCase(destination);
    }

    private void displaySearchResults(String departure, String destination, String departureDate, String returnDate) {
        flowPane.getChildren().clear();

        VBox flightResult = new VBox(10);
        flightResult.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-background-radius: 5;");

        flightResult.getChildren().add(new Label("Départ : " + departure));
        flightResult.getChildren().add(new Label("Destination : " + destination));
        flightResult.getChildren().add(new Label("Date de départ : " + departureDate));
        flightResult.getChildren().add(new Label("Date de retour : " + returnDate));

        flowPane.getChildren().add(flightResult);
    }
}
