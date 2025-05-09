package controllers;

import entities.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceClient;
import utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class AddClientPopupController {
    private ServiceClient serviceClient = new ServiceClient();
    private Runnable refreshCallback;

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
    private ImageView iv_profilePicture;

    private String profilePicturePath = "/images/default_profile.png";

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    void selectProfilePicture(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo de profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                profilePicturePath = ImageUtils.saveProfileImage(selectedFile);
                iv_profilePicture.setImage(new Image(selectedFile.toURI().toString()));
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de l'image", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void ajouterClient(ActionEvent event) {
        try {
            if (tf_nom.getText().isEmpty() || tf_prenom.getText().isEmpty() ||
                    tf_email.getText().isEmpty() || tf_numero.getText().isEmpty() ||
                    tf_datenaissance.getText().isEmpty() || tf_mdp.getText().isEmpty() ||
                    tf_mdp_confirm.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Champs vides", "Veuillez remplir tous les champs");
                return;
            }

            String password = tf_mdp.getText();
            String confirmPassword = tf_mdp_confirm.getText();

            if (!password.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Mots de passe différents",
                        "Les mots de passe ne correspondent pas");
                return;
            }

            if (!isValidPassword(password)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Mot de passe invalide",
                        "Le mot de passe doit contenir au moins:\n• Une lettre majuscule\n• Un chiffre\n• Un caractère spécial");
                return;
            }

            if (serviceClient.emailExists(tf_email.getText())) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Email déjà utilisé",
                        "Cet email est déjà utilisé par un autre compte");
                return;
            }

            if (tf_email.getText().equals("admin@gmail.com")) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Email non autorisé",
                        "L'email admin@gmail.com ne peut pas être utilisé pour l'inscription");
                return;
            }

            try {
                Integer.parseInt(tf_numero.getText());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Format invalide",
                        "Le numéro de téléphone doit être un nombre");
                return;
            }

            Client newClient = new Client(
                    tf_nom.getText(),
                    tf_prenom.getText(),
                    tf_email.getText(),
                    Integer.parseInt(tf_numero.getText()),
                    tf_datenaissance.getText(),
                    tf_mdp.getText(),
                    profilePicturePath  // Make sure this is set
            );

            serviceClient.ajouter(newClient);
            if (refreshCallback != null) {
                refreshCallback.run();
            }
            closeWindow();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de base de données", e.getMessage());
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Validation", e.getMessage());
        }
    }

    @FXML
    void annuler(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tf_nom.getScene().getWindow();
        stage.close();
    }

    private boolean isValidPassword(String password) {
        boolean hasUppercase = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("\\d").matcher(password).find();
        boolean hasSpecialChar = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find();
        return hasUppercase && hasDigit && hasSpecialChar;
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}