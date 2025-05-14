package controllers;

import entities.Client;
import entities.Commentaires;
import entities.Publication;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Rating;
import services.ServiceCommentaire;
import services.ServicePublication;
import services.EmailService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;

public class AddCommentaireController {
    @FXML
    private VBox formVBox;
    @FXML
    private Label titleLabel;
    @FXML
    private Rating ratingControl;
    @FXML
    private TextArea commentaireField;
    @FXML
    private Button saveBtn;

    private Client currentClient;
    private ServiceCommentaire serviceCommentaire = new ServiceCommentaire();
    private ServicePublication servicePublication = new ServicePublication();
    private Stage stage;
    private Commentaires editingCommentaire;
    private int badWordOffenses = 0;
    private Integer publicationId;

    public void setClient(Client client, Stage stage, Commentaires commentaire, Integer publicationId) {
        this.currentClient = client;
        this.stage = stage;
        this.editingCommentaire = commentaire;
        this.publicationId = publicationId;
        initializeForm();
        if (commentaire != null) {
            populateForm(commentaire);
        }
    }

    private void initializeForm() {
        formVBox.setOpacity(0);
        formVBox.setScaleX(0.9);
        formVBox.setScaleY(0.9);

        FadeTransition fade = new FadeTransition(Duration.millis(600), formVBox);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(600), formVBox);
        scale.setToX(1);
        scale.setToY(1);

        ParallelTransition transition = new ParallelTransition(fade, scale);
        transition.play();

        ratingControl.ratingProperty().addListener((obs, old, newValue) -> updateSaveButtonState());
        commentaireField.textProperty().addListener((obs, old, newValue) -> {
            validateField(commentaireField, newValue);
            updateSaveButtonState();
        });

        if (editingCommentaire != null) {
            titleLabel.setText("Modifier Commentaire");
        }
    }

    private void populateForm(Commentaires commentaire) {
        ratingControl.setRating(commentaire.getRating());
        commentaireField.setText(commentaire.getCommentaire());
    }

    @FXML
    private void saveCommentaire() {
        if (badWordOffenses >= 2) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Vous êtes banni pour usage répété de mots vulgaires!");
            return;
        }

        try {
            int rating = (int) ratingControl.getRating();
            String commentaire = commentaireField.getText().trim();
            validateInput(commentaire, rating);

            String dateCommentaire = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Commentaires newCommentaire;

            if (editingCommentaire == null) {
                if (publicationId == null || publicationId == 0) {
                    throw new IllegalArgumentException("ID de publication invalide!");
                }
                if (currentClient == null) {
                    throw new IllegalArgumentException("Utilisateur non connecté!");
                }

                newCommentaire = new Commentaires(
                        currentClient.getId_client(),
                        publicationId,
                        commentaire,
                        dateCommentaire,
                        rating
                );
                serviceCommentaire.ajouter(newCommentaire);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Commentaire ajouté avec succès!");

                // Send email notification asynchronously
                Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                        Publication publication = servicePublication.getById(publicationId);
                        EmailService.sendCommentNotification(currentClient, publication, newCommentaire);
                        System.out.println("Email notification sent successfully for publication ID: " + publicationId);
                    } catch (Exception e) {
                        Platform.runLater(() -> showAlert(Alert.AlertType.WARNING, "Attention",
                                "Commentaire ajouté, mais l'envoi de l'email a échoué : " + e.getMessage()));
                        e.printStackTrace();
                    }
                });
            } else {
                editingCommentaire.setCommentaire(commentaire);
                editingCommentaire.setRating(rating);
                editingCommentaire.setDate_commentaire(dateCommentaire);
                editingCommentaire.setPublication_id(publicationId != null ? publicationId : editingCommentaire.getPublication_id());
                serviceCommentaire.modifier(editingCommentaire);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Commentaire modifié avec succès!");
            }

            stage.close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du commentaire : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cancel() {
        stage.close();
    }

    private void validateField(TextInputControl field, String text) {
        String[] badWords = {"merde", "putain", "con", "salope", "fuck", "connard", "pute", "bitch", "damn", "shit"};
        boolean hasBadWord = false;
        String foundBadWord = null;

        for (String word : badWords) {
            if (text.toLowerCase().contains(word)) {
                hasBadWord = true;
                foundBadWord = word;
                break;
            }
        }

        if (hasBadWord) {
            field.getStyleClass().add("error-field");
            handleBadWordOffense(foundBadWord);
        } else if (text.trim().isEmpty()) {
            field.getStyleClass().add("error-field");
        } else {
            field.getStyleClass().remove("error-field");
        }
    }

    private void handleBadWordOffense(String badWord) {
        badWordOffenses++;
        if (badWordOffenses == 1) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Mot vulgaire détecté : \"" + badWord + "\". Un autre usage entraînera un bannissement!");
        } else if (badWordOffenses >= 2) {
            showAlert(Alert.AlertType.ERROR, "Banni", "Vous êtes banni pour usage répété de mots vulgaires!");
            saveBtn.setDisable(true);
        }
    }

    private void updateSaveButtonState() {
        int rating = (int) ratingControl.getRating();
        String commentaire = commentaireField.getText().trim();
        saveBtn.setDisable(!isValidInput(commentaire, rating) || badWordOffenses >= 2);
    }

    private boolean isValidInput(String commentaire, int rating) {
        String[] badWords = {"merde", "putain", "con", "salope", "fuck", "connard", "pute", "bitch", "damn", "shit"};
        for (String word : badWords) {
            if (commentaire.toLowerCase().contains(word)) {
                return false;
            }
        }
        return rating >= 1 && rating <= 5 && !commentaire.trim().isEmpty();
    }

    private void validateInput(String commentaire, int rating) {
        String[] badWords = {"merde", "putain", "con", "salope", "fuck", "connard", "pute", "bitch", "damn", "shit"};
        for (String word : badWords) {
            if (commentaire.toLowerCase().contains(word)) {
                throw new IllegalArgumentException("Les mots vulgaires sont interdits!");
            }
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5!");
        }
        if (commentaire.isEmpty()) {
            throw new IllegalArgumentException("Le commentaire ne peut pas être vide!");
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