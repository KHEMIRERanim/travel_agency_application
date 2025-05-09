package controllers;

import entities.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

public class UserProfileController {
    private ServiceClient serviceClient = new ServiceClient();
    private Client currentClient;
    private boolean editMode = false;

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
    private Button btn_edit;

    @FXML
    private Button btn_save;

    @FXML
    private Button btn_delete;

    @FXML
    private Label statusLabel;

    @FXML
    private ImageView iv_profilePicture;

    public void setClient(Client client) {
        this.currentClient = client;
        System.out.println("Setting client in UserProfileController: " + (client != null ? client.getEmail() : "null"));
        displayClientInfo();
        setFieldsEditable(false);
        btn_save.setDisable(true);
        updateProfilePicture();
    }

    private void displayClientInfo() {
        if (currentClient == null) {
            System.out.println("Current client is null in displayClientInfo");
            return;
        }
        tf_nom.setText(currentClient.getNom());
        tf_prenom.setText(currentClient.getPrenom());
        tf_email.setText(currentClient.getEmail());
        tf_telephone.setText(String.valueOf(currentClient.getNumero_telephone()));
        tf_dateNaissance.setText(currentClient.getDate_de_naissance());
        tf_password.setText(currentClient.getMot_de_passe());
    }

    private void updateProfilePicture() {
        if (currentClient == null) return;
        iv_profilePicture.setImage(ImageUtils.loadProfileImage(currentClient.getProfilePicture()));
    }
    private void setFieldsEditable(boolean editable) {
        tf_nom.setEditable(editable);
        tf_prenom.setEditable(editable);
        tf_email.setEditable(editable);
        tf_telephone.setEditable(editable);
        tf_dateNaissance.setEditable(editable);
        tf_password.setEditable(editable);
    }

    @FXML
    void selectProfilePicture(ActionEvent event) {
        if (!editMode) {
            statusLabel.setText("Veuillez activer le mode édition pour changer la photo.");
            return;
        }

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
                serviceClient.modifier(currentClient);
                updateProfilePicture();
                statusLabel.setText("Photo de profil mise à jour avec succès.");
            } catch (IOException | SQLException e) {
                statusLabel.setText("Erreur lors du chargement de l'image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void editProfile(ActionEvent event) {
        editMode = true;
        setFieldsEditable(true);
        btn_save.setDisable(false);
        btn_edit.setDisable(true);
        statusLabel.setText("Mode édition activé. Modifiez vos informations et cliquez sur Enregistrer.");
    }

    @FXML
    void saveProfile(ActionEvent event) {
        try {
            if (tf_nom.getText().isEmpty() || tf_prenom.getText().isEmpty() ||
                    tf_email.getText().isEmpty() || tf_telephone.getText().isEmpty() ||
                    tf_dateNaissance.getText().isEmpty() || tf_password.getText().isEmpty()) {
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
            currentClient.setMot_de_passe(tf_password.getText());

            serviceClient.modifier(currentClient);

            editMode = false;
            setFieldsEditable(false);
            btn_save.setDisable(true);
            btn_edit.setDisable(false);
            statusLabel.setText("Profil mis à jour avec succès");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la mise à jour: " + e.getMessage());
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            statusLabel.setText("Erreur de validation: " + e.getMessage());
        }
    }

    @FXML
    void deleteProfile(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Supprimer le compte");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer votre compte?");
        alert.setContentText("Cette action est irréversible.");

        ButtonType buttonTypeYes = new ButtonType("Oui");
        ButtonType buttonTypeNo = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        alert.showAndWait().ifPresent(type -> {
            if (type == buttonTypeYes) {
                try {
                    serviceClient.supprimer(currentClient);

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
                } catch (SQLException e) {
                    statusLabel.setText("Erreur lors de la suppression: " + e.getMessage());
                    System.out.println(e.getMessage());
                }
            }
        });
    }
}