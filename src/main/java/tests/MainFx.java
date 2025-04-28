package tests;

import controllers.AjouterPersonneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import services.ServiceClient;
import java.io.IOException;

public class MainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AjouterPersonne.fxml"));

            // 2. Load the root layout
            Parent root = fxmlLoader.load();

            // 3. Get the controller instance
            AjouterPersonneController controller = fxmlLoader.getController();

            // 4. Initialize and inject the service
            ServiceClient serviceClient = new ServiceClient();
            controller.setServiceClient(serviceClient);

            // 5. Create and configure the scene
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Ajouter une Personne");

            // 6. Configure window behavior
            primaryStage.setResizable(false); // Optional: prevents window resizing

            // 7. Show the application window
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Erreur de chargement de l'interface:");
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible de charger l'interface", e.getMessage());
        }
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}