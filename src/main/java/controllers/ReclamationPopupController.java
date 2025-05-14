package controllers;

import entities.Client;
import entities.Reclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import services.ServiceReclamation;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ReclamationPopupController implements Initializable {
    private ServiceReclamation serviceReclamation = new ServiceReclamation();
    private Client currentClient;
    private Reclamation reclamationToEdit;
    private ClientReclamationsController parentController;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField dateIncidentField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    public void setClient(Client client) {
        this.currentClient = client;
    }

    public void setParentController(ClientReclamationsController controller) {
        this.parentController = controller;
    }

    public void setReclamation(Reclamation reclamation) {
        this.reclamationToEdit = reclamation;
        typeComboBox.setValue(reclamation.getType());
        dateIncidentField.setText(reclamation.getDateIncident());
        descriptionTextArea.setText(reclamation.getDescription());
        submitButton.setText("Modifier");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> types = FXCollections.observableArrayList(
                "Réclamations liées aux réservations",
                "Réclamations concernant les vols",
                "Réclamations liées à l'hébergement",
                "Réclamations sur les prestations incluses",
                "Réclamations liées au service client",
                "Réclamations post-voyage",
                "Réclamations exceptionnelles"
        );
        typeComboBox.setItems(types);

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateIncidentField.setText(today.format(formatter));

        cancelButton.setOnAction(e -> cancelButton.getScene().getWindow().hide());
    }

    @FXML
    void submitReclamation(ActionEvent event) {
        try {
            String type = typeComboBox.getValue();
            String dateIncident = dateIncidentField.getText();
            String description = descriptionTextArea.getText();

            if (type == null || dateIncident.isEmpty() || description.isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs");
                return;
            }

            if (!Pattern.matches("^\\d{2}/\\d{2}/\\d{4}$", dateIncident)) {
                showAlert("Erreur", "La date doit être au format jj/mm/aaaa");
                return;
            }

            if (reclamationToEdit == null) {
                // New reclamation
                Reclamation reclamation = new Reclamation(
                        currentClient.getId_client(),
                        type,
                        dateIncident,
                        description
                );
                serviceReclamation.ajouter(reclamation);
            } else {
                // Update reclamation
                reclamationToEdit.setType(type);
                reclamationToEdit.setDateIncident(dateIncident);
                reclamationToEdit.setDescription(description);
                serviceReclamation.modifier(reclamationToEdit);
            }

            parentController.refreshReclamations();
            submitButton.getScene().getWindow().hide();

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la soumission: " + e.getMessage());
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert("Erreur", "Erreur de validation: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}