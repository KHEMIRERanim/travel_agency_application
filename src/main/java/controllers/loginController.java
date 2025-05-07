package controllers;

import entities.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    private Label errorLabel;

    @FXML
    void login(ActionEvent event) {
        String email = tf_email.getText();
        String password = tf_password.getText();

        // Check if fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        // Check for admin login
        if (email.equals("admin@gmail.com") && password.equals("admin")) {
            // Open admin dashboard
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboard.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard Admin");
                stage.show();
            } catch (IOException e) {
                errorLabel.setText("Erreur système: " + e.getMessage());
                System.out.println(e.getMessage());
            }
            return;
        }

        // Check user login
        try {
            boolean found = false;
            for (Client client : serviceClient.recuperer()) {
                if (client.getEmail().equals(email) && client.getMot_de_passe().equals(password)) {
                    found = true;

                    // Open user dashboard with client information
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
                    break;
                }
            }

            if (!found) {
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
}