package controllers;

import entities.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceClient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ForgotPasswordController {
    private ServiceClient serviceClient = new ServiceClient();

    @FXML
    private TextField tf_email;

    @FXML
    private Label statusLabel;

    @FXML
    void verifyEmail(ActionEvent event) {
        String email = tf_email.getText().trim();

        // Validation basique de l'email
        if (email.isEmpty()) {
            statusLabel.setText("Veuillez entrer votre adresse email");
            return;
        }

        try {
            // Vérification si l'email existe dans la base de données
            if (serviceClient.emailExists(email)) {
                // Récupérer le client pour passer son ID au contrôleur de réinitialisation
                List<Client> clients = serviceClient.recuperer();
                Client foundClient = null;

                for (Client client : clients) {
                    if (client.getEmail().equals(email)) {
                        foundClient = client;
                        break;
                    }
                }

                if (foundClient != null) {
                    // Passer à la page de réinitialisation du mot de passe
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
                    Parent root = loader.load();

                    // Obtenir le contrôleur et lui passer le client
                    ResetPasswordController controller = loader.getController();
                    controller.setClient(foundClient);

                    // Afficher la nouvelle fenêtre
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Réinitialiser mot de passe");
                    stage.show();
                }
            } else {
                statusLabel.setText("Cet email n'existe pas dans notre système");
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur de connexion à la base de données");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            statusLabel.setText("Erreur lors du chargement de la page");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}