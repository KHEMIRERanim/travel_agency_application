package controllers;

import entities.Client;
import entities.Reclamation;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import services.ServiceClient;
import services.ServiceReclamation;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminReclamationsController implements Initializable {
    private ServiceReclamation serviceReclamation = new ServiceReclamation();
    private ServiceClient serviceClient = new ServiceClient();
    private Reclamation selectedReclamation;

    @FXML
    private ListView<Reclamation> reclamationsListView;

    @FXML
    private Label clientLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private ComboBox<String> etatComboBox;

    @FXML
    private Button updateButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup etat combo box with possible states
        ObservableList<String> etats = FXCollections.observableArrayList(
                "pas encore vu",
                "en cours",
                "résolue"
        );
        etatComboBox.setItems(etats);

        // Configure ListView with custom cell factory
        reclamationsListView.setCellFactory(param -> new ReclamationListCell());

        // Add list selection listener
        reclamationsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedReclamation = newSelection;
                displayReclamationDetails(selectedReclamation);
                statusLabel.setText("");
            }
        });

        // Load initial data
        loadReclamations();
    }

    private void displayReclamationDetails(Reclamation reclamation) {
        try {
            Client client = serviceClient.getById(reclamation.getClientId());
            if (client != null) {
                clientLabel.setText(client.getNom() + " " + client.getPrenom());
            } else {
                clientLabel.setText("Client #" + reclamation.getClientId());
            }
        } catch (SQLException e) {
            clientLabel.setText("Client #" + reclamation.getClientId());
            System.out.println("Error loading client: " + e.getMessage());
        }

        typeLabel.setText(reclamation.getType());
        dateLabel.setText(reclamation.getDateIncident());
        descriptionTextArea.setText(reclamation.getDescription());
        etatComboBox.setValue(reclamation.getEtat());
    }

    private void loadReclamations() {
        try {
            ObservableList<Reclamation> reclamations = FXCollections.observableArrayList(
                    serviceReclamation.recuperer()
            );
            reclamationsListView.setItems(reclamations);
            statusLabel.setText("Réclamations chargées avec succès");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors du chargement des réclamations: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void updateEtat(ActionEvent event) {
        try {
            if (selectedReclamation == null) {
                statusLabel.setText("Veuillez sélectionner une réclamation pour la mettre à jour");
                return;
            }

            String newEtat = etatComboBox.getValue();
            if (newEtat == null || newEtat.isEmpty()) {
                statusLabel.setText("Veuillez sélectionner un état");
                return;
            }

            // Update the state
            serviceReclamation.updateEtat(selectedReclamation.getId_reclamation(), newEtat);
            statusLabel.setText("État de la réclamation mis à jour avec succès");

            // Refresh the table to reflect changes
            refreshTable(event);

        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la mise à jour: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void refreshTable(ActionEvent event) {
        loadReclamations();
        // Clear selection
        selectedReclamation = null;
        clientLabel.setText("--");
        typeLabel.setText("--");
        dateLabel.setText("--");
        descriptionTextArea.clear();
        etatComboBox.setValue(null);
    }

    // Custom ListCell to display reclamations in a beautiful format
    private class ReclamationListCell extends ListCell<Reclamation> {
        private HBox cellContainer;
        private VBox cellMain;
        private HBox cellHeader;
        private Label titleLabel;
        private Label dateLabel;
        private Region spacer;
        private Label statusLabel;
        private Label descriptionLabel;
        private Label clientLabel;

        public ReclamationListCell() {
            super();

            // Create UI components
            cellContainer = new HBox();
            cellContainer.getStyleClass().add("cell-container");

            cellMain = new VBox();
            cellMain.getStyleClass().add("cell-main");
            HBox.setHgrow(cellMain, Priority.ALWAYS);

            cellHeader = new HBox();
            cellHeader.getStyleClass().add("cell-header");
            cellHeader.setAlignment(Pos.CENTER_LEFT);

            titleLabel = new Label();
            titleLabel.getStyleClass().add("cell-title");

            spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            dateLabel = new Label();
            dateLabel.getStyleClass().add("cell-date");

            statusLabel = new Label();
            statusLabel.getStyleClass().add("status-indicator");

            clientLabel = new Label();
            clientLabel.getStyleClass().add("cell-subtitle");

            descriptionLabel = new Label();
            descriptionLabel.getStyleClass().add("cell-description");
            descriptionLabel.setWrapText(true);
            descriptionLabel.setMaxWidth(600);

            // Add components to containers
            cellHeader.getChildren().addAll(titleLabel, spacer, dateLabel, statusLabel);
            cellMain.getChildren().addAll(cellHeader, clientLabel, descriptionLabel);
            cellContainer.getChildren().add(cellMain);
        }

        @Override
        protected void updateItem(Reclamation reclamation, boolean empty) {
            super.updateItem(reclamation, empty);

            if (empty || reclamation == null) {
                setGraphic(null);
            } else {
                // Set data to components
                titleLabel.setText(reclamation.getType());
                dateLabel.setText(reclamation.getDateIncident());

                // Set status label with appropriate style
                statusLabel.setText(reclamation.getEtat());
                statusLabel.getStyleClass().removeAll("status-pending", "status-inprogress", "status-resolved");

                switch (reclamation.getEtat()) {
                    case "pas encore vu":
                        statusLabel.getStyleClass().add("status-pending");
                        break;
                    case "en cours":
                        statusLabel.getStyleClass().add("status-inprogress");
                        break;
                    case "résolue":
                        statusLabel.getStyleClass().add("status-resolved");
                        break;
                }

                // Set client name
                try {
                    Client client = serviceClient.getById(reclamation.getClientId());
                    if (client != null) {
                        clientLabel.setText(client.getNom() + " " + client.getPrenom());
                    } else {
                        clientLabel.setText("Client #" + reclamation.getClientId());
                    }
                } catch (SQLException e) {
                    clientLabel.setText("Client #" + reclamation.getClientId());
                }

                // Truncate description if too long
                String desc = reclamation.getDescription();
                if (desc.length() > 100) {
                    descriptionLabel.setText(desc.substring(0, 97) + "...");
                } else {
                    descriptionLabel.setText(desc);
                }

                setGraphic(cellContainer);
            }
        }
    }
}