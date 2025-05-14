package controllers;

import entities.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import java.util.regex.Pattern;

public class ResetPasswordController {
    private ServiceClient serviceClient = new ServiceClient();
    private Client currentClient;

    @FXML
    private Label emailInfoLabel;

    @FXML
    private PasswordField tf_new_password;

    @FXML
    private PasswordField tf_confirm_password;

    @FXML
    private Label statusLabel;

    @FXML
    private Label passwordRequirements;

    @FXML
    private TextField tf_new_password_visible;

    @FXML
    private Button btn_toggle_new_password;

    private boolean newPasswordVisible = false;

    @FXML
    private TextField tf_confirm_password_visible;

    @FXML
    private Button btn_toggle_confirm_password;

    private boolean confirmPasswordVisible = false;

    public void setClient(Client client) {
        this.currentClient = client;
        // Afficher l'email du client pour confirmer
        emailInfoLabel.setText("Réinitialisation du mot de passe pour: " + client.getEmail());
    }

    @FXML
    void changePassword(ActionEvent event) {
        try {
            // Validation des champs
            String newPassword = newPasswordVisible ? tf_new_password_visible.getText() : tf_new_password.getText();
            String confirmPassword = confirmPasswordVisible ? tf_confirm_password_visible.getText() : tf_confirm_password.getText();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                statusLabel.setText("Veuillez remplir tous les champs");
                return;
            }

            // Vérification que les mots de passe correspondent
            if (!newPassword.equals(confirmPassword)) {
                statusLabel.setText("Les mots de passe ne correspondent pas");
                return;
            }

            // Vérification des critères de complexité du mot de passe
            if (!isValidPassword(newPassword)) {
                statusLabel.setText("Le mot de passe doit contenir au moins:\n• Une lettre majuscule\n• Un chiffre\n• Un caractère spécial");
                return;
            }

            // Mise à jour du mot de passe
            currentClient.setMot_de_passe(newPassword);
            serviceClient.modifier(currentClient);

            // Affichage d'une alerte de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Mot de passe réinitialisé");
            alert.setContentText("Votre mot de passe a été réinitialisé avec succès!");
            alert.showAndWait();

            // Retour à la page de connexion
            goToLogin(event);

        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la modification du mot de passe: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    /**
     * Vérifie si le mot de passe répond aux critères de complexité
     * @param password Le mot de passe à vérifier
     * @return true si le mot de passe est valide, false sinon
     */
    private boolean isValidPassword(String password) {
        // Au moins une lettre majuscule
        boolean hasUppercase = Pattern.compile("[A-Z]").matcher(password).find();

        // Au moins un chiffre
        boolean hasDigit = Pattern.compile("\\d").matcher(password).find();

        // Au moins un caractère spécial (ponctuation)
        boolean hasSpecialChar = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find();

        return hasUppercase && hasDigit && hasSpecialChar;
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

    @FXML
    void toggleNewPasswordVisibility(ActionEvent event) {
        newPasswordVisible = !newPasswordVisible;
        if (newPasswordVisible) {
            tf_new_password_visible.setText(tf_new_password.getText());
            tf_new_password_visible.setVisible(true);
            tf_new_password_visible.setManaged(true);
            tf_new_password.setVisible(false);
            tf_new_password.setManaged(false);
            btn_toggle_new_password.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/eye-off.png"))));
        } else {
            tf_new_password.setText(tf_new_password_visible.getText());
            tf_new_password.setVisible(true);
            tf_new_password.setManaged(true);
            tf_new_password_visible.setVisible(false);
            tf_new_password_visible.setManaged(false);
            btn_toggle_new_password.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/eye.png"))));
        }
    }

    @FXML
    void toggleConfirmPasswordVisibility(ActionEvent event) {
        confirmPasswordVisible = !confirmPasswordVisible;
        if (confirmPasswordVisible) {
            tf_confirm_password_visible.setText(tf_confirm_password.getText());
            tf_confirm_password_visible.setVisible(true);
            tf_confirm_password_visible.setManaged(true);
            tf_confirm_password.setVisible(false);
            tf_confirm_password.setManaged(false);
            btn_toggle_confirm_password.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/eye-off.png"))));
        } else {
            tf_confirm_password.setText(tf_confirm_password_visible.getText());
            tf_confirm_password.setVisible(true);
            tf_confirm_password.setManaged(true);
            tf_confirm_password_visible.setVisible(false);
            tf_confirm_password_visible.setManaged(false);
            btn_toggle_confirm_password.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/eye.png"))));
        }
    }
}