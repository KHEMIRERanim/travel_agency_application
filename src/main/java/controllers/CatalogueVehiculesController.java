package controllers;

import entities.Vehicule;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.ServiceVehicule;
import javafx.geometry.Pos;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CatalogueVehiculesController {
    @FXML
    private TilePane vehiclePane;
    @FXML
    private ComboBox<String> typeFilter;
    @FXML
    private TextField minPriceField;
    @FXML
    private TextField maxPriceField;
    @FXML
    private VBox formContainer;

    private ServiceVehicule vehicleService = new ServiceVehicule();
    private VBox currentForm;
    private UserDashboardController dashboardController; // Référence au UserDashboardController
    private boolean isEmbeddedInDashboard = false; // Indique si intégré dans le dashboard

    // Méthode pour recevoir la référence au UserDashboardController
    public void setDashboardController(UserDashboardController controller) {
        this.dashboardController = controller;
        this.isEmbeddedInDashboard = (controller != null);
    }

    @FXML
    public void initialize() {
        loadVehicles();
        try {
            List<String> types = vehicleService.getAllTypes();
            typeFilter.getItems().addAll(types);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "Could not load vehicle types: " + e.getMessage());
        }
    }

    private void loadVehicles() {
        try {
            List<Vehicule> vehicles = vehicleService.getAll();
            vehiclePane.getChildren().clear();
            for (Vehicule vehicle : vehicles) {
                vehiclePane.getChildren().add(createVehicleCard(vehicle));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        try {
            String type = typeFilter.getValue();
            String minPriceText = minPriceField.getText().trim();
            String maxPriceText = maxPriceField.getText().trim();

            Double minPrice = minPriceText.isEmpty() ? null : Double.parseDouble(minPriceText);
            Double maxPrice = maxPriceText.isEmpty() ? null : Double.parseDouble(maxPriceText);

            if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Min price cannot be greater than max price.");
                return;
            }

            List<Vehicule> vehicles = vehicleService.filterVehicles(type, minPrice, maxPrice);
            vehiclePane.getChildren().clear();
            for (Vehicule vehicle : vehicles) {
                vehiclePane.getChildren().add(createVehicleCard(vehicle));
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Price fields must be valid numbers.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", e.getMessage());
        }
    }

    private HBox createVehicleCard(Vehicule vehicle) {
        HBox card = new HBox(15);
        card.getStyleClass().add("vehicle-card");
        card.setPrefWidth(300);

        // Image du véhicule
        ImageView imageView = new ImageView();
        String imagePath = vehicle.getImagePath();
        String resourcePath = "/images/" + (imagePath != null && !imagePath.isEmpty() ? imagePath : "default.jpg");
        try {
            if (getClass().getResource(resourcePath) != null) {
                Image image = new Image(getClass().getResource(resourcePath).toExternalForm());
                imageView.setImage(image);
            } else {
                Image placeholder = new Image(getClass().getResource("/images/default.jpg").toExternalForm());
                imageView.setImage(placeholder);
            }
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Error loading image: " + resourcePath);
            Image placeholder = new Image(getClass().getResource("/images/default.jpg").toExternalForm());
            imageView.setImage(placeholder);
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);
        }

        // Détails du véhicule
        VBox details = new VBox(8);
        details.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label typeLabel = new Label(vehicle.getType());
        typeLabel.getStyleClass().addAll("vehicle-type", "bold-text");

        Label priceLabel = new Label(String.format("%.2f TND/jour", vehicle.getPrix()));
        priceLabel.getStyleClass().add("price-text");

        // Caractéristiques supplémentaires
        HBox features = new HBox(10);
        features.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Bouton de réservation avec style amélioré
        Button reserveButton = new Button("Réserver");
        reserveButton.getStyleClass().add("reserve-button");
        reserveButton.setOnAction(event -> openReservationForm(vehicle));

        // Ajout d'une animation hover sur le bouton
        reserveButton.setOnMouseEntered(e -> {
            reserveButton.setScaleX(1.1);
            reserveButton.setScaleY(1.1);
        });
        reserveButton.setOnMouseExited(e -> {
            reserveButton.setScaleX(1.0);
            reserveButton.setScaleY(1.0);
        });

        details.getChildren().addAll(
            typeLabel,
            priceLabel,
            features,
            reserveButton
        );

        card.getChildren().addAll(imageView, details);

        // Animation de la carte au survol
        card.setOnMouseEntered(e -> {
            card.setEffect(new javafx.scene.effect.DropShadow(10, javafx.scene.paint.Color.rgb(0, 0, 0, 0.2)));
            card.setTranslateY(-5);
        });
        card.setOnMouseExited(e -> {
            card.setEffect(new javafx.scene.effect.DropShadow(5, javafx.scene.paint.Color.rgb(0, 0, 0, 0.1)));
            card.setTranslateY(0);
        });

        return card;
    }

    private void openReservationForm(Vehicule vehicle) {
        try {
            // Clear previous form
            if (currentForm != null) {
                formContainer.getChildren().clear();
            }

            // Load form
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReservationForm.fxml"));
            currentForm = loader.load();
            ReservationFormController controller = loader.getController();
            controller.setVehicle(vehicle);

            // Load vehicle image
            ImageView vehicleImage = new ImageView();
            String imagePath = vehicle.getImagePath();
            String resourcePath = "/images/" + (imagePath != null && !imagePath.isEmpty() ? imagePath : "default.jpg");
            try {
                if (getClass().getResource(resourcePath) != null) {
                    Image image = new Image(getClass().getResource(resourcePath).toExternalForm());
                    vehicleImage.setImage(image);
                } else {
                    Image placeholder = new Image(getClass().getResource("/images/default.jpg").toExternalForm());
                    vehicleImage.setImage(placeholder);
                }
                vehicleImage.setFitWidth(200);
                vehicleImage.setFitHeight(150);
                vehicleImage.getStyleClass().add("vehicle-image");
            } catch (Exception e) {
                System.err.println("Error loading vehicle image: " + resourcePath);
                Image placeholder = new Image("file:src/main/resources/images/default.jpg");
                vehicleImage.setImage(placeholder);
                vehicleImage.setFitWidth(200);
                vehicleImage.setFitHeight(150);
                vehicleImage.getStyleClass().add("vehicle-image");
            }

            // Add image and form to container
            formContainer.getChildren().setAll(vehicleImage, currentForm);

            // Slide-in animation
            formContainer.setTranslateX(-350); // Start off-screen
            formContainer.setVisible(true);
            formContainer.setManaged(true);
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), formContainer);
            slideIn.setToX(0);
            slideIn.play();

            // Hide form after submission or cancellation
            controller.setOnFormClosed(ignored -> {
                TranslateTransition slideOut = new TranslateTransition(Duration.millis(500), formContainer);
                slideOut.setToX(-350);
                slideOut.setOnFinished(event -> {
                    formContainer.getChildren().clear();
                    formContainer.setVisible(false);
                    formContainer.setManaged(false);
                    currentForm = null;
                });
                slideOut.play();
            });
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Form Load Error", "Could not load reservation form: " + e.getMessage());
        }
    }

    @FXML
    public void openManageReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ManageReservations.fxml"));
            Parent managePane = loader.load();
            if (isEmbeddedInDashboard && dashboardController != null) {
                // Charger dans contentArea si intégré dans le dashboard
                dashboardController.loadContentToArea(managePane);
            } else {
                // Mode standalone - nouvelle fenêtre
                Stage stage = new Stage();
                Scene scene = new Scene(managePane, 800, 600);
                scene.getStylesheets().add(getClass().getResource("/styles/med.css").toExternalForm());
                scene.getStylesheets().add("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css");
                stage.setTitle("Manage Reservations");
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Form Load Error", "Could not load manage reservations: " + e.getMessage());
        }
    }

    @FXML
    private void backToDashboard() {
        if (isEmbeddedInDashboard && dashboardController != null) {
            // Charger le contenu initial ou vider contentArea
            dashboardController.loadContentToArea(new Label("Sélectionnez une option"));
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