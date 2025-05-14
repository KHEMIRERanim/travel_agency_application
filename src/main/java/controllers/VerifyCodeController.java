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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceClient;
import utils.EmailSender;

import java.io.IOException;

public class VerifyCodeController {

    private ServiceClient serviceClient = new ServiceClient();
    private Client currentClient;
    private String verificationCode;
    private String email;

    @FXML
    private TextField tf_code;

    @FXML
    private Label statusLabel;

    @FXML
    private Label emailLabel;

    /**
     * Initialise le contrôleur avec les informations nécessaires
     * @param client Le client qui réinitialise son mot de passe
     * @param code Le code de vérification envoyé par email
     * @param email L'adresse email du client
     */
    public void initialize(Client client, String code, String email) {
        this.currentClient = client;
        this.verificationCode = code;
        this.email = email;
        this.emailLabel.setText(email);
    }

    @FXML
    void verifyCode(ActionEvent event) {
        String enteredCode = tf_code.getText().trim();

        if (enteredCode.isEmpty()) {
            statusLabel.setText("Veuillez entrer le code de vérification");
            return;
        }

        // Vérifier si le code entré correspond au code envoyé
        if (enteredCode.equals(verificationCode)) {
            try {
                // Charger la page de réinitialisation du mot de passe
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
                Parent root = loader.load();

                // Obtenir le contrôleur et lui passer le client
                ResetPasswordController controller = loader.getController();
                controller.setClient(currentClient);

                // Afficher la nouvelle fenêtre
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Réinitialiser mot de passe");
                stage.show();
            } catch (IOException e) {
                statusLabel.setText("Erreur lors du chargement de la page");
                System.out.println(e.getMessage());
            }
        } else {
            statusLabel.setText("Code de vérification incorrect");
        }
    }

    @FXML
    void resendCode(ActionEvent event) {
        // Désactiver le bouton pendant l'envoi
        ((Button) event.getSource()).setDisable(true);
        statusLabel.setText("Envoi en cours...");

        // Régénérer et renvoyer un nouveau code
        String newCode = generateVerificationCode();
        this.verificationCode = newCode;

        // Utiliser un thread séparé pour l'envoi de l'email
        new Thread(() -> {
            try {
                // Envoyer le nouveau code par email
                EmailSender.sendVerificationCode(email, newCode);

                // Retourner à l'UI thread pour mettre à jour l'interface
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("Un nouveau code a été envoyé à votre adresse email");
                    ((Button) event.getSource()).setDisable(false);
                });
            } catch (Exception e) {
                // Retourner à l'UI thread pour afficher l'erreur
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("Erreur lors de l'envoi du code");
                    System.out.println(e.getMessage());
                    ((Button) event.getSource()).setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    void goBack(ActionEvent event) {
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

    /**
     * Envoie un email contenant le code de vérification
     * @param email L'adresse email du destinataire
     * @param code Le code de vérification à envoyer
     */
    private void sendVerificationEmail(String email, String code) {
        // Vous devrez implémenter cette méthode selon votre service d'envoi d'emails
        // Cette méthode est appelée par ForgotPasswordController et lors du renvoi du code
        System.out.println("Envoi d'un email à " + email + " avec le code: " + code);
        // Implémentez l'envoi d'email ici
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
}