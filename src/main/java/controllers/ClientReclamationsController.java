package controllers;

import entities.Client;
import entities.Reclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.ServiceReclamation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientReclamationsController implements Initializable {
    private ServiceReclamation serviceReclamation = new ServiceReclamation();
    private Client currentClient;
    private Reclamation selectedReclamation;

    @FXML
    private ListView<Reclamation> reclamationsListView;

    @FXML
    private Button newReclamationButton;

    @FXML
    private Label statusLabel;

    public void setClient(Client client) {
        this.currentClient = client;
        loadReclamations();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reclamationsListView.setCellFactory(param -> new ReclamationListCell());

        reclamationsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedReclamation = newSelection;
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
    void newReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReclamationPopup.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("Cannot find ReclamationPopup.fxml");
            }
            VBox popupContent = loader.load();
            ReclamationPopupController popupController = loader.getController();
            popupController.setClient(currentClient);
            popupController.setParentController(this);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Nouvelle Réclamation");
            popupStage.setScene(new Scene(popupContent));
            popupStage.setResizable(false);
            popupStage.showAndWait();
        } catch (IOException e) {
            statusLabel.setText("Erreur lors de l'ouverture du popup: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReclamationPopup.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("Cannot find ReclamationPopup.fxml");
            }
            VBox popupContent = loader.load();
            ReclamationPopupController popupController = loader.getController();
            popupController.setClient(currentClient);
            popupController.setParentController(this);
            popupController.setReclamation(selectedReclamation);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Modifier Réclamation");
            popupStage.setScene(new Scene(popupContent));
            popupStage.setResizable(false);
            popupStage.showAndWait();
        } catch (IOException e) {
            statusLabel.setText("Erreur lors de l'ouverture du popup: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
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
            } else {
                statusLabel.setText("Suppression annulée");
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la suppression: " + e.getMessage());
            System.out.println(e.getMessage());
        }
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

    public void refreshReclamations() {
        loadReclamations();
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
        private HBox buttonBox;
        private Button updateButton;
        private Button deleteButton;
        private Button viewConversationButton;

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

            buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.setVisible(false);
            buttonBox.setManaged(false);
            buttonBox.getStyleClass().add("button-box");

            updateButton = new Button("Modifier");
            updateButton.getStyleClass().addAll("button", "button-primary");
            updateButton.setOnAction(e -> updateReclamation(null));

            deleteButton = new Button("Supprimer");
            deleteButton.getStyleClass().addAll("button", "button-danger");
            deleteButton.setOnAction(e -> deleteReclamation(null));

            viewConversationButton = new Button("Voir Conversation");
            viewConversationButton.getStyleClass().addAll("button", "button-secondary");
            viewConversationButton.setOnAction(e -> viewConversation(null));

            buttonBox.getChildren().addAll(updateButton, deleteButton, viewConversationButton);

            cellHeader.getChildren().addAll(titleLabel, spacer, dateLabel, statusLabel);
            cellMain.getChildren().addAll(cellHeader, descriptionLabel);
            cellContainer.getChildren().addAll(cellMain, buttonBox);

            setOnMouseEntered(event -> {
                if (!isEmpty()) {
                    cellContainer.getStyleClass().add("cell-hover");
                    buttonBox.setVisible(true);
                    buttonBox.setManaged(true);
                    getListView().getSelectionModel().select(getItem());
                }
            });

            setOnMouseExited(event -> {
                if (!isEmpty()) {
                    cellContainer.getStyleClass().remove("cell-hover");
                    buttonBox.setVisible(false);
                    buttonBox.setManaged(false);
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
                        updateButton.setDisable(false);
                        viewConversationButton.setDisable(false);
                        break;
                    case "traitée":
                        statusLabel.getStyleClass().add("status-resolved");
                        updateButton.setDisable(true);
                        viewConversationButton.setDisable(true);
                        break;
                    default:
                        statusLabel.getStyleClass().add("status-inprogress");
                        updateButton.setDisable(false);
                        viewConversationButton.setDisable(false);
                        break;
                }

                String desc = reclamation.getDescription();
                descriptionLabel.setText(desc != null && desc.length() > 100 ? desc.substring(0, 97) + "..." : (desc != null ? desc : "N/A"));

                setGraphic(cellContainer);
            }
        }
    }
}