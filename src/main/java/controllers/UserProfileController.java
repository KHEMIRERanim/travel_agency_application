package controllers;

import entities.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceClient;

import java.io.IOException;
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

    public void setClient(Client client) {
        this.currentClient = client;
        displayClientInfo();
        setFieldsEditable(false);
        btn_save.setDisable(true);
    }

    private void displayClientInfo() {
        tf_nom.setText(currentClient.getNom());
        tf_prenom.setText(currentClient.getPrenom());
        tf_email.setText(currentClient.getEmail());
        tf_telephone.setText(String.valueOf(currentClient.getNumero_telephone()));
        tf_dateNaissance.setText(currentClient.getDate_de_naissance());
        tf_password.setText(currentClient.getMot_de_passe());
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
            // Validate inputs
            if (tf_nom.getText().isEmpty() || tf_prenom.getText().isEmpty() ||
                    tf_email.getText().isEmpty() || tf_telephone.getText().isEmpty() ||
                    tf_dateNaissance.getText().isEmpty() || tf_password.getText().isEmpty()) {
                statusLabel.setText("Veuillez remplir tous les champs");
                return;
            }

            // Update client object
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

            // Update in database
            serviceClient.modifier(currentClient);

            // Update UI
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
                    // Delete from database
                    serviceClient.supprimer(currentClient);

                    // Return to login page
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