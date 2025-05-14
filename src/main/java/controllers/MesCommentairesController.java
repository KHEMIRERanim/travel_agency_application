package controllers;

import entities.Client;
import entities.Commentaires;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.ServiceCommentaire;

import java.io.IOException;
import java.sql.SQLException;

public class MesCommentairesController {
    @FXML
    private VBox commentairesVBox;
    @FXML
    private Button addCommentaireBtn;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> sortComboBox;

    private ServiceCommentaire serviceCommentaire = new ServiceCommentaire();
    private Client currentClient;

    public void setClient(Client client) {
        this.currentClient = client;
        initializeControls();
        loadCommentaires();
    }

    private void initializeControls() {
        searchField.textProperty().addListener((obs, oldValue, newValue) -> loadCommentaires());
        sortComboBox.valueProperty().addListener((obs, oldValue, newValue) -> loadCommentaires());
    }

    private void loadCommentaires() {
        commentairesVBox.getChildren().clear();
        try {
            String searchText = searchField.getText().trim().toLowerCase();
            String sortOption = sortComboBox.getValue();
            java.util.List<Commentaires> commentaires = serviceCommentaire.getCommentaires(
                    currentClient.getId_client(),
                    searchText,
                    sortOption
            );
            if (commentaires.isEmpty()) {
                Label noCommentairesLabel = new Label("Aucun commentaire trouvé.");
                noCommentairesLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666; -fx-font-family: 'Segoe UI';");
                commentairesVBox.getChildren().add(noCommentairesLabel);
                return;
            }

            int index = 0;
            for (Commentaires commentaire : commentaires) {
                VBox card = createCommentaireCard(commentaire);
                card.setOpacity(0);
                card.setTranslateY(50);
                card.setScaleX(0.9);
                card.setScaleY(0.9);

                FadeTransition fade = new FadeTransition(Duration.millis(600), card);
                fade.setToValue(1);

                TranslateTransition slide = new TranslateTransition(Duration.millis(600), card);
                slide.setToY(0);

                ScaleTransition scale = new ScaleTransition(Duration.millis(600), card);
                scale.setToX(1);
                scale.setToY(1);

                ParallelTransition transition = new ParallelTransition(fade, slide, scale);
                transition.setDelay(Duration.millis(index * 150));
                transition.play();

                commentairesVBox.getChildren().add(card);
                index++;
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des commentaires: " + e.getMessage());
        }
    }

    private VBox createCommentaireCard(Commentaires commentaire) {
        VBox card = new VBox(15);
        card.setPrefWidth(700);
        card.getStyleClass().add("comment-card");

        Label contentLabel = new Label(commentaire.getCommentaire());
        contentLabel.getStyleClass().add("comment-content");
        contentLabel.setWrapText(true);

        String stars = "★".repeat(commentaire.getRating()) + "☆".repeat(5 - commentaire.getRating());
        Label ratingLabel = new Label("Note: " + stars);
        ratingLabel.getStyleClass().add("comment-rating");

        Label dateLabel = new Label("Commenté le " + commentaire.getDate_commentaire());
        dateLabel.getStyleClass().add("comment-date");

        if (commentaire.getClient_id() == currentClient.getId_client()) {
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            Button modifierBtn = new Button("Modifier");
            modifierBtn.getStyleClass().add("action-button");
            modifierBtn.setOnAction(e -> navigateToEditPage(commentaire));

            Button supprimerBtn = new Button("Supprimer");
            supprimerBtn.getStyleClass().add("action-button");
            supprimerBtn.getStyleClass().add("delete-button");
            supprimerBtn.setOnAction(e -> deleteCommentaire(commentaire));

            buttonBox.getChildren().addAll(modifierBtn, supprimerBtn);
            card.getChildren().addAll(contentLabel, ratingLabel, dateLabel, buttonBox);
        } else {
            card.getChildren().addAll(contentLabel, ratingLabel, dateLabel);
        }

        return card;
    }

    @FXML
    private void showAddPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddCommentaire.fxml"));
            Parent root = loader.load();
            AddCommentaireController controller = loader.getController();

            Stage addStage = new Stage();
            controller.setClient(currentClient, addStage, null, null); // General comment, no publication_id

            Scene scene = new Scene(root, 600, 500);
            scene.getStylesheets().add(getClass().getResource("/styles/publications.css").toExternalForm());
            addStage.setScene(scene);
            addStage.setTitle("Ajouter un Commentaire");
            addStage.initModality(Modality.APPLICATION_MODAL);
            addStage.setOnHidden(e -> loadCommentaires());
            addStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la page d'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToEditPage(Commentaires commentaire) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddCommentaire.fxml"));
            Parent root = loader.load();
            AddCommentaireController controller = loader.getController();

            Stage editStage = new Stage();
            controller.setClient(currentClient, editStage, commentaire, commentaire.getPublication_id());

            Scene scene = new Scene(root, 600, 500);
            scene.getStylesheets().add(getClass().getResource("/styles/publications.css").toExternalForm());
            editStage.setScene(scene);
            editStage.setTitle("Modifier Commentaire");
            editStage.initModality(Modality.APPLICATION_MODAL);
            editStage.setOnHidden(e -> loadCommentaires());
            editStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la page d'édition: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteCommentaire(Commentaires commentaire) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Voulez-vous vraiment supprimer ce commentaire?");
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/publications.css").toExternalForm());
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    serviceCommentaire.supprimer(commentaire);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Commentaire supprimé avec succès!");
                    loadCommentaires();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/publications.css").toExternalForm());
        alert.showAndWait();
    }
}