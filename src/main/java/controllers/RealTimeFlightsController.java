package controllers;

import entities.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;

public class RealTimeFlightsController {

    @FXML
    private WebView webView;
    @FXML
    private Button backButton;

    private Client currentClient;
    private ChercherVolController sourceController;
    private UserDashboardController dashboardController;
    private boolean isEmbeddedInDashboard = false;

    public void setClient(Client client) {
        this.currentClient = client;
    }

    public void setSourceController(ChercherVolController controller) {
        this.sourceController = controller;
    }

    public void setDashboardController(UserDashboardController controller) {
        this.dashboardController = controller;
        this.isEmbeddedInDashboard = (controller != null);
    }

    public void setRequiredSeats(int seats) {
        // Peut être utilisé pour filtrer les vols avec assez de sièges
    }

    @FXML
    public void initialize() {
        // Charger real-time-flights.html dans WebView
        String htmlPath = getClass().getResource("/real-time-flights.html").toExternalForm();
        if (htmlPath != null) {
            webView.getEngine().load(htmlPath);
        } else {
            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier non trouvé", "Impossible de charger real-time-flights.html"));
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
            Parent root = loader.load();
            ChercherVolController controller = loader.getController();
            controller.setClient(currentClient);
            if (isEmbeddedInDashboard && dashboardController != null) {
                controller.setDashboardController(dashboardController);
                dashboardController.loadContentToArea(root);
            } else {
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Recherche de vol");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation", "Impossible de revenir à la recherche : " + e.getMessage());
            e.printStackTrace();
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