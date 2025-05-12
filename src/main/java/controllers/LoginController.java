package controllers;

import entities.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import services.ServiceClient;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    private ServiceClient serviceClient = new ServiceClient();

    @FXML
    private TextField tf_email;

    @FXML
    private PasswordField tf_password;

    @FXML
    private TextField tf_password_visible;

    @FXML
    private Button btn_toggle_password;

    private boolean passwordVisible = false;

    @FXML
    private Label errorLabel;

    @FXML
    void login(ActionEvent event) {
        String email = tf_email.getText();
        String password = passwordVisible ? tf_password_visible.getText() : tf_password.getText();

        // Check if fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        try {
            Client client = serviceClient.authenticate(email, password);
            
            if (client != null) {
                if (client.getRole().equals("ADMIN")) {
                    // Open admin dashboard
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboard.fxml"));
                        Parent root = loader.load();
                        AdminDashboardController controller = loader.getController();
                        controller.setCurrentLoggedInClient(client);
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Dashboard Admin");
                        stage.show();
                    } catch (IOException e) {
                        errorLabel.setText("Erreur système: " + e.getMessage());
                        System.out.println(e.getMessage());
                    }
                } else {
                    // Open user dashboard
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDashboard.fxml"));
                        Parent root = loader.load();

                        UserDashboardController controller = loader.getController();
                        controller.setClient(client);

                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Dashboard Utilisateur");
                        stage.show();
                    } catch (IOException e) {
                        errorLabel.setText("Erreur système: " + e.getMessage());
                        System.out.println(e.getMessage());
                    }
                }
            } else {
                errorLabel.setText("Email ou mot de passe incorrect");
            }
        } catch (SQLException e) {
            errorLabel.setText("Erreur de connexion à la base de données");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void goToSignUp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPersonne.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inscription");
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void forgotPassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgotPassword.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mot de passe oublié");
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void togglePasswordVisibility(ActionEvent event) {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            tf_password_visible.setText(tf_password.getText());
            tf_password_visible.setVisible(true);
            tf_password_visible.setManaged(true);
            tf_password.setVisible(false);
            tf_password.setManaged(false);
            btn_toggle_password.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/eye-off.png"))));
        } else {
            tf_password.setText(tf_password_visible.getText());
            tf_password.setVisible(true);
            tf_password.setManaged(true);
            tf_password_visible.setVisible(false);
            tf_password_visible.setManaged(false);
            btn_toggle_password.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/eye.png"))));
        }
    }
}