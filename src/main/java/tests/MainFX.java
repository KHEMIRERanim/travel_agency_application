package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger l'interface principale du menu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MenuPrincipale.fxml"));
            Parent root = loader.load();

            // Créer la scène avec le contenu chargé
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            // Définir la taille initiale de la fenêtre (par exemple, largeur 1200px et hauteur 800px)
            primaryStage.setWidth(1200);  // Largeur
            primaryStage.setHeight(800);  // Hauteur

            // Titre de la fenêtre
            primaryStage.setTitle("Gestion des Vols");

            // Empêcher le redimensionnement de la fenêtre (facultatif)
            primaryStage.setResizable(false);

            // Afficher la fenêtre
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}