package controllers;

import entities.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AddClientPopupController implements Initializable {
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

    @FXML
    private ComboBox<String> cb_role;

    @FXML
    private ComboBox<String> cb_gender;

    private String profilePicturePath = "/images/default_profile.png";

    @FXML
    private TextField tf_mdp_visible;
    @FXML
    private Button btn_toggle_mdp;
    private boolean mdpVisible = false;
    @FXML
    private TextField tf_mdp_confirm_visible;
    @FXML
    private Button btn_toggle_mdp_confirm;
    private boolean mdpConfirmVisible = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cb_role.getItems().addAll("USER", "ADMIN");
        cb_role.setValue("USER"); // Default value
        cb_gender.getItems().addAll("Homme", "Femme", "Autre");
    }

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
                    tf_mdp_confirm.getText().isEmpty() || cb_role.getValue() == null) {
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
                    profilePicturePath,
                    cb_role.getValue(),
                    cb_gender.getValue()
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
}