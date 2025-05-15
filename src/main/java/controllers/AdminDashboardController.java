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
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.ServiceClient;
import javafx.geometry.Insets;
import utils.ImageUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminDashboardController implements Initializable {
    private ServiceClient serviceClient = new ServiceClient();

    @FXML
    private ListView<Client> clientListView;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private TextField searchField;

    @FXML
    private Button clientsBtn;

    @FXML
    private Button reclamationsBtn;

    @FXML
    private Button volsBtn;

    @FXML
    private Button hotelsBtn;

    @FXML
    private Button transportBtn;

    @FXML
    private Button publicationsBtn;

    @FXML
    private Button logoutBtn;

    private ObservableList<Client> allClients;
    private Client currentLoggedInClient;

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
                    private final HBox container = new HBox(15);
                    private final VBox infoContainer = new VBox(5);
                    private final Text nameText = new Text();
                    private final HBox detailsBox = new HBox(15);
                    private final ImageView profileImageView = new ImageView();
                    private final Button editButton = new Button("Modifier");
                    private final Button deleteButton = new Button("Supprimer");
                    private Client currentClient;

                    // Add a VBox for the role
                    private final VBox roleBox = createDetailBox("Rôle", "");
                    // Add a VBox for the gender
                    private final VBox genderBox = createDetailBox("Genre", "");

                    {
                        // Initialize the layout
                        container.getStyleClass().add("list-cell-container");

                        // Configure image view
                        profileImageView.setFitHeight(50);
                        profileImageView.setFitWidth(50);
                        HBox.setMargin(profileImageView, new Insets(0, 15, 0, 0));

                        // Configure info container
                        infoContainer.getStyleClass().add("client-info-container");
                        HBox.setHgrow(infoContainer, Priority.ALWAYS);
                        nameText.getStyleClass().add("client-name");

                        // Create detail boxes
                        VBox emailBox = createDetailBox("Email", "");
                        VBox passwordBox = createDetailBox("Mot de passe", "");
                        VBox phoneBox = createDetailBox("Téléphone", "");
                        VBox dobBox = createDetailBox("Date de naissance", "");

                        // Add roleBox and genderBox to detailsBox
                        detailsBox.getChildren().addAll(emailBox, passwordBox, phoneBox, dobBox, roleBox, genderBox);
                        infoContainer.getChildren().addAll(nameText, detailsBox);

                        // Configure buttons
                        editButton.getStyleClass().addAll("button", "button-primary");
                        editButton.setPrefSize(100, 30);
                        editButton.setVisible(false);

                        deleteButton.getStyleClass().addAll("button", "button-danger");
                        deleteButton.setPrefSize(100, 30);
                        deleteButton.setVisible(false);

                        HBox buttonBox = new HBox(10, editButton, deleteButton);
                        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

                        container.getChildren().addAll(profileImageView, infoContainer, buttonBox);

                        // Hover behavior
                        setOnMouseEntered(e -> {
                            if (!isEmpty()) {
                                editButton.setVisible(true);
                                deleteButton.setVisible(true);
                            }
                        });
                        setOnMouseExited(e -> {
                            editButton.setVisible(false);
                            deleteButton.setVisible(false);
                        });
                    }

                    private VBox createDetailBox(String labelText, String valueText) {
                        VBox box = new VBox(2);
                        Text label = new Text(labelText);
                        label.getStyleClass().add("info-label");
                        Text value = new Text(valueText);
                        value.getStyleClass().add("info-value");
                        box.getChildren().addAll(label, value);
                        return box;
                    }

                    @Override
                    protected void updateItem(Client client, boolean empty) {
                        super.updateItem(client, empty);

                        if (empty || client == null) {
                            setText(null);
                            setGraphic(null);
                            currentClient = null;
                        } else {
                            currentClient = client;
                            nameText.setText(client.getNom() + " " + client.getPrenom());

                            // Update detail values
                            ((Text)((VBox)detailsBox.getChildren().get(0)).getChildren().get(1)).setText(client.getEmail());
                            ((Text)((VBox)detailsBox.getChildren().get(1)).getChildren().get(1)).setText("********");
                            ((Text)((VBox)detailsBox.getChildren().get(2)).getChildren().get(1)).setText(String.valueOf(client.getNumero_telephone()));
                            ((Text)((VBox)detailsBox.getChildren().get(3)).getChildren().get(1)).setText(client.getDate_de_naissance());
                            ((Text)((VBox)detailsBox.getChildren().get(4)).getChildren().get(1)).setText(client.getRole());
                            ((Text)((VBox)detailsBox.getChildren().get(5)).getChildren().get(1)).setText(client.getGender());

                            // Main admin logic
                            if (client.getEmail().equalsIgnoreCase("admin@gmail.com")) {
                                deleteButton.setDisable(true);
                                deleteButton.setVisible(false);
                                // Only allow edit if this is the logged-in admin
                                if (currentLoggedInClient != null && currentLoggedInClient.getEmail().equalsIgnoreCase("admin@gmail.com") &&
                                    client.getEmail().equalsIgnoreCase(currentLoggedInClient.getEmail())) {
                                    editButton.setDisable(false);
                                    editButton.setVisible(true);
                                } else {
                                    editButton.setDisable(true);
                                    editButton.setVisible(false);
                                }
                            } else {
                                deleteButton.setDisable(false);
                                deleteButton.setVisible(true);
                                editButton.setDisable(false);
                                editButton.setVisible(true);
                            }

                            // Load profile image
                            try {
                                profileImageView.setImage(ImageUtils.loadProfileImage(client.getProfilePicture()));
                            } catch (Exception e) {
                                System.err.println("Error loading profile image: " + e.getMessage());
                            }

                            // Set button actions
                            editButton.setOnAction(event -> editSelectedClient(event, currentClient));
                            deleteButton.setOnAction(event -> removeSelectedClient(event, currentClient));

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
            ObservableList<Client> filteredList = allClients.stream()
                    .filter(client -> client.getNom().toLowerCase().contains(lowerCaseFilter) ||
                            client.getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                            client.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                            String.valueOf(client.getNumero_telephone()).contains(lowerCaseFilter))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            clientListView.setItems(filteredList);
        }
    }

    @FXML
    void editSelectedClient(ActionEvent event, Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditClientPopup.fxml"));
            Parent root = loader.load();

            EditClientPopupController controller = loader.getController();
            controller.setClient(client);
            controller.setRefreshCallback(this::loadClients);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node)event.getSource()).getScene().getWindow());
            popupStage.setTitle("Modifier le client");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/admin-common.css").toExternalForm());
            popupStage.setScene(scene);
            popupStage.showAndWait();

            loadClients();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger le formulaire de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void removeSelectedClient(ActionEvent event, Client client) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le client");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + client.getNom() + " " + client.getPrenom() + "?");

        ButtonType yesButton = new ButtonType("Oui");
        ButtonType noButton = new ButtonType("Non");
        confirmation.getButtonTypes().setAll(yesButton, noButton);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                try {
                    serviceClient.supprimer(client);
                    loadClients();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddClientPopup.fxml"));
            Parent root = loader.load();

            AddClientPopupController controller = loader.getController();
            controller.setRefreshCallback(this::loadClients);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node)event.getSource()).getScene().getWindow());
            popupStage.setTitle("Ajouter un client");

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MenuPrincipale.fxml"));
            Parent volsView = loader.load();

            // If contentArea is available, use it
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(volsView);
            } else {
                // Otherwise open in a new window
                Stage stage = new Stage();
                stage.setScene(new Scene(volsView));
                stage.setTitle("Gestion des vols");
                stage.show();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger la gestion des vols: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    void showHotels(ActionEvent event) {
        showEmptyModuleAlert("Gestion des hôtels", "Le module de gestion des hôtels n'est pas encore disponible.");
    }

    @FXML
    void showTransport(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminVehicles.fxml"));
            Parent vehiclesView = loader.load();

            // If using contentArea (which is the case in the current UI)
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(vehiclesView);
            } else {
                // Fallback to opening in a new window
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(vehiclesView));
                stage.setTitle("Gestion des véhicules");
                stage.show();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger la gestion des véhicules: " + e.getMessage());
            e.printStackTrace();
        }
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

    public void setCurrentLoggedInClient(Client client) {
        this.currentLoggedInClient = client;
    }
}