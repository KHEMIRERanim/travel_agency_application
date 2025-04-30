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
    void ouvrirAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVol.fxml"));
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            // Adapter la taille de la fenêtre
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

            // Ajouter le contenu à contentArea
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            // Récupérer le Stage actuel (fenêtre)
            Stage stage = (Stage) contentArea.getScene().getWindow();

            // Définir la taille de la fenêtre (par exemple, largeur 1200px et hauteur 800px)
            stage.setWidth(1200);  // Largeur
            stage.setHeight(800);  // Hauteur

            // Ajuster la taille de la fenêtre en fonction du contenu de la scène
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

            // Ajouter le contenu à contentArea
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            // Récupérer le Stage actuel (fenêtre)
            Stage stage = (Stage) contentArea.getScene().getWindow();

            // Définir la taille de la fenêtre (par exemple, largeur 1200px et hauteur 800px)
            stage.setWidth(1200);  // Largeur
            stage.setHeight(800);  // Hauteur

            // Ajuster la taille de la fenêtre en fonction du contenu de la scène
            stage.sizeToScene();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}