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

    // Méthode pour recevoir les données du client depuis la connexion
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

    // Charger le contenu dans la zone de contenu et ajuster les ancrages
    public void loadContentToArea(Parent content) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);

        // Ajuster le contenu pour remplir toute la zone de contenu
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
    }

    @FXML
    void reserveFlight(ActionEvent event) {
        try {
            // Charger le fichier ChercherVol.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
            Parent flightSearchView = loader.load();

            // Obtenir le contrôleur et définir la référence au tableau de bord
            ChercherVolController controller = loader.getController();
            controller.setDashboardController(this);
            controller.setClient(currentClient); // Ajout : Passer le client

            // Effacer la zone de contenu et ajouter la vue de recherche de vols
            loadContentToArea(flightSearchView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger la réservation de vol: " + e.getMessage());
        }
    }

    @FXML
    void reserveHotel(ActionEvent event) {
        try {
            // Placeholder pour une fonctionnalité future - charger l'écran de réservation d'hôtel
            contentArea.getChildren().clear();
            Label label = new Label("Réservation d'hôtel");
            label.setLayoutX(200);
            label.setLayoutY(200);
            label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            contentArea.getChildren().add(label);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger la réservation d'hôtel: " + e.getMessage());
        }
    }
    @FXML
    void reserveTransport(ActionEvent event) {
        try {
            // Charger ton fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CatalogueVehicules.fxml"));
            Parent transportView = loader.load();

            // Récupérer ton contrôleur et établir une communication (si nécessaire)
            CatalogueVehiculesController controller = loader.getController();
            controller.setDashboardController(this); // Passe une référence au contrôleur principal

            // Charger ton interface dans la zone de contenu
            contentArea.getChildren().setAll(transportView);
        } catch (IOException e) {
            // Afficher une alerte en cas d'erreur
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger la réservation de transport: " + e.getMessage());
        }
    }

    @FXML
    void showProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserProfile.fxml"));
            Parent profileView = loader.load();

            // Passer le client au contrôleur de profil
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MyReservations.fxml"));
            Parent reservationsView = loader.load();

            // Passer le client au contrôleur des réservations
            MyReservationsController controller = loader.getController();
            controller.setClient(currentClient);

            loadContentToArea(reservationsView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger les réservations: " + e.getMessage());
        }
    }

    @FXML
    void showComplaints(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientReclamations.fxml"));
            Parent reclamationsView = loader.load();

            // Passer le client au contrôleur des réclamations
            ClientReclamationsController controller = loader.getController();
            controller.setClient(currentClient);

            loadContentToArea(reclamationsView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger les réclamations: " + e.getMessage());
        }
    }

    @FXML
    void planTrip(ActionEvent event) {
        // Placeholder pour une fonctionnalité future
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