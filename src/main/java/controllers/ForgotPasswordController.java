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
import utils.EmailSender;

import javax.mail.MessagingException;
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

        // Afficher un indicateur de chargement
        statusLabel.setText("Recherche en cours...");

        try {
            // Vérification si l'email existe dans la base de données
            if (serviceClient.emailExists(email)) {
                // Récupérer le client pour passer son ID au contrôleur de vérification
                List<Client> clients = serviceClient.recuperer();
                Client foundClient = null;

                for (Client client : clients) {
                    if (client.getEmail().equals(email)) {
                        foundClient = client;
                        break;
                    }
                }

                if (foundClient != null) {
                    // Générer un code de vérification
                    String verificationCode = generateVerificationCode();
                    final Client finalFoundClient = foundClient;

                    // Pour éviter que l'UI se bloque, nous utilisons un thread séparé pour l'envoi d'email
                    statusLabel.setText("Envoi du code de vérification...");

                    // Utiliser un thread séparé pour l'envoi de l'email
                    new Thread(() -> {
                        try {
                            // Envoyer le code par email
                            EmailSender.sendVerificationCode(email, verificationCode);

                            // Retourner à l'UI thread pour mettre à jour l'interface
                            javafx.application.Platform.runLater(() -> {
                                try {
                                    // Passer à la page de vérification du code
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/VerifyCode.fxml"));
                                    Parent root = loader.load();

                                    // Obtenir le contrôleur et lui passer les informations nécessaires
                                    VerifyCodeController controller = loader.getController();
                                    controller.initialize(finalFoundClient, verificationCode, email);

                                    // Afficher la nouvelle fenêtre
                                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                    stage.setScene(new Scene(root));
                                    stage.setTitle("Vérification du code");
                                    stage.show();
                                } catch (IOException e) {
                                    statusLabel.setText("Erreur lors du chargement de la page");
                                    System.out.println(e.getMessage());
                                }
                            });
                        } catch (MessagingException e) {
                            // Retourner à l'UI thread pour afficher l'erreur
                            javafx.application.Platform.runLater(() -> {
                                statusLabel.setText("Erreur lors de l'envoi de l'email. Veuillez réessayer.");
                                System.out.println("Erreur d'envoi d'email: " + e.getMessage());
                            });
                        }
                    }).start();
                }
            } else {
                statusLabel.setText("Cet email n'existe pas dans notre système");
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur de connexion à la base de données");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Génère un code de vérification aléatoire à 6 chiffres
     * @return Le code de vérification généré
     */
    private String generateVerificationCode() {
        // Générer un code à 6 chiffres
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
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