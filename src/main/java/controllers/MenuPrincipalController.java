package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    private Button btnModifier;

    @FXML
    private AnchorPane contentArea;
    @FXML
    private Button btnsupprimer;

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
            e.printStackTrace();
        }
    }

    @FXML
    void ouvrirSupprimer() { // Nouvelle méthode pour ouvrir l'interface de suppression
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
            e.printStackTrace();
        }
    }
}