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
import javafx.scene.control.ComboBox;
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

public class AjouterPersonneController {
    ServiceClient serviceClient = new ServiceClient();

    @FXML
    private TextField tf_datenaissance;

    @FXML
    private TextField tf_email;

    @FXML
    private PasswordField tf_mdp;

    @FXML
    private TextField tf_mdp_visible;

    @FXML
    private Button btn_toggle_mdp;

    @FXML
    private PasswordField tf_mdp_confirm;

    @FXML
    private TextField tf_mdp_confirm_visible;

    @FXML
    private Button btn_toggle_mdp_confirm;

    @FXML
    private TextField tf_nom;

    @FXML
    private TextField tf_numero;

    @FXML
    private TextField tf_prenom;

    @FXML
    private ImageView iv_profilePicture;

    @FXML
    private ComboBox<String> cb_gender;

    private String profilePicturePath = "/images/default_profile.png";

    private boolean mdpVisible = false;
    private boolean mdpConfirmVisible = false;

    @FXML
    void initialize() {
        cb_gender.getItems().addAll("Homme", "Femme", "Autre");
        cb_gender.setValue(null);
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
    void toggleMdpVisibility(ActionEvent event) {
        mdpVisible = !mdpVisible;
        if (mdpVisible) {
            tf_mdp_visible.setText(tf_mdp.getText());
            tf_mdp_visible.setVisible(true);
            tf_mdp_visible.setManaged(true);
            tf_mdp.setVisible(false);
            tf_mdp.setManaged(false);
            btn_toggle_mdp.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/eye-off.png"))));
        } else {
            tf_mdp.setText(tf_mdp_visible.getText());
            tf_mdp.setVisible(true);
            tf_mdp.setManaged(true);
            tf_mdp_visible.setVisible(false);
            tf_mdp_visible.setManaged(false);
            btn_toggle_mdp.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/eye.png"))));
        }
    }

    @FXML
    void toggleMdpConfirmVisibility(ActionEvent event) {
        mdpConfirmVisible = !mdpConfirmVisible;
        if (mdpConfirmVisible) {
            tf_mdp_confirm_visible.setText(tf_mdp_confirm.getText());
            tf_mdp_confirm_visible.setVisible(true);
            tf_mdp_confirm_visible.setManaged(true);
            tf_mdp_confirm.setVisible(false);
            tf_mdp_confirm.setManaged(false);
            btn_toggle_mdp_confirm.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/eye-off.png"))));
        } else {
            tf_mdp_confirm.setText(tf_mdp_confirm_visible.getText());
            tf_mdp_confirm.setVisible(true);
            tf_mdp_confirm.setManaged(true);
            tf_mdp_confirm_visible.setVisible(false);
            tf_mdp_confirm_visible.setManaged(false);
            btn_toggle_mdp_confirm.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/eye.png"))));
        }
    }

    @FXML
    void AjouterPersonne(ActionEvent event) {
        try {
            // Validation des champs vides
            if (tf_nom.getText().isEmpty() || tf_prenom.getText().isEmpty() ||
                    tf_email.getText().isEmpty() || tf_numero.getText().isEmpty() ||
                    tf_datenaissance.getText().isEmpty() || tf_mdp.getText().isEmpty() ||
                    tf_mdp_confirm.getText().isEmpty() || cb_gender.getValue() == null) {

                showAlert(Alert.AlertType.ERROR, "Erreur", "Champs vides", "Veuillez remplir tous les champs");
                return;
            }

            // Validation du mot de passe
            String password = mdpVisible ? tf_mdp_visible.getText() : tf_mdp.getText();
            String confirmPassword = mdpConfirmVisible ? tf_mdp_confirm_visible.getText() : tf_mdp_confirm.getText();

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

            if (tf_email.getText().equals("admin@gmail.com")) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Email non autorisé",
                        "L'email admin@gmail.com ne peut pas être utilisé pour l'inscription");
                return;
            }

            if (serviceClient.emailExists(tf_email.getText())) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Email déjà utilisé",
                        "Cet email est déjà utilisé par un autre compte");
                return;
            }

            // Création d'un nouveau client avec le chemin de l'image et le rôle par défaut "USER"
            Client newClient = new Client(
                    tf_nom.getText(),
                    tf_prenom.getText(),
                    tf_email.getText(),
                    Integer.parseInt(tf_numero.getText()),
                    tf_datenaissance.getText(),
                    password,
                    profilePicturePath,
                    "USER",
                    cb_gender.getValue()
            );

            serviceClient.ajouter(newClient);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Inscription réussie",
                    "Votre compte a été créé avec succès!");
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

    private boolean isValidPassword(String password) {
        boolean hasUppercase = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("\\d").matcher(password).find();
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