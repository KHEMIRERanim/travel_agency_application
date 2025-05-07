package controllers;

import entities.Client;
import entities.Reclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Callback;
import services.ServiceReclamation;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientReclamationsController implements Initializable {
    private ServiceReclamation serviceReclamation = new ServiceReclamation();
    private Client currentClient;
    private Reclamation selectedReclamation;

    @FXML
    private ListView<Reclamation> reclamationsListView;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField dateIncidentField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Button submitButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button newReclamationButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label statusLabel;

    public void setClient(Client client) {
        this.currentClient = client;
        loadReclamations();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Make submit button initially disabled instead of hidden
        submitButton.setDisable(true);

        // Configure ListView cell factory
        reclamationsListView.setCellFactory(new Callback<ListView<Reclamation>, ListCell<Reclamation>>() {
            @Override
            public ListCell<Reclamation> call(ListView<Reclamation> param) {
                return new ListCell<Reclamation>() {
                    @Override
                    protected void updateItem(Reclamation reclamation, boolean empty) {
                        super.updateItem(reclamation, empty);

                        // Clear all previous style classes to prevent style inheritance
                        getStyleClass().removeAll("status-pending", "status-progress", "status-resolved", "reclamation-cell");

                        if (empty || reclamation == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            String status = reclamation.getEtat();

                            // Add reclamation-cell style to all cells
                            getStyleClass().add("reclamation-cell");

                            // Apply status-specific style class
                            if ("pas encore vu".equals(status)) {
                                getStyleClass().add("status-pending");
                            } else if ("en cours".equals(status)) {
                                getStyleClass().add("status-progress");
                            } else if ("traité".equals(status) || "résolue".equals(status)) {
                                getStyleClass().add("status-resolved");
                            }

                            setText(String.format("Type: %s | Date: %s | État: %s | %s",
                                    reclamation.getType(),
                                    reclamation.getDateIncident(),
                                    reclamation.getEtat(),
                                    reclamation.getDescription().length() > 50 ?
                                            reclamation.getDescription().substring(0, 47) + "..." :
                                            reclamation.getDescription()
                            ));
                        }
                    }
                };
            }
        });

        // Setup type combo box
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

        // Set today's date as default
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateIncidentField.setText(today.format(formatter));

        // Add ListView row selection listener
        reclamationsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedReclamation = newSelection;
                typeComboBox.setValue(selectedReclamation.getType());
                dateIncidentField.setText(selectedReclamation.getDateIncident());
                descriptionTextArea.setText(selectedReclamation.getDescription());

                // Enable update and delete buttons
                updateButton.setDisable(false);
                deleteButton.setDisable(false);

                // Disable submit button when a reclamation is selected
                submitButton.setDisable(true);

                // Disable editing if the state is not "pas encore vu"
                boolean isEditable = "pas encore vu".equals(selectedReclamation.getEtat());
                typeComboBox.setDisable(!isEditable);
                dateIncidentField.setDisable(!isEditable);
                descriptionTextArea.setDisable(!isEditable);
                updateButton.setDisable(!isEditable);

                // Delete button should be enabled for any state
                deleteButton.setDisable(false);

                if (!isEditable) {
                    statusLabel.setText("Cette réclamation est déjà en traitement et ne peut plus être modifiée.");
                } else {
                    statusLabel.setText("Vous pouvez modifier cette réclamation.");
                }
            }
        });
    }

    private void loadReclamations() {
        try {
            if (currentClient != null) {
                ObservableList<Reclamation> reclamations = FXCollections.observableArrayList(
                        serviceReclamation.getReclamationsByClientId(currentClient.getId_client())
                );
                reclamationsListView.setItems(reclamations);

                // If no reclamations are found, display a message
                if (reclamations.isEmpty()) {
                    statusLabel.setText("Vous n'avez pas encore de réclamations");
                } else {
                    statusLabel.setText("Réclamations chargées avec succès");
                }
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors du chargement des réclamations: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void submitReclamation(ActionEvent event) {
        try {
            // Validate inputs
            String type = typeComboBox.getValue();
            String dateIncident = dateIncidentField.getText();
            String description = descriptionTextArea.getText();

            if (type == null || dateIncident.isEmpty() || description.isEmpty()) {
                statusLabel.setText("Veuillez remplir tous les champs");
                return;
            }

            // Create and save reclamation
            Reclamation reclamation = new Reclamation(
                    currentClient.getId_client(),
                    type,
                    dateIncident,
                    description
            );

            serviceReclamation.ajouter(reclamation);
            statusLabel.setText("Réclamation soumise avec succès");

            // Refresh list and clear form
            loadReclamations();
            clearForm(event);

            // Disable submit button after submission
            submitButton.setDisable(true);

        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la soumission: " + e.getMessage());
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            statusLabel.setText("Erreur de validation: " + e.getMessage());
        }
    }

    @FXML
    void updateReclamation(ActionEvent event) {
        try {
            if (selectedReclamation == null) {
                statusLabel.setText("Veuillez sélectionner une réclamation à modifier");
                return;
            }

            // Check if state allows updating
            if (!"pas encore vu".equals(selectedReclamation.getEtat())) {
                statusLabel.setText("Cette réclamation est déjà en traitement et ne peut plus être modifiée.");
                return;
            }

            // Validate inputs
            String type = typeComboBox.getValue();
            String dateIncident = dateIncidentField.getText();
            String description = descriptionTextArea.getText();

            if (type == null || dateIncident.isEmpty() || description.isEmpty()) {
                statusLabel.setText("Veuillez remplir tous les champs");
                return;
            }

            // Update reclamation
            selectedReclamation.setType(type);
            selectedReclamation.setDateIncident(dateIncident);
            selectedReclamation.setDescription(description);

            serviceReclamation.modifier(selectedReclamation);
            statusLabel.setText("Réclamation mise à jour avec succès");

            // Refresh list and clear form
            loadReclamations();
            clearForm(event);

        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la mise à jour: " + e.getMessage());
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            statusLabel.setText("Erreur de validation: " + e.getMessage());
        }
    }

    @FXML
    void deleteReclamation(ActionEvent event) {
        try {
            if (selectedReclamation == null) {
                statusLabel.setText("Veuillez sélectionner une réclamation à supprimer");
                return;
            }

            // Ask for confirmation
            Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmation de suppression");
            confirmDialog.setHeaderText("Supprimer la réclamation");
            confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cette réclamation ?");

            Optional<ButtonType> result = confirmDialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Proceed with deletion
                serviceReclamation.supprimer(selectedReclamation);
                statusLabel.setText("Réclamation supprimée avec succès");

                // Refresh list and clear form
                loadReclamations();
                clearForm(event);
            } else {
                statusLabel.setText("Suppression annulée");
            }

        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la suppression: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void clearForm(ActionEvent event) {
        typeComboBox.setValue(null);

        // Reset to today's date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateIncidentField.setText(today.format(formatter));

        descriptionTextArea.setText("");
        selectedReclamation = null;
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        // Disable submit button by default
        submitButton.setDisable(true);

        // Re-enable all fields
        typeComboBox.setDisable(false);
        dateIncidentField.setDisable(false);
        descriptionTextArea.setDisable(false);

        statusLabel.setText("Prêt à saisir une nouvelle réclamation");

        // Clear list selection
        reclamationsListView.getSelectionModel().clearSelection();
    }

    @FXML
    void newReclamation(ActionEvent event) {
        clearForm(event);
        // Enable submit button when "Nouvelle" is clicked
        submitButton.setDisable(false);
    }
}