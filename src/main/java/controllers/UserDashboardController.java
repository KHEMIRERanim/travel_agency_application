package controllers;

import entities.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ServiceClient;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserDashboardController implements Initializable {
    private ServiceClient serviceClient = new ServiceClient();
    private Client currentClient;
    private boolean isReservationsMenuOpen = false;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Label welcomeLabel;

    @FXML
    private VBox reservationsSubMenu;

    @FXML
    private Button reservationsBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the controller
    }

    // Method to receive client data from login
    public void setClient(Client client) {
        this.currentClient = client;
        welcomeLabel.setText("Bonjour, " + client.getPrenom() + " " + client.getNom() + "!");
    }

    @FXML
    void toggleReservationsMenu(ActionEvent event) {
        isReservationsMenuOpen = !isReservationsMenuOpen;
        reservationsSubMenu.setVisible(isReservationsMenuOpen);
        reservationsSubMenu.setManaged(isReservationsMenuOpen);

        // Change button style to indicate active state if submenu is open
        if (isReservationsMenuOpen) {
            reservationsBtn.setStyle("-fx-background-color: #17316f; -fx-text-fill: white;");
        } else {
            reservationsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        }
    }

    // Load content to the content area and properly set anchors
    public void loadContentToArea(Parent content) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);

        // Adjust the content to fill the entire content area
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
    }

    @FXML
    void reserveFlight(ActionEvent event) {
        try {
            // Load the ChercherVol.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
            Parent flightSearchView = loader.load();

            // Get the controller and set the dashboard reference
            ChercherVolController controller = loader.getController();
            controller.setDashboardController(this);

            // Clear the content area and add the flight search view
            loadContentToArea(flightSearchView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger la réservation de vol: " + e.getMessage());
        }
    }

    @FXML
    void reserveHotel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeHotelsReservation.fxml"));
            Parent hotelsView = loader.load();

            ListeHotelsReservationController controller = loader.getController();
            controller.setClient(currentClient);

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(hotelsView);
            } else {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(hotelsView));
                stage.show();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger la gestion des hotels: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void reserveTransport(ActionEvent event) {
        try {
            // Placeholder for future functionality - load transport reservation screen
            contentArea.getChildren().clear();
            Label label = new Label("Réservation de transport");
            label.setLayoutX(200);
            label.setLayoutY(200);
            label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            contentArea.getChildren().add(label);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger la réservation de transport: " + e.getMessage());
        }
    }

    @FXML
    void showProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserProfile.fxml"));
            Parent profileView = loader.load();

            // Pass client to profile controller
            UserProfileController controller = loader.getController();
            controller.setClient(currentClient);

            loadContentToArea(profileView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger le profil: " + e.getMessage());
        }
    }

    @FXML
    void showReservations(ActionEvent event) {
        // This method is now replaced by the submenu, but kept for backward compatibility
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

            loadContentToArea(reclamationsView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger les réclamations: " + e.getMessage());
        }
    }

    @FXML
    void historiqueReservartionsHotel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HistoriqueReservationsHotel.fxml"));
            Parent historiqueView = loader.load();

            HistoriqueReservationsHotelController controller = loader.getController();
            controller.setClientId(currentClient.getId_client());

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(historiqueView);
            } else {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(historiqueView));
                stage.show();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger la gestion des hotels: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void planTrip(ActionEvent event) {
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