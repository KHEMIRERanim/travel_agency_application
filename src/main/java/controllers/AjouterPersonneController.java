package controllers;

import entities.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceClient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class AjouterPersonneController {
    ServiceClient serviceClient = new ServiceClient();

    @FXML
    private TextField tf_datenaissance;

    @FXML
    private TextField tf_email;

    @FXML
    private PasswordField tf_mdp;

    @FXML
    private PasswordField tf_mdp_confirm;

    @FXML
    private TextField tf_nom;

    @FXML
    private TextField tf_numero;

    @FXML
    private TextField tf_prenom;

    @FXML
    void AjouterPersonne(ActionEvent event) {
        try {
            // Validation des champs vides
            if (tf_nom.getText().isEmpty() || tf_prenom.getText().isEmpty() ||
                    tf_email.getText().isEmpty() || tf_numero.getText().isEmpty() ||
                    tf_datenaissance.getText().isEmpty() || tf_mdp.getText().isEmpty() ||
                    tf_mdp_confirm.getText().isEmpty()) {

                showAlert(Alert.AlertType.ERROR, "Erreur", "Champs vides", "Veuillez remplir tous les champs");
                return;
            }

            // Validation du mot de passe
            String password = tf_mdp.getText();
            String confirmPassword = tf_mdp_confirm.getText();

            // Vérification que les mots de passe correspondent
            if (!password.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Mots de passe différents",
                        "Les mots de passe ne correspondent pas");
                return;
            }

            // Vérification des critères de complexité du mot de passe
            if (!isValidPassword(password)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Mot de passe invalide",
                        "Le mot de passe doit contenir au moins:\n• Une lettre majuscule\n• Un chiffre\n• Un caractère spécial");
                return;
            }
            //email admin cant be used from user
            if (tf_email.getText().equals("admin@gmail.com")) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Email non autorisé",
                        "L'email admin@gmail.com ne peut pas être utilisé pour l'inscription");
                return;
            }
            // Vérification si l'email existe déjà
            if (serviceClient.emailExists(tf_email.getText())) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Email déjà utilisé",
                        "Cet email est déjà utilisé par un autre compte");
                return;
            }

            // Création d'un nouveau client
            serviceClient.ajouter(new Client(
                    tf_nom.getText(),
                    tf_prenom.getText(),
                    tf_email.getText(),
                    Integer.parseInt(tf_numero.getText()),
                    tf_datenaissance.getText(),
                    tf_mdp.getText()
            ));

            // Affichage d'un message de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Inscription réussie",
                    "Votre compte a été créé avec succès!");

            // Retour à la page de connexion
            goToLogin(event);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de base de données", e.getMessage());
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format invalide",
                    "Le numéro de téléphone doit être un nombre");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Validation", e.getMessage());
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

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}