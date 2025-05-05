package controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.MyDatabase;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class loginController implements Initializable {

    public static String firstname;
    public static String username;
    public static Integer user_id;

    @FXML
    private Label loginMessageLabel;
    @FXML
    private ImageView lobbyImageView;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button backButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File lobbyFile = new File(getClass().getResource("/images/lobbyHotel.jpg").toExternalForm());
        Image lobbyImage = new Image(lobbyFile.toURI().toString());
        lobbyImageView.setImage(lobbyImage);
        loginMessageLabel.setText("");
    }

    public void loginButtonOnAction(javafx.event.ActionEvent event) {
        if (!usernameField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Please enter username and password.");
        }
    }

    public void validateLogin() {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        try {
            Connection conn = MyDatabase.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usernameField.getText());
            stmt.setString(2, passwordField.getText());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                loginController.username = rs.getString("username");
                loginController.firstname = rs.getString("firstname"); // adjust column name if needed
                loginController.user_id = rs.getInt("id"); // adjust column name if needed

                // Load dashboard scene
                Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
                Stage loginStage = new Stage();
                loginStage.initStyle(StageStyle.UNDECORATED);
                loginStage.setScene(new Scene(root, 600, 400));
                loginStage.show();

                // Close current window
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.close();
            } else {
                loginMessageLabel.setText("Invalid login. Try again.");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            loginMessageLabel.setText("Database error.");
        }
    }

    public void backButtonOnAction(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("homepage.fxml"));
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(new Scene(root, 600, 400));
            loginStage.show();

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelButtonOnAction(javafx.event.ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
