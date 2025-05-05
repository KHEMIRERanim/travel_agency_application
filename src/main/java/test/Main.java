package test; // Ensure this matches your package structure

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// Main function of the program, core root, initializing first class and controller
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Correct path to the FXML file
        Parent root = FXMLLoader.load(getClass().getResource("/homepage.fxml"));

        // Set the stage style to undecorated (no window borders)
        primaryStage.initStyle(StageStyle.UNDECORATED);

        // Set the scene with the loaded FXML
        primaryStage.setScene(new Scene(root, 600, 400));

        // Show the stage (the application window)
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
