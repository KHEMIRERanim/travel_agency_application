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
import javafx.stage.Stage;
import services.ServiceClient;

import java.io.IOException;
import java.sql.SQLException;

public class UserProfileController {

    private ServiceClient serviceClient = new ServiceClient();
    private Client currentClient;
    private Flight selectedFlight;
    private int requiredPassengers;
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
        this.currentClient = client != null ? client : new Client();
        displayClientInfo();
        setFieldsEditable(false);
        btn_save.setDisable(true);
    }

    public void setFlight(Flight flight, int passengers) {
        this.selectedFlight = flight;
        this.requiredPassengers = passengers;
    }

    private void displayClientInfo() {
        tf_nom.setText(currentClient.getNom() != null ? currentClient.getNom() : "");
        tf_prenom.setText(currentClient.getPrenom() != null ? currentClient.getPrenom() : "");
        tf_email.setText(currentClient.getEmail() != null ? currentClient.getEmail() : "");
        tf_telephone.setText(currentClient.getNumero_telephone() != 0 ? String.valueOf(currentClient.getNumero_telephone()) : "");
        tf_dateNaissance.setText(currentClient.getDate_de_naissance() != null ? currentClient.getDate_de_naissance() : "");
        tf_password.setText(currentClient.getMot_de_passe() != null ? currentClient.getMot_de_passe() : "");
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
            // Validation des champs
            if (tf_nom.getText().isEmpty() || tf_prenom.getText().isEmpty() ||
                    tf_email.getText().isEmpty() || tf_telephone.getText().isEmpty() ||
                    tf_dateNaissance.getText().isEmpty() || tf_password.getText().isEmpty()) {
                statusLabel.setText("Veuillez remplir tous les champs");
                return;
            }

            // Valider le format du numéro de téléphone
            try {
                Integer.parseInt(tf_telephone.getText());
            } catch (NumberFormatException e) {
                statusLabel.setText("Le numéro de téléphone doit être un nombre");
                return;
            }

            // Mise à jour de l'objet client
            currentClient.setNom(tf_nom.getText());
            currentClient.setPrenom(tf_prenom.getText());
            currentClient.setEmail(tf_email.getText());
            currentClient.setNumero_telephone(Integer.parseInt(tf_telephone.getText()));
            currentClient.setDate_de_naissance(tf_dateNaissance.getText());
            currentClient.setMot_de_passe(tf_password.getText());

            // Mise à jour dans la base de données
            serviceClient.modifier(currentClient);

            // Si un vol est sélectionné, rediriger vers la page de confirmation
            if (selectedFlight != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FlightBookingConfirmation.fxml"));
                Parent root = loader.load();
                FlightBookingController controller = loader.getController();
                controller.setClient(currentClient);
                controller.setFlight(selectedFlight, requiredPassengers);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Confirmation de la réservation");
                stage.show();
            }

            // Mise à jour de l'interface utilisateur
            editMode = false;
            setFieldsEditable(false);
            btn_save.setDisable(true);
            btn_edit.setDisable(false);
            statusLabel.setText("Profil mis à jour avec succès");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la mise à jour: " + e.getMessage());
        } catch (IOException e) {
            statusLabel.setText("Erreur lors du chargement de la page: " + e.getMessage());
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
                    // Supprimer de la base de données
                    serviceClient.supprimer(currentClient);

                    // Retourner à la page de connexion
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Login");
                    stage.show();
                } catch (SQLException e) {
                    statusLabel.setText("Erreur lors de la suppression: " + e.getMessage());
                } catch (IOException e) {
                    statusLabel.setText("Erreur lors du chargement de la page de connexion: " + e.getMessage());
                }
            }
        });
    }
}