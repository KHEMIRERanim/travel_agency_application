package controllers;

import entities.Commentaires;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import services.ServiceCommentaire;

import java.sql.SQLException;
import java.util.List;

public class ShowCommentairesController {
    @FXML
    private VBox commentairesVBox;
    @FXML
    private Label titleLabel;

    private ServiceCommentaire serviceCommentaire = new ServiceCommentaire();
    private int publicationId;

    public void setPublication(int publicationId, String publicationTitle) throws SQLException {
        this.publicationId = publicationId;
        titleLabel.setText("Commentaires pour: " + publicationTitle);
        loadCommentaires();
    }

    private void loadCommentaires() throws SQLException {
        commentairesVBox.getChildren().clear();
        List<Commentaires> commentaires = serviceCommentaire.getCommentairesByPublicationId(publicationId);
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

            FadeTransition fade = new FadeTransition(Duration.millis(600), card);
            fade.setToValue(1);

            TranslateTransition slide = new TranslateTransition(Duration.millis(600), card);
            slide.setToY(0);

            ParallelTransition transition = new ParallelTransition(fade, slide);
            transition.setDelay(Duration.millis(index * 150));
            transition.play();

            commentairesVBox.getChildren().add(card);
            index++;
        }
    }

    private VBox createCommentaireCard(Commentaires commentaire) {
        VBox card = new VBox(10);
        card.setPrefWidth(520);
        card.getStyleClass().add("comment-card");

        Label contentLabel = new Label(commentaire.getCommentaire());
        contentLabel.getStyleClass().add("comment-content");
        contentLabel.setWrapText(true);

        String stars = "★".repeat(commentaire.getRating()) + "☆".repeat(5 - commentaire.getRating());
        Label ratingLabel = new Label("Note: " + stars);
        ratingLabel.getStyleClass().add("comment-rating");

        Label dateLabel = new Label("Commenté le " + commentaire.getDate_commentaire());
        dateLabel.getStyleClass().add("comment-date");

        card.getChildren().addAll(contentLabel, ratingLabel, dateLabel);
        return card;
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