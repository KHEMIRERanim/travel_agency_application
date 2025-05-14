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
import services.ServiceClient;
import services.ServiceReclamation;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
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
    private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reclamationsListView.setCellFactory(param -> new ReclamationListCell());

        reclamationsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedReclamation = newSelection;
                displayReclamationDetails(selectedReclamation);
                statusLabel.setText("");
            }
        });

        loadReclamations();
    }

    private void displayReclamationDetails(Reclamation reclamation) {
        try {
            Client client = serviceClient.getById(reclamation.getClientId());
            clientLabel.setText(client != null ? client.getNom() + " " + client.getPrenom() : "Client #" + reclamation.getClientId());
        } catch (SQLException e) {
            clientLabel.setText("Client #" + reclamation.getClientId());
            System.out.println("Error loading client: " + e.getMessage());
        }

        typeLabel.setText(reclamation.getType());
        dateLabel.setText(reclamation.getDateIncident());
        descriptionTextArea.setText(reclamation.getDescription());
    }

    private void loadReclamations() {
        try {
            ObservableList<Reclamation> reclamations = FXCollections.observableArrayList(serviceReclamation.recuperer());
            reclamationsListView.setItems(reclamations);
            statusLabel.setText("Réclamations chargées avec succès");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors du chargement des réclamations: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    void refreshTable() {
        loadReclamations();
        selectedReclamation = null;
        clientLabel.setText("--");
        typeLabel.setText("--");
        dateLabel.setText("--");
        descriptionTextArea.clear();
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
        private Label clientLabel;
        private Button traiterButton;

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

            clientLabel = new Label();
            clientLabel.getStyleClass().add("cell-subtitle");

            descriptionLabel = new Label();
            descriptionLabel.getStyleClass().add("cell-description");
            descriptionLabel.setWrapText(true);
            descriptionLabel.setMaxWidth(500);

            traiterButton = new Button("Traiter");
            traiterButton.getStyleClass().addAll("button", "button-primary");
            traiterButton.setVisible(false);
            traiterButton.setOnAction(event -> showTraiterPopup(getItem()));

            cellHeader.getChildren().addAll(titleLabel, spacer, dateLabel, statusLabel);
            cellMain.getChildren().addAll(cellHeader, clientLabel, descriptionLabel);
            cellContainer.getChildren().addAll(cellMain, traiterButton);

            setOnMouseEntered(event -> {
                if (getItem() != null) {
                    traiterButton.setVisible(true);
                }
            });

            setOnMouseExited(event -> traiterButton.setVisible(false));
        }

        private void showTraiterPopup(Reclamation reclamation) {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Traiter la Réclamation - #" + reclamation.getId_reclamation());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            content.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

            // State selection
            HBox stateBox = new HBox(10);
            stateBox.setAlignment(Pos.CENTER_LEFT);
            Label stateLabel = new Label("État de la réclamation:");
            stateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #374151;");
            ComboBox<String> etatCombo = new ComboBox<>(FXCollections.observableArrayList("en cours", "traitée"));
            etatCombo.setValue(reclamation.getEtat());
            etatCombo.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 5; -fx-border-color: #d1d5db; -fx-border-radius: 5; -fx-padding: 5;");
            stateBox.getChildren().addAll(stateLabel, etatCombo);

            // Conversation section
            VBox chatSection = new VBox(10);
            Label chatLabel = new Label("Conversation:");
            chatLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #374151; -fx-font-weight: bold;");
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(200);
            VBox chatContainer = new VBox(5);
            chatContainer.setStyle("-fx-padding: 10;");
            loadChatMessages(chatContainer, reclamation);
            scrollPane.setContent(chatContainer);

            HBox inputBox = new HBox(10);
            inputBox.setAlignment(Pos.CENTER_LEFT);
            TextArea messageArea = new TextArea();
            messageArea.setPromptText("Entrez votre message au client...");
            messageArea.setPrefHeight(60);
            messageArea.setWrapText(true);
            messageArea.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 5; -fx-border-color: #d1d5db; -fx-border-radius: 5; -fx-padding: 5;");
            Button sendButton = new Button("Envoyer");
            sendButton.setStyle("-fx-background-color: #1e40af; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 16; -fx-cursor: hand;");
            sendButton.setOnAction(e -> {
                try {
                    String message = messageArea.getText().trim();
                    if (!message.isEmpty()) {
                        serviceReclamation.addMessage(reclamation.getId_reclamation(), "ADMIN", message);
                        messageArea.clear();
                        chatContainer.getChildren().clear();
                        loadChatMessages(chatContainer, reclamation);
                        statusLabel.setText("Message envoyé avec succès");
                    }
                } catch (SQLException ex) {
                    statusLabel.setText("Erreur lors de l'envoi: " + ex.getMessage());
                    System.out.println(ex.getMessage());
                }
            });
            inputBox.getChildren().addAll(messageArea, sendButton);

            chatSection.getChildren().addAll(chatLabel, scrollPane, inputBox);

            content.getChildren().addAll(stateBox, chatSection);

            dialog.getDialogPane().setContent(content);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    try {
                        String newEtat = etatCombo.getValue();
                        if (newEtat != null && !newEtat.isEmpty() && !newEtat.equals(reclamation.getEtat())) {
                            serviceReclamation.updateEtat(reclamation.getId_reclamation(), newEtat);
                        }
                        statusLabel.setText("Réclamation traitée avec succès");
                        loadReclamations();
                    } catch (SQLException e) {
                        statusLabel.setText("Erreur lors du traitement: " + e.getMessage());
                        System.out.println(e.getMessage());
                    }
                }
                return null;
            });

            dialog.showAndWait();
        }

        @Override
        protected void updateItem(Reclamation reclamation, boolean empty) {
            super.updateItem(reclamation, empty);

            if (empty || reclamation == null) {
                setGraphic(null);
            } else {
                titleLabel.setText(reclamation.getType());
                dateLabel.setText(reclamation.getDateIncident());

                statusLabel.setText(reclamation.getEtat());
                statusLabel.getStyleClass().removeAll("status-inprogress", "status-resolved");
                switch (reclamation.getEtat()) {
                    case "en cours":
                        statusLabel.getStyleClass().add("status-inprogress");
                        break;
                    case "traitée":
                        statusLabel.getStyleClass().add("status-resolved");
                        break;
                }

                try {
                    Client client = serviceClient.getById(reclamation.getClientId());
                    clientLabel.setText(client != null ? client.getNom() + " " + client.getPrenom() : "Client #" + reclamation.getClientId());
                } catch (SQLException e) {
                    clientLabel.setText("Client #" + reclamation.getClientId());
                }

                String desc = reclamation.getDescription();
                descriptionLabel.setText(desc.length() > 100 ? desc.substring(0, 97) + "..." : desc);

                setGraphic(cellContainer);
            }
        }
    }

    private void loadChatMessages(VBox chatContainer, Reclamation reclamation) {
        chatContainer.getChildren().clear();
        if (reclamation != null) {
            try {
                List<ServiceReclamation.Message> messages = serviceReclamation.getMessagesByReclamationId(reclamation.getId_reclamation());
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
}