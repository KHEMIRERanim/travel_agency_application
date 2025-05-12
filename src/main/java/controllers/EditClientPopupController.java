package controllers;

import entities.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
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

public class EditClientPopupController {
    private ServiceClient serviceClient = new ServiceClient();
    private Client currentClient;
    private Runnable refreshCallback;

    @FXML
    private TextField tf_nom;

    @FXML
    private TextField tf_prenom;

    @FXML
    private TextField tf_email;

    @FXML
    private TextField tf_telephone;

    @FXML
    private TextField tf_dateNaissance;

    @FXML
    private PasswordField tf_password;

    @FXML
    private TextField tf_password_visible;

    @FXML
    private Button btn_save;

    @FXML
    private Button btn_cancel;

    @FXML
    private Button btn_toggle_password;

    @FXML
    private Label statusLabel;

    @FXML
    private ImageView iv_profilePicture;

    @FXML
    private ComboBox<String> cb_gender;

    private boolean passwordVisible = false;

    public void setClient(Client client) {
        this.currentClient = client;
        displayClientInfo();
        loadProfilePicture();
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    private void displayClientInfo() {
        tf_nom.setText(currentClient.getNom());
        tf_prenom.setText(currentClient.getPrenom());
        tf_email.setText(currentClient.getEmail());
        tf_telephone.setText(String.valueOf(currentClient.getNumero_telephone()));
        tf_dateNaissance.setText(currentClient.getDate_de_naissance());
        tf_password.setText(currentClient.getMot_de_passe());
        if (cb_gender != null) {
            cb_gender.getItems().setAll("Homme", "Femme", "Autre");
            cb_gender.setValue(currentClient.getGender());
        }
    }

    private void loadProfilePicture() {
        String imagePath = currentClient.getProfilePicture();
        iv_profilePicture.setImage(ImageUtils.loadProfileImage(imagePath));
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
                String newProfilePicturePath = ImageUtils.saveProfileImage(selectedFile);
                currentClient.setProfilePicture(newProfilePicturePath);
                iv_profilePicture.setImage(new Image(selectedFile.toURI().toString()));
                statusLabel.setText("Photo de profil sélectionnée. Enregistrez pour confirmer.");
            } catch (IOException e) {
                statusLabel.setText("Erreur lors du chargement de l'image: " + e.getMessage());
                e.printStackTrace();
            }
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

    @FXML
    void saveClient(ActionEvent event) {
        try {
            if (tf_nom.getText().isEmpty() || tf_prenom.getText().isEmpty() ||
                    tf_email.getText().isEmpty() || tf_telephone.getText().isEmpty() ||
                    tf_dateNaissance.getText().isEmpty() || tf_password_visible.getText().isEmpty() ||
                    cb_gender.getValue() == null) {
                statusLabel.setText("Veuillez remplir tous les champs");
                return;
            }
            currentClient.setNom(tf_nom.getText());
            currentClient.setPrenom(tf_prenom.getText());
            currentClient.setEmail(tf_email.getText());
            try {
                currentClient.setNumero_telephone(Integer.parseInt(tf_telephone.getText()));
            } catch (NumberFormatException e) {
                statusLabel.setText("Le numéro de téléphone doit être un nombre");
                return;
            }
            currentClient.setDate_de_naissance(tf_dateNaissance.getText());
            String password = passwordVisible ? tf_password_visible.getText() : tf_password.getText();
            currentClient.setMot_de_passe(password);
            currentClient.setGender(cb_gender.getValue());
            serviceClient.modifier(currentClient);
            statusLabel.setText("Client mis à jour avec succès");
            Thread.sleep(1000);
            closeWindow(event);
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la mise à jour: " + e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            statusLabel.setText("Erreur: " + e.getMessage());
        }
    }

    @FXML
    void cancelEdit(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}