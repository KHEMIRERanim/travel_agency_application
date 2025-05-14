package controllers;

import entities.Client;
import entities.Publication;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.ServicePublication;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MesPublicationsController {
    @FXML
    private VBox publicationsVBox;
    @FXML
    private Button addPublicationBtn;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> sortComboBox;

    private ServicePublication servicePublication = new ServicePublication();
    private Client currentClient;
    private Set<Integer> likedPublications = new HashSet<>();

    public void setClient(Client client) {
        this.currentClient = client;
        initializeControls();
        loadPublications();
    }

    private void initializeControls() {
        searchField.textProperty().addListener((obs, oldValue, newValue) -> loadPublications());
        sortComboBox.valueProperty().addListener((obs, oldValue, newValue) -> loadPublications());
    }


    //afichge des carte
    private void loadPublications() {
        publicationsVBox.getChildren().clear();
        try {
            String searchText = searchField.getText().trim().toLowerCase();
            String sortOption = sortComboBox.getValue();
            List<Publication> publications = servicePublication.getAllPublications(searchText, sortOption);
            if (publications.isEmpty()) {
                Label noPublicationsLabel = new Label("Aucune publication trouvée.");
                noPublicationsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666; -fx-font-family: 'Segoe UI';");
                publicationsVBox.getChildren().add(noPublicationsLabel);
                return;
            }

            int index = 0;
            for (Publication publication : publications) {
                VBox card = createPublicationCard(publication);
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

                publicationsVBox.getChildren().add(card);
                index++;
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des publications: " + e.getMessage());
        }
    }
////asnaaali el carte
    private VBox createPublicationCard(Publication publication) {
        VBox card = new VBox(15);
        card.setPrefWidth(700);
        card.getStyleClass().add("publication-card");

        Label titleLabel = new Label(publication.getTitre());
        titleLabel.getStyleClass().add("publication-title");
        titleLabel.setStyle("-fx-cursor: hand;");
        titleLabel.setOnMouseClicked(e -> {
            try {
                showCommentaires(publication.getId_publication(), publication.getTitre());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        Label typeLabel = new Label("Type: " + publication.getTypePublication());
        typeLabel.getStyleClass().add("publication-type");

        Label contentLabel = new Label(publication.getContenu());
        contentLabel.getStyleClass().add("publication-content");
        contentLabel.setWrapText(true);

        Label dateLabel = new Label("Publié le " + publication.getDate_publication());
        dateLabel.getStyleClass().add("publication-date");

        Label statusLabel = new Label(publication.isActive() ? "Actif" : "Inactif");
        statusLabel.getStyleClass().add("publication-status");
        statusLabel.getStyleClass().add(publication.isActive() ? "status-active" : "status-inactive");

        HBox likeBox = new HBox(10);
        likeBox.setAlignment(Pos.CENTER_LEFT);
        Label likeCountLabel = new Label("Likes: " + publication.getLikeCount());
        likeCountLabel.getStyleClass().add("like-count-label");

        Button likeBtn = new Button("Like");
        likeBtn.getStyleClass().add("action-button");
        likeBtn.getStyleClass().add("like-button");
        likeBtn.setDisable(publication.getClient_id() == currentClient.getId_client() || likedPublications.contains(publication.getId_publication()));
        likeBtn.setOnAction(e -> likePublication(publication, likeCountLabel));

        Button dislikeBtn = new Button("Dislike");
        dislikeBtn.getStyleClass().add("action-button");
        dislikeBtn.getStyleClass().add("dislike-button");
        dislikeBtn.setDisable(publication.getClient_id() == currentClient.getId_client() || likedPublications.contains(publication.getId_publication()));
        dislikeBtn.setOnAction(e -> dislikePublication(publication, likeCountLabel));

        likeBox.getChildren().addAll(likeCountLabel, likeBtn, dislikeBtn);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button commentaireBtn = new Button("Commentaire");
        commentaireBtn.getStyleClass().add("action-button");
        commentaireBtn.getStyleClass().add("comment-button");
        commentaireBtn.setOnAction(e -> showAddCommentaire(publication.getId_publication()));

        // Only show edit/delete/toggle buttons for current client's publications
        if (publication.getClient_id() == currentClient.getId_client()) {
            Button modifierBtn = new Button("Modifier");
            modifierBtn.getStyleClass().add("action-button");
            modifierBtn.setOnAction(e -> navigateToEditPage(publication));

            Button supprimerBtn = new Button("Supprimer");
            supprimerBtn.getStyleClass().add("action-button");
            supprimerBtn.getStyleClass().add("delete-button");
            supprimerBtn.setOnAction(e -> deletePublication(publication));

            Button toggleBtn = new Button(publication.isActive() ? "Désactiver" : "Activer");
            toggleBtn.getStyleClass().add("action-button");
            toggleBtn.getStyleClass().add("toggle-button");
            toggleBtn.getStyleClass().add(publication.isActive() ? "active" : "inactive");
            toggleBtn.setOnAction(e -> togglePublicationStatus(publication));

            buttonBox.getChildren().addAll(toggleBtn, modifierBtn, supprimerBtn, commentaireBtn);
        } else {
            buttonBox.getChildren().add(commentaireBtn);
        }

        card.getChildren().addAll(titleLabel, typeLabel, contentLabel, dateLabel, statusLabel, likeBox, buttonBox);
        return card;
    }

    private void likePublication(Publication publication, Label likeCountLabel) {
        try {
            servicePublication.incrementLikeCount(publication.getId_publication());
            publication.setLikeCount(publication.getLikeCount() + 1);
            likeCountLabel.setText("Likes: " + publication.getLikeCount());
            likedPublications.add(publication.getId_publication());
            updateLikeButtons(publication);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Publication likée!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du like: " + e.getMessage());
        }
    }

    private void dislikePublication(Publication publication, Label likeCountLabel) {
        try {
            servicePublication.decrementLikeCount(publication.getId_publication());
            publication.setLikeCount(publication.getLikeCount() - 1);
            likeCountLabel.setText("Likes: " + publication.getLikeCount());
            likedPublications.add(publication.getId_publication());
            updateLikeButtons(publication);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Publication dislikée!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du dislike: " + e.getMessage());
        }
    }

    private void updateLikeButtons(Publication publication) {
        for (Node node : publicationsVBox.getChildren()) {
            if (node instanceof VBox) {
                VBox card = (VBox) node;
                for (Node child : card.getChildren()) {
                    if (child instanceof HBox && ((HBox) child).getChildren().stream().anyMatch(n -> n instanceof Button && n.getStyleClass().contains("like-button"))) {
                        HBox likeBox = (HBox) child;
                        Button likeBtn = (Button) likeBox.getChildren().stream()
                                .filter(n -> n instanceof Button && n.getStyleClass().contains("like-button"))
                                .findFirst().orElse(null);
                        Button dislikeBtn = (Button) likeBox.getChildren().stream()
                                .filter(n -> n instanceof Button && n.getStyleClass().contains("dislike-button"))
                                .findFirst().orElse(null);
                        if (likeBtn != null && dislikeBtn != null) {
                            likeBtn.setDisable(publication.getClient_id() == currentClient.getId_client() || likedPublications.contains(publication.getId_publication()));
                            dislikeBtn.setDisable(publication.getClient_id() == currentClient.getId_client() || likedPublications.contains(publication.getId_publication()));
                        }
                    }
                }
            }
        }
    }

    private void showCommentaires(int publicationId, String publicationTitle) throws SQLException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowCommentaires.fxml"));
            Parent root = loader.load();
            ShowCommentairesController controller = loader.getController();

            Stage showStage = new Stage();
            controller.setPublication(publicationId, publicationTitle);

            Scene scene = new Scene(root, 600, 500);
            scene.getStylesheets().add(getClass().getResource("/styles/publications.css").toExternalForm());
            showStage.setScene(scene);
            showStage.setTitle("Commentaires pour " + publicationTitle);
            showStage.initModality(Modality.APPLICATION_MODAL);
            showStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des commentaires: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAddCommentaire(int publicationId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddCommentaire.fxml"));
            Parent root = loader.load();
            AddCommentaireController controller = loader.getController();

            Stage addStage = new Stage();
            controller.setClient(currentClient, addStage, null, publicationId);

            Scene scene = new Scene(root, 600, 500);
            scene.getStylesheets().add(getClass().getResource("/styles/publications.css").toExternalForm());
            addStage.setScene(scene);
            addStage.setTitle("Ajouter un Commentaire");
            addStage.initModality(Modality.APPLICATION_MODAL);
            addStage.setOnHidden(e -> loadPublications());
            addStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la page d'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showAddPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddPublication.fxml"));
            Parent root = loader.load();
            AddPublicationController controller = loader.getController();

            Stage addStage = new Stage();
            controller.setClient(currentClient, addStage);

            Scene scene = new Scene(root, 760, 700);
            scene.getStylesheets().add(getClass().getResource("/styles/publications.css").toExternalForm());
            addStage.setScene(scene);
            addStage.setTitle("Ajouter une Nouvelle Publication");
            addStage.initModality(Modality.APPLICATION_MODAL);
            addStage.setOnHidden(e -> loadPublications());
            addStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la page d'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToEditPage(Publication publication) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PublicationEdit.fxml"));
            Parent root = loader.load();
            PublicationEditController controller = loader.getController();

            Stage editStage = new Stage();
            controller.setPublication(publication, currentClient, null);

            Scene scene = new Scene(root, 760, 700);
            scene.getStylesheets().add(getClass().getResource("/styles/publications.css").toExternalForm());
            editStage.setScene(scene);
            editStage.setTitle("Modifier Publication");
            editStage.initModality(Modality.APPLICATION_MODAL);
            editStage.setOnHidden(e -> loadPublications());
            editStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la page d'édition: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deletePublication(Publication publication) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Voulez-vous vraiment supprimer cette publication?");
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/publications.css").toExternalForm());
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    servicePublication.supprimer(publication);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Publication supprimée avec succès!");
                    loadPublications();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void togglePublicationStatus(Publication publication) {
        try {
            publication.setActive(!publication.isActive());
            servicePublication.modifier(publication);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Statut de la publication mis à jour!");
            loadPublications();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour du statut: " + e.getMessage());
            e.printStackTrace();
        }
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