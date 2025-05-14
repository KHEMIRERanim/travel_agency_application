package controllers;

import entities.Client;
import entities.Flight;
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
    private Flight selectedFlight;
    private int requiredPassengers;
    private boolean editMode = false;
    private boolean passwordVisible = false;

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
    private Button btn_edit;

    @FXML
    private Button btn_save;

    @FXML
    private Button btn_delete;

    @FXML
    private Button btn_toggle_password;

    @FXML
    private Label statusLabel;

    @FXML
    private ImageView iv_profilePicture;

    @FXML
    private ComboBox<String> cb_gender;

    public void setClient(Client client) {
        this.currentClient = client;
        System.out.println("Setting client in UserProfileController: " + (client != null ? client.getEmail() : "null"));
        displayClientInfo();
        setFieldsEditable(false);
        btn_save.setDisable(true);
        updateProfilePicture();
    }

    public void setFlight(Flight flight, int passengers) {
        this.selectedFlight = flight;
        this.requiredPassengers = passengers;
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
        if (cb_gender != null) {
            cb_gender.getItems().setAll("Homme", "Femme", "Autre");
            cb_gender.setValue(currentClient.getGender());
        }
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
        if (editable && editMode) {
            tf_password.setEditable(true);
            tf_password_visible.setEditable(true);
        } else {
            tf_password.setEditable(false);
            tf_password_visible.setEditable(false);
        }
        if (cb_gender != null) cb_gender.setDisable(!editable);
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
                    tf_dateNaissance.getText().isEmpty() || tf_password_visible.getText().isEmpty() ||
                    cb_gender.getValue() == null) {
                statusLabel.setText("Veuillez remplir tous les champs");
                return;
            }
            try {
                Integer.parseInt(tf_telephone.getText());
            } catch (NumberFormatException e) {
                statusLabel.setText("Le numéro de téléphone doit être un nombre");
                return;
            }

            currentClient.setNom(tf_nom.getText());
            currentClient.setPrenom(tf_prenom.getText());
            currentClient.setEmail(tf_email.getText());
            currentClient.setNumero_telephone(Integer.parseInt(tf_telephone.getText()));
            currentClient.setDate_de_naissance(tf_dateNaissance.getText());
            String password = passwordVisible ? tf_password_visible.getText() : tf_password.getText();
            currentClient.setMot_de_passe(password);
            currentClient.setGender(cb_gender.getValue());
            serviceClient.modifier(currentClient);

            editMode = false;
            setFieldsEditable(false);
            btn_save.setDisable(true);
            btn_edit.setDisable(false);
            statusLabel.setText("Profil mis à jour avec succès");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la mise à jour: " + e.getMessage());
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
                }
            }
        });
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
}