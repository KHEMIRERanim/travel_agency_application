package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MenuPrincipalController {
    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnAfficher;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Button btnAfficherReservations;

    @FXML
    void ouvrirAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVol.fxml"));
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.sizeToScene();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la page Ajouter Vol", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void ouvrirAfficher() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherVols.fxml"));
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.sizeToScene();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la page Afficher Vols", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void ouvrirModifier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierVol.fxml"));
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.sizeToScene();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la page Modifier Vol", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void ouvrirSupprimer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SupprimerVols.fxml"));
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.sizeToScene();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la page Supprimer Vols", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void ouvrirAfficherReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReservations.fxml"));
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.sizeToScene();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la page Afficher Réservations", e.getMessage());
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