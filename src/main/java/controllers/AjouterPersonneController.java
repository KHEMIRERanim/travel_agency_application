package controllers;

import entities.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import services.ServiceClient;
import java.sql.SQLException;

public class AjouterPersonneController {
    private ServiceClient serviceClient; // Service instance

    @FXML
    private TextField tf_datenaissance;
    @FXML
    private TextField tf_email;
    @FXML
    private TextField tf_mdp;
    @FXML
    private TextField tf_nom;
    @FXML
    private TextField tf_numero;
    @FXML
    private TextField tf_prenom;

    // Constructor
    public AjouterPersonneController() {
        // Don't initialize serviceClient here - it will be injected
    }

    // Method to inject the service
    public void setServiceClient(ServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    @FXML
    void AjouterPersonne(ActionEvent event) {
        try {
            // Validate inputs first
            if (validateInputs()) {
                Client newClient = new Client(
                        tf_nom.getText(),
                        tf_prenom.getText(),
                        tf_email.getText(),
                        Integer.parseInt(tf_numero.getText()),
                        tf_datenaissance.getText(),
                        tf_mdp.getText()
                );

                serviceClient.ajouter(newClient);

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Client ajouté avec succès");
                clearFields();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le numéro de téléphone doit être un nombre valide");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'ajout: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (tf_nom.getText().isEmpty() || tf_prenom.getText().isEmpty() ||
                tf_email.getText().isEmpty() || tf_numero.getText().isEmpty() ||
                tf_datenaissance.getText().isEmpty() || tf_mdp.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        tf_nom.clear();
        tf_prenom.clear();
        tf_email.clear();
        tf_numero.clear();
        tf_datenaissance.clear();
        tf_mdp.clear();
    }
}