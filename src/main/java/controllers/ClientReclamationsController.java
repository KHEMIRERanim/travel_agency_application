package controllers;

import entities.Client;
import entities.Reclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import services.ServiceReclamation;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

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
    private Button viewConversationButton;

    @FXML
    private Label statusLabel;

    public void setClient(Client client) {
        this.currentClient = client;
        loadReclamations();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        submitButton.setDisable(true);

        reclamationsListView.setCellFactory(param -> new ReclamationListCell());

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

        reclamationsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedReclamation = newSelection;
                typeComboBox.setValue(selectedReclamation.getType());
                dateIncidentField.setText(selectedReclamation.getDateIncident());
                descriptionTextArea.setText(selectedReclamation.getDescription());

                updateButton.setDisable(false);
                deleteButton.setDisable(false);
                submitButton.setDisable(true);

                boolean isEditable = "en cours".equals(selectedReclamation.getEtat());
                typeComboBox.setDisable(!isEditable);
                dateIncidentField.setDisable(!isEditable);
                descriptionTextArea.setDisable(!isEditable);
                updateButton.setDisable(!isEditable);
                viewConversationButton.setDisable(!isEditable);

                statusLabel.setText(isEditable ? "Vous pouvez modifier cette réclamation." : "Cette réclamation est traitée et ne peut plus être modifiée.");
            }
        });

        loadReclamations();
    }

    private void loadReclamations() {
        try {
            if (currentClient != null) {
                ObservableList<Reclamation> reclamations = FXCollections.observableArrayList(
                        serviceReclamation.getReclamationsByClientId(currentClient.getId_client())
                );
                reclamationsListView.setItems(reclamations);
                statusLabel.setText(reclamations.isEmpty() ? "Vous n'avez pas encore de réclamations" : "Réclamations chargées avec succès");
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors du chargement des réclamations: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void submitReclamation(ActionEvent event) {
        try {
            String type = typeComboBox.getValue();
            String dateIncident = dateIncidentField.getText();
            String description = descriptionTextArea.getText();

            if (type == null || dateIncident.isEmpty() || description.isEmpty()) {
                statusLabel.setText("Veuillez remplir tous les champs");
                return;
            }

            if (!Pattern.matches("^\\d{2}/\\d{2}/\\d{4}$", dateIncident)) {
                statusLabel.setText("La date doit être au format jj/mm/aaaa");
                return;
            }

            Reclamation reclamation = new Reclamation(
                    currentClient.getId_client(),
                    type,
                    dateIncident,
                    description
            );

            serviceReclamation.ajouter(reclamation);
            statusLabel.setText("Réclamation soumise avec succès");

            loadReclamations();
            clearForm(event);
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

            if (!"en cours".equals(selectedReclamation.getEtat())) {
                statusLabel.setText("Cette réclamation est traitée et ne peut plus être modifiée.");
                return;
            }

            String type = typeComboBox.getValue();
            String dateIncident = dateIncidentField.getText();
            String description = descriptionTextArea.getText();

            if (type == null || dateIncident.isEmpty() || description.isEmpty()) {
                statusLabel.setText("Veuillez remplir tous les champs");
                return;
            }

            if (!Pattern.matches("^\\d{2}/\\d{2}/\\d{4}$", dateIncident)) {
                statusLabel.setText("La date doit être au format jj/mm/aaaa");
                return;
            }

            selectedReclamation.setType(type);
            selectedReclamation.setDateIncident(dateIncident);
            selectedReclamation.setDescription(description);

            serviceReclamation.modifier(selectedReclamation);
            statusLabel.setText("Réclamation mise à jour avec succès");

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

            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmation de suppression");
            confirmDialog.setHeaderText("Supprimer la réclamation");
            confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cette réclamation ?");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                serviceReclamation.supprimer(selectedReclamation);
                statusLabel.setText("Réclamation supprimée avec succès");
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
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateIncidentField.setText(today.format(formatter));
        descriptionTextArea.setText("");
        selectedReclamation = null;
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        submitButton.setDisable(true);
        viewConversationButton.setDisable(true);
        typeComboBox.setDisable(false);
        dateIncidentField.setDisable(false);
        descriptionTextArea.setDisable(false);
        statusLabel.setText("Prêt à saisir une nouvelle réclamation");
        reclamationsListView.getSelectionModel().clearSelection();
    }

    @FXML
    void newReclamation(ActionEvent event) {
        clearForm(event);
        submitButton.setDisable(false);
    }

    @FXML
    void viewConversation(ActionEvent event) {
        if (selectedReclamation == null) {
            statusLabel.setText("Veuillez sélectionner une réclamation");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Conversation - Réclamation #" + selectedReclamation.getId_reclamation());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        VBox chatContainer = new VBox(10);
        chatContainer.setStyle("-fx-padding: 10; -fx-spacing: 5;");
        loadChatMessages(chatContainer);
        scrollPane.setContent(chatContainer);

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Tapez votre message...");
        messageArea.setPrefHeight(60);
        messageArea.setWrapText(true);
        Button sendButton = new Button("Envoyer");
        sendButton.setStyle("-fx-background-color: #1e40af; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 16; -fx-cursor: hand;");
        sendButton.setOnAction(e -> {
            try {
                String message = messageArea.getText().trim();
                if (!message.isEmpty()) {
                    serviceReclamation.addMessage(selectedReclamation.getId_reclamation(), "CLIENT", message);
                    messageArea.clear();
                    chatContainer.getChildren().clear();
                    loadChatMessages(chatContainer);
                    statusLabel.setText("Message envoyé avec succès");
                }
            } catch (SQLException ex) {
                statusLabel.setText("Erreur lors de l'envoi: " + ex.getMessage());
                System.out.println(ex.getMessage());
            }
        });

        inputBox.getChildren().addAll(messageArea, sendButton);
        content.getChildren().addAll(scrollPane, inputBox);

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private void loadChatMessages(VBox chatContainer) {
        chatContainer.getChildren().clear();
        if (selectedReclamation != null) {
            try {
                List<ServiceReclamation.Message> messages = serviceReclamation.getMessagesByReclamationId(selectedReclamation.getId_reclamation());
                for (ServiceReclamation.Message message : messages) {
                    Label messageLabel = new Label(message.getSender_type() + ": " + message.getMessage_text());
                    messageLabel.setWrapText(true);
                    messageLabel.setMaxWidth(500);
                    messageLabel.setStyle("-fx-padding: 10; -fx-background-radius: 10;");
                    if (message.getSender_type().equals("CLIENT")) {
                        messageLabel.setStyle(messageLabel.getStyle() + " -fx-background-color: #dbeafe; -fx-text-fill: #1e40af;");
                    } else {
                        messageLabel.setStyle(messageLabel.getStyle() + " -fx-background-color: #d1fae5; -fx-text-fill: #065f46; -fx-alignment: center-right;");
                    }
                    chatContainer.getChildren().add(messageLabel);
                }
            } catch (SQLException e) {
                statusLabel.setText("Erreur lors du chargement des messages: " + e.getMessage());
                System.out.println(e.getMessage());
            }
        }
    }

    private class ReclamationListCell extends ListCell<Reclamation> {
        private HBox cellContainer;
        private VBox cellMain;
        private HBox cellHeader;
        private Label titleLabel;
        private Label dateLabel;
        private Region spacer;
        private Label statusLabel;
        private Label descriptionLabel;

        public ReclamationListCell() {
            super();

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

            descriptionLabel = new Label();
            descriptionLabel.getStyleClass().add("cell-description");
            descriptionLabel.setWrapText(true);
            descriptionLabel.setMaxWidth(500);

            cellHeader.getChildren().addAll(titleLabel, spacer, dateLabel, statusLabel);
            cellMain.getChildren().addAll(cellHeader, descriptionLabel);
            cellContainer.getChildren().add(cellMain);

            setOnMouseEntered(event -> {
                if (!isEmpty()) {
                    cellContainer.getStyleClass().add("cell-hover");
                }
            });

            setOnMouseExited(event -> {
                if (!isEmpty()) {
                    cellContainer.getStyleClass().remove("cell-hover");
                }
            });
        }

        @Override
        protected void updateItem(Reclamation reclamation, boolean empty) {
            super.updateItem(reclamation, empty);

            if (empty || reclamation == null) {
                setGraphic(null);
                setText(null);
            } else {
                titleLabel.setText(reclamation.getType() != null ? reclamation.getType() : "N/A");
                dateLabel.setText(reclamation.getDateIncident() != null ? reclamation.getDateIncident() : "N/A");

                String etat = reclamation.getEtat() != null ? reclamation.getEtat() : "N/A";
                statusLabel.setText(etat);
                statusLabel.getStyleClass().removeAll("status-inprogress", "status-resolved");
                switch (etat.toLowerCase()) {
                    case "en cours":
                        statusLabel.getStyleClass().add("status-inprogress");
                        break;
                    case "traitée":
                        statusLabel.getStyleClass().add("status-resolved");
                        break;
                    default:
                        statusLabel.getStyleClass().add("status-inprogress");
                        break;
                }

                String desc = reclamation.getDescription();
                descriptionLabel.setText(desc != null && desc.length() > 100 ? desc.substring(0, 97) + "..." : (desc != null ? desc : "N/A"));

                setGraphic(cellContainer);
            }
        }
    }
}