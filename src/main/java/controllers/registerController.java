package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;
import utils.MyDatabase;

import java.io.File;
import java.sql.*;

public class registerController {

    @FXML
    private ImageView logoHotel;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField firstnameTextField;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField telephoneTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private PasswordField confirmPasswordTextField;
    @FXML
    private Label registerMessageLabel;

    private MyDatabase database;

    // Initialize the controller
    public void initialize() {
        // Set logo image on registration screen
        File logoFile = new File("Hotel/images/logo.jpg");
        Image logoImage = new Image(logoFile.toURI().toString());
        logoHotel.setImage(logoImage);
    }

    // Handle back button action
    public void backButtonOnAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/homepage.fxml"));
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(new Scene(root, 600, 400));
            loginStage.show();
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handle cancel button action (close window)
    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // Register user to the system
    public void registerUser() {
        // Get database instance
        database = MyDatabase.getInstance();

        // Retrieve data from input fields
        String firstname = firstnameTextField.getText();
        String lastname = lastnameTextField.getText();
        String address = addressTextField.getText();
        String telephone = telephoneTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        String confirmPassword = confirmPasswordTextField.getText();
        String email = emailTextField.getText();

        // Check if all fields are filled
        if (firstname.isEmpty() || lastname.isEmpty() || address.isEmpty() || telephone.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            // Display error message if any field is empty
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill all the fields.");
            alert.show();
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            // Display error message if passwords don't match
            Alert alert = new Alert(Alert.AlertType.ERROR, "Passwords do not match.");
            alert.show();
            return;
        }

        // Prepare SQL query to insert the data into the database
        String query = "INSERT INTO user (firstname, lastname, address, telephone, username, password, email) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = database.getConnection().prepareStatement(query)) {
            // Set parameters for the PreparedStatement
            stmt.setString(1, firstname);
            stmt.setString(2, lastname);
            stmt.setString(3, address);
            stmt.setString(4, telephone);
            stmt.setString(5, username);
            stmt.setString(6, password);
            stmt.setString(7, email);

            // Execute the insert query
            int rowsAffected = stmt.executeUpdate();

            // Check if the registration was successful
            if (rowsAffected > 0) {
                registerMessageLabel.setText("User has been registered successfully! Please go back to log in.");
            } else {
                registerMessageLabel.setText("Registration failed. Try again.");
            }
        } catch (SQLException e) {
            // Handle any SQL errors
            e.printStackTrace();
            registerMessageLabel.setText("Error: " + e.getMessage());
        }
    }
}
