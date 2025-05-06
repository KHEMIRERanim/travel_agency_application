package controllers;

import entities.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import services.ServiceClient;

import java.io.IOException;

public class UserDashboardController {
    private ServiceClient serviceClient = new ServiceClient();
    private Client currentClient;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Label welcomeLabel;

    // Method to receive client data from login
    public void setClient(Client client) {
        this.currentClient = client;
        welcomeLabel.setText("Bonjour, " + client.getPrenom() + " " + client.getNom() + "!");
    }

    @FXML
    void showProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserProfile.fxml"));
            Parent profileView = loader.load();

            // Pass client to profile controller
            UserProfileController controller = loader.getController();
            controller.setClient(currentClient);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(profileView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", "Impossible de charger le profil: " + e.getMessage());
        }
    }

    @FXML
    void showReservations(ActionEvent event) {
        // Placeholder for future functionality
        contentArea.getChildren().clear();
        Label label = new Label("Vos réservations seront affichées ici");
        label.setLayoutX(200);
        label.setLayoutY(200);
        contentArea.getChildren().add(label);
    }

    @FXML
    void showComplaints(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientReclamations.fxml"));
            Parent reclamationsView = loader.load();

            // Pass client to reclamations controller
            ClientReclamationsController controller = loader.getController();
            controller.setClient(currentClient);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(reclamationsView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", "Impossible de charger les réclamations: " + e.getMessage());
        }
    }

    @FXML
    void planTrip(ActionEvent event) {
        // Placeholder for future functionality
        contentArea.getChildren().clear();
        Label label = new Label("Planifier un nouveau voyage");
        label.setLayoutX(200);
        label.setLayoutY(200);
        contentArea.getChildren().add(label);
    }

    @FXML
    void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de déconnexion", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}