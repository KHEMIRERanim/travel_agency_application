package controllers;

import entities.Client;
import entities.Publication;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.ServicePublication;
import org.controlsfx.control.Notifications;

import java.sql.SQLException;

public class PublicationEditController {

    @FXML
    private VBox formVBox;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextField titreField;
    @FXML
    private TextArea contenuField;
    @FXML
    private Label likeCountLabel;
    @FXML
    private CheckBox activeCheckBox;
    @FXML
    private Button saveButton;

    private Publication publication;
    private Client currentClient;
    private ServicePublication servicePublication = new ServicePublication();
    private Stage stage;

    public void setPublication(Publication publication, Client client, VBox publicationsVBox) {
        this.publication = publication;
        this.currentClient = client;
        this.stage = new Stage();
        initializeForm();
    }

    private void initializeForm() {
        // Populate fields
        typeComboBox.setValue(publication.getTypePublication());
        titreField.setText(publication.getTitre());
        contenuField.setText(publication.getContenu());
        likeCountLabel.setText(String.valueOf(publication.getLikeCount()));
        activeCheckBox.setSelected(publication.isActive());

        // Apply entrance animation
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

        // Real-time input validation
        typeComboBox.valueProperty().addListener((obs, old, newValue) -> updateSaveButtonState());
        titreField.textProperty().addListener((obs, old, newValue) -> {
            validateField(titreField, newValue);
            updateSaveButtonState();
        });
        contenuField.textProperty().addListener((obs, old, newValue) -> {
            validateField(contenuField, newValue);
            updateSaveButtonState();
        });
    }

    @FXML
    private void savePublication() {
        try {
            String type = typeComboBox.getValue();
            String titre = titreField.getText().trim();
            String contenu = contenuField.getText().trim();
            validateInput(titre, contenu, type);

            publication.setTypePublication(type);
            publication.setTitre(titre);
            publication.setContenu(contenu);
            publication.setActive(activeCheckBox.isSelected());
            servicePublication.modifier(publication);

            showNotification("Succès", "Publication modifiée avec succès!", NotificationType.SUCCESS);
            stage.close();
        } catch (IllegalArgumentException | SQLException e) {
            showNotification("Erreur", e.getMessage(), NotificationType.ERROR);
        }
    }

    @FXML
    private void cancelEdit() {
        stage.close();
    }

    private void validateField(TextInputControl field, String text) {
        String[] badWords = {"merde", "putain", "con", "salope", "fuck", "connard", "pute", "bitch", "damn", "shit"};
        boolean hasBadWord = false;
        for (String word : badWords) {
            if (text.toLowerCase().contains(word)) {
                hasBadWord = true;
                break;
            }
        }
        if (hasBadWord || text.trim().isEmpty()) {
            field.getStyleClass().add("error-field");
        } else {
            field.getStyleClass().remove("error-field");
        }
    }

    private void updateSaveButtonState() {
        String type = typeComboBox.getValue();
        String titre = titreField.getText().trim();
        String contenu = contenuField.getText().trim();
        saveButton.setDisable(!isValidInput(titre, contenu, type));
    }

    private boolean isValidInput(String titre, String contenu, String type) {
        String[] badWords = {"merde", "putain", "con", "salope", "fuck", "connard", "pute", "bitch", "damn", "shit"};
        for (String word : badWords) {
            if (titre.toLowerCase().contains(word) || contenu.toLowerCase().contains(word)) {
                return false;
            }
        }
        return type != null && !titre.trim().isEmpty() && !contenu.trim().isEmpty();
    }

    private void validateInput(String titre, String contenu, String type) {
        String[] badWords = {"merde", "putain", "con", "salope", "fuck", "connard", "pute", "bitch", "damn", "shit"};
        for (String word : badWords) {
            if (titre.toLowerCase().contains(word) || contenu.toLowerCase().contains(word)) {
                throw new IllegalArgumentException("Les mots vulgaires sont interdits!");
            }
        }
        if (type == null) {
            throw new IllegalArgumentException("Le type de publication est requis!");
        }
        if (titre.isEmpty() || contenu.isEmpty()) {
            throw new IllegalArgumentException("Le titre et le contenu ne peuvent pas être vides!");
        }
    }

    private void showNotification(String title, String text, NotificationType type) {
        Notifications notification = Notifications.create()
                .title(title)
                .text(text)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT);
        switch (type) {
            case SUCCESS:
                notification.showInformation();
                break;
            case WARNING:
                notification.showWarning();
                break;
            case ERROR:
                notification.showError();
                break;
        }
    }

    private enum NotificationType {
        SUCCESS, WARNING, ERROR
    }
}