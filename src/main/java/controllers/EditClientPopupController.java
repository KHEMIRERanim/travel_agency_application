package controllers;

import entities.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceClient;

import java.sql.SQLException;

public class EditClientPopupController {
    private ServiceClient serviceClient = new ServiceClient();
    private Client currentClient;

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
    private Button btn_save;

    @FXML
    private Button btn_cancel;

    @FXML
    private Label statusLabel;

    public void setClient(Client client) {
        this.currentClient = client;
        displayClientInfo();
    }

    private void displayClientInfo() {
        tf_nom.setText(currentClient.getNom());
        tf_prenom.setText(currentClient.getPrenom());
        tf_email.setText(currentClient.getEmail());
        tf_telephone.setText(String.valueOf(currentClient.getNumero_telephone()));
        tf_dateNaissance.setText(currentClient.getDate_de_naissance());
        tf_password.setText(currentClient.getMot_de_passe());
    }

    @FXML
    void saveClient(ActionEvent event) {
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

            // Show success message
            statusLabel.setText("Client mis à jour avec succès");

            // Close the window after a short delay
            Thread.sleep(1000);
            closeWindow(event);
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