package controllers;

import entities.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import services.ServiceClient;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

public class AdminDashboardController implements Initializable {
    private ServiceClient serviceClient = new ServiceClient();

    @FXML
    private ListView<Client> clientListView;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private TextField searchField;

    private ObservableList<Client> allClients;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupClientListView();
        loadClients();

        // Add search functionality
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterClients(newValue);
            });
        }
    }

    private void setupClientListView() {
        clientListView.setCellFactory(new Callback<ListView<Client>, ListCell<Client>>() {
            @Override
            public ListCell<Client> call(ListView<Client> param) {
                return new ListCell<Client>() {
                    @Override
                    protected void updateItem(Client client, boolean empty) {
                        super.updateItem(client, empty);

                        if (empty || client == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            // Create the layout for each cell
                            HBox container = new HBox(15);
                            container.getStyleClass().add("list-cell-container");

                            VBox infoContainer = new VBox(5);
                            infoContainer.getStyleClass().add("client-info-container");
                            HBox.setHgrow(infoContainer, Priority.ALWAYS);

                            // Main client info
                            Text nameText = new Text(client.getNom() + " " + client.getPrenom());
                            nameText.getStyleClass().add("client-name");

                            HBox detailsBox = new HBox(15);

                            // Email
                            VBox emailBox = new VBox(2);
                            Text emailLabel = new Text("Email");
                            emailLabel.getStyleClass().add("info-label");
                            Text emailValue = new Text(client.getEmail());
                            emailValue.getStyleClass().add("info-value");
                            emailBox.getChildren().addAll(emailLabel, emailValue);

                            // Password
                            VBox passwordBox = new VBox(2);
                            Text passwordLabel = new Text("Mot de passe");
                            passwordLabel.getStyleClass().add("info-label");
                            Text passwordValue = new Text(client.getMot_de_passe());
                            passwordValue.getStyleClass().add("info-value");
                            passwordBox.getChildren().addAll(passwordLabel, passwordValue);

                            // Phone
                            VBox phoneBox = new VBox(2);
                            Text phoneLabel = new Text("Téléphone");
                            phoneLabel.getStyleClass().add("info-label");
                            Text phoneValue = new Text(String.valueOf(client.getNumero_telephone()));
                            phoneValue.getStyleClass().add("info-value");
                            phoneBox.getChildren().addAll(phoneLabel, phoneValue);

                            // Date of birth
                            VBox dobBox = new VBox(2);
                            Text dobLabel = new Text("Date de naissance");
                            dobLabel.getStyleClass().add("info-label");
                            Text dobValue = new Text(client.getDate_de_naissance());
                            dobValue.getStyleClass().add("info-value");
                            dobBox.getChildren().addAll(dobLabel, dobValue);

                            detailsBox.getChildren().addAll(emailBox, passwordBox, phoneBox, dobBox);

                            // Add all elements to containers
                            infoContainer.getChildren().addAll(nameText, detailsBox);
                            container.getChildren().add(infoContainer);

                            setGraphic(container);
                        }
                    }
                };
            }
        });
    }

    private void loadClients() {
        try {
            allClients = FXCollections.observableArrayList(serviceClient.recuperer());
            clientListView.setItems(allClients);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des clients", e.getMessage());
        }
    }

    private void filterClients(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            clientListView.setItems(allClients);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            ObservableList<Client> filteredList = allClients.stream().filter(client ->
                            client.getNom().toLowerCase().contains(lowerCaseFilter) ||
                                    client.getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                                    client.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                                    String.valueOf(client.getNumero_telephone()).contains(lowerCaseFilter))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            clientListView.setItems(filteredList);
        }
    }
    @FXML
    void removeSelectedClient(ActionEvent event) {
        Client selectedClient = clientListView.getSelectionModel().getSelectedItem();

        if (selectedClient == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Aucun client sélectionné",
                    "Veuillez sélectionner un client à supprimer.");
            return;
        }

        // Show confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le client");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + selectedClient.getNom() + " " + selectedClient.getPrenom() + "?");

        // Customize the buttons
        ButtonType yesButton = new ButtonType("Oui");
        ButtonType noButton = new ButtonType("Non");
        confirmation.getButtonTypes().setAll(yesButton, noButton);

        // Handle the response
        confirmation.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                try {
                    serviceClient.supprimer(selectedClient);
                    loadClients(); // Refresh the list
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Client supprimé",
                            "Le client a été supprimé avec succès.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de suppression",
                            "Une erreur est survenue lors de la suppression: " + e.getMessage());
                }
            }
        });
    }
    @FXML
    void refreshList(ActionEvent event) {
        loadClients();
    }

    @FXML
    void addNewClient(ActionEvent event) {
        try {
            // Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddClientPopup.fxml"));
            Parent root = loader.load();

            // Get the controller
            AddClientPopupController controller = loader.getController();

            // Set the refresh callback
            controller.setRefreshCallback(this::loadClients);

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node)event.getSource()).getScene().getWindow());
            popupStage.setTitle("Ajouter un client");

            // Apply stylesheets
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/admin-common.css").toExternalForm());

            popupStage.setScene(scene);
            popupStage.setResizable(false);
            popupStage.showAndWait();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger le formulaire d'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void showClients(ActionEvent event) {
        // Already showing clients, just refresh
        loadClients();

        // Make sure the clients view is visible
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboard.fxml"));
            Parent dashboardView = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.setTitle("Admin Dashboard - Gestion des Utilisateurs");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger la gestion des utilisateurs: " + e.getMessage());
        }
    }

    @FXML
    void showReclamations(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminReclamations.fxml"));
            Parent reclamationsView = loader.load();

            // If contentArea is available, use it
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(reclamationsView);
            } else {
                // Otherwise open in a new window
                Stage stage = new Stage();
                stage.setScene(new Scene(reclamationsView));
                stage.setTitle("Gestion des réclamations");
                stage.show();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger la gestion des réclamations: " + e.getMessage());
        }
    }

    @FXML
    void showVols(ActionEvent event) {
        showEmptyModuleAlert("Gestion des vols", "Le module de gestion des vols n'est pas encore disponible.");
    }

    @FXML
    void showHotels(ActionEvent event) {
        showEmptyModuleAlert("Gestion des hôtels", "Le module de gestion des hôtels n'est pas encore disponible.");
    }

    @FXML
    void showTransport(ActionEvent event) {
        showEmptyModuleAlert("Gestion du transport", "Le module de gestion du transport n'est pas encore disponible.");
    }

    @FXML
    void showPublications(ActionEvent event) {
        showEmptyModuleAlert("Gestion des publications", "Le module de gestion des publications et commentaires n'est pas encore disponible.");
    }

    private void showEmptyModuleAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}