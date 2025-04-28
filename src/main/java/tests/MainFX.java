package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Charger le fichier FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AjouterVol.fxml"));

        try {
            // Charger la scène
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);

            // Configurer la scène et la fenêtre
            primaryStage.setScene(scene);
            primaryStage.setTitle("AjouterVol");
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Erreur de chargement du fichier FXML : " + e.getMessage());
        }
    }
}
