package controllers;

import entities.ReservationVehicule;
import entities.Vehicule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.ServiceReservationVehicule;
import services.ServiceVehicule;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class AdminVehiclesController {
    private ServiceVehicule serviceVehicule = new ServiceVehicule();
    private ServiceReservationVehicule serviceReservation = new ServiceReservationVehicule();

    @FXML
    private TabPane tabPane;

    @FXML
    private ListView<Vehicule> vehicleListView;

    @FXML
    private TextField vehicleSearchField;

    @FXML
    private ListView<ReservationVehicule> reservationListView;

    @FXML
    private TextField reservationSearchField;

    private ObservableList<Vehicule> allVehicles;
    private ObservableList<ReservationVehicule> allReservations;

    @FXML
    public void initialize() {
        setupVehicleListView();
        setupReservationListView();
        loadVehicles(null);
        loadReservations(null);

        if (vehicleSearchField != null) {
            vehicleSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterVehicles(newValue);
            });
        }

        if (reservationSearchField != null) {
            reservationSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterReservations(newValue);
            });
        }
    }

    private void setupVehicleListView() {
        vehicleListView.setCellFactory(new Callback<ListView<Vehicule>, ListCell<Vehicule>>() {
            @Override
            public ListCell<Vehicule> call(ListView<Vehicule> param) {
                return new ListCell<Vehicule>() {
                    private HBox container;
                    private ImageView imageView;
                    private VBox infoContainer;
                    private Text typeText;
                    private HBox detailsBox;
                    private VBox priceBox, locationBox, datesBox;
                    private Text priceLabel, priceValue, locationLabel, locationValue, datesLabel, datesValue;
                    private Button editButton, deleteButton;
                    private Vehicule currentVehicle;

                    {
                        container = new HBox(15);
                        container.getStyleClass().add("list-cell-container");

                        imageView = new ImageView();
                        imageView.setFitWidth(80);
                        imageView.setFitHeight(80);
                        imageView.setPreserveRatio(true);

                        infoContainer = new VBox(5);
                        infoContainer.getStyleClass().add("vehicle-info-container");
                        HBox.setHgrow(infoContainer, Priority.ALWAYS);

                        typeText = new Text();
                        typeText.getStyleClass().add("vehicle-type");

                        detailsBox = new HBox(15);
                        priceBox = new VBox(2);
                        priceLabel = new Text("Prix par jour");
                        priceLabel.getStyleClass().add("info-label");
                        priceValue = new Text();
                        priceValue.getStyleClass().add("info-value");
                        priceBox.getChildren().addAll(priceLabel, priceValue);

                        locationBox = new VBox(2);
                        locationLabel = new Text("Lieux");
                        locationLabel.getStyleClass().add("info-label");
                        locationValue = new Text();
                        locationValue.getStyleClass().add("info-value");
                        locationBox.getChildren().addAll(locationLabel, locationValue);

                        datesBox = new VBox(2);
                        datesLabel = new Text("Dates");
                        datesLabel.getStyleClass().add("info-label");
                        datesValue = new Text();
                        datesValue.getStyleClass().add("info-value");
                        datesBox.getChildren().addAll(datesLabel, datesValue);

                        detailsBox.getChildren().addAll(priceBox, locationBox, datesBox);

                        infoContainer.getChildren().addAll(typeText, detailsBox);

                        editButton = new Button("Modifier");
                        editButton.getStyleClass().addAll("button", "button-primary");
                        editButton.setPrefHeight(30.0);
                        editButton.setPrefWidth(100.0);
                        editButton.setVisible(false);

                        deleteButton = new Button("Supprimer");
                        deleteButton.getStyleClass().addAll("button", "button-danger");
                        deleteButton.setPrefHeight(30.0);
                        deleteButton.setPrefWidth(100.0);
                        deleteButton.setVisible(false);

                        HBox buttonBox = new HBox(10);
                        buttonBox.getChildren().addAll(editButton, deleteButton);
                        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

                        container.getChildren().addAll(imageView, infoContainer, buttonBox);

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

                    @Override
                    protected void updateItem(Vehicule vehicle, boolean empty) {
                        super.updateItem(vehicle, empty);

                        if (empty || vehicle == null) {
                            setText(null);
                            setGraphic(null);
                            currentVehicle = null;
                        } else {
                            currentVehicle = vehicle;
                            typeText.setText(vehicle.getType());
                            priceValue.setText(String.format("%.2f TND", vehicle.getPrix()));
                            locationValue.setText(String.format("%s à %s", vehicle.getLieuPrise(), vehicle.getLieuRetour()));
                            datesValue.setText(String.format("%s - %s",
                                    vehicle.getDateLocation() != null ? vehicle.getDateLocation().toString() : "N/A",
                                    vehicle.getDateRetour() != null ? vehicle.getDateRetour().toString() : "N/A"));

                            String imagePath = vehicle.getImagePath();
                            String resourcePath = "/images/" + (imagePath != null && !imagePath.isEmpty() ? imagePath : "default.jpg");
                            try {
                                Image image = new Image(getClass().getResource(resourcePath).toExternalForm());
                                imageView.setImage(image);
                            } catch (Exception e) {
                                System.err.println("Error loading image: " + resourcePath);
                                Image placeholder = new Image(getClass().getResource("/images/default.jpg").toExternalForm());
                                imageView.setImage(placeholder);
                            }

                            editButton.setOnAction(event -> editSelectedVehicle(event, currentVehicle));
                            deleteButton.setOnAction(event -> removeSelectedVehicle(event, currentVehicle));

                            setGraphic(container);
                        }
                    }
                };
            }
        });
    }

    private void setupReservationListView() {
        reservationListView.setCellFactory(new Callback<ListView<ReservationVehicule>, ListCell<ReservationVehicule>>() {
            @Override
            public ListCell<ReservationVehicule> call(ListView<ReservationVehicule> param) {
                return new ListCell<ReservationVehicule>() {
                    private HBox container;
                    private VBox infoContainer;
                    private Text clientText;
                    private HBox detailsBox;
                    private VBox vehicleBox, dateBox, locationBox;
                    private Text vehicleLabel, vehicleValue, dateLabel, dateValue, locationLabel, locationValue;
                    private Button viewButton, cancelButton;
                    private ReservationVehicule currentReservation;

                    {
                        container = new HBox(15);
                        container.getStyleClass().add("list-cell-container");

                        infoContainer = new VBox(5);
                        infoContainer.getStyleClass().add("reservation-info-container");
                        HBox.setHgrow(infoContainer, Priority.ALWAYS);

                        clientText = new Text();
                        clientText.getStyleClass().add("reservation-client");

                        detailsBox = new HBox(15);
                        vehicleBox = new VBox(2);
                        vehicleLabel = new Text("Véhicule");
                        vehicleLabel.getStyleClass().add("info-label");
                        vehicleValue = new Text();
                        vehicleValue.getStyleClass().add("info-value");
                        vehicleBox.getChildren().addAll(vehicleLabel, vehicleValue);

                        locationBox = new VBox(2);
                        locationLabel = new Text("Lieux");
                        locationLabel.getStyleClass().add("info-label");
                        locationValue = new Text();
                        locationValue.getStyleClass().add("info-value");
                        locationBox.getChildren().addAll(locationLabel, locationValue);

                        dateBox = new VBox(2);
                        dateLabel = new Text("Date");
                        dateLabel.getStyleClass().add("info-label");
                        dateValue = new Text();
                        dateValue.getStyleClass().add("info-value");
                        dateBox.getChildren().addAll(dateLabel, dateValue);

                        detailsBox.getChildren().addAll(vehicleBox, locationBox, dateBox);

                        infoContainer.getChildren().addAll(clientText, detailsBox);

                        viewButton = new Button("Voir");
                        viewButton.getStyleClass().addAll("button", "button-primary");
                        viewButton.setPrefHeight(30.0);
                        viewButton.setPrefWidth(100.0);
                        viewButton.setVisible(false);

                        cancelButton = new Button("Annuler");
                        cancelButton.getStyleClass().addAll("button", "button-danger");
                        cancelButton.setPrefHeight(30.0);
                        cancelButton.setPrefWidth(100.0);
                        cancelButton.setVisible(false);

                        HBox buttonBox = new HBox(10);
                        buttonBox.getChildren().addAll(viewButton, cancelButton);
                        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

                        container.getChildren().addAll(infoContainer, buttonBox);

                        setOnMouseEntered(e -> {
                            if (!isEmpty()) {
                                viewButton.setVisible(true);
                                cancelButton.setVisible(true);
                            }
                        });
                        setOnMouseExited(e -> {
                            viewButton.setVisible(false);
                            cancelButton.setVisible(false);
                        });
                    }

                    @Override
                    protected void updateItem(ReservationVehicule reservation, boolean empty) {
                        super.updateItem(reservation, empty);

                        if (empty || reservation == null) {
                            setText(null);
                            setGraphic(null);
                            currentReservation = null;
                        } else {
                            currentReservation = reservation;
                            clientText.setText(reservation.getClientEmail() != null ? reservation.getClientEmail() : "N/A");
                            vehicleValue.setText(getVehicleType(reservation.getVehiculeId()));
                            locationValue.setText(String.format("%s à %s", reservation.getLieuPrise(), reservation.getLieuRetour()));
                            dateValue.setText(String.format("%s - %s",
                                    reservation.getDateDebut() != null ? reservation.getDateDebut().toString() : "N/A",
                                    reservation.getDateFin() != null ? reservation.getDateFin().toString() : "N/A"));

                            viewButton.setOnAction(event -> viewReservationDetails(event, currentReservation));
                            cancelButton.setOnAction(event -> cancelReservation(event, currentReservation));

                            setGraphic(container);
                        }
                    }
                };
            }
        });
    }

    private String getVehicleType(int vehiculeId) {
        try {
            Vehicule vehicule = serviceVehicule.getById(vehiculeId);
            return vehicule != null ? vehicule.getType() : "Inconnu";
        } catch (SQLException e) {
            return "Erreur";
        }
    }

    private void loadVehicles(Void unused) {
        try {
            allVehicles = FXCollections.observableArrayList(serviceVehicule.getAll());
            vehicleListView.setItems(allVehicles);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des véhicules", e.getMessage());
        }
    }

    private void loadReservations(Void unused) {
        try {
            allReservations = FXCollections.observableArrayList(serviceReservation.getAll());
            reservationListView.setItems(allReservations);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des réservations", e.getMessage());
        }
    }

    private void filterVehicles(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            vehicleListView.setItems(allVehicles);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            ObservableList<Vehicule> filteredList = allVehicles.stream().filter(vehicle ->
                            vehicle.getType().toLowerCase().contains(lowerCaseFilter) ||
                                    vehicle.getLieuPrise().toLowerCase().contains(lowerCaseFilter) ||
                                    vehicle.getLieuRetour().toLowerCase().contains(lowerCaseFilter) ||
                                    String.valueOf(vehicle.getPrix()).contains(lowerCaseFilter))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            vehicleListView.setItems(filteredList);
        }
    }

    private void filterReservations(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            reservationListView.setItems(allReservations);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            ObservableList<ReservationVehicule> filteredList = allReservations.stream().filter(reservation ->
                            (reservation.getClientEmail() != null ? reservation.getClientEmail().toLowerCase().contains(lowerCaseFilter) : false) ||
                                    getVehicleType(reservation.getVehiculeId()).toLowerCase().contains(lowerCaseFilter) ||
                                    (reservation.getLieuPrise() != null ? reservation.getLieuPrise().toLowerCase().contains(lowerCaseFilter) : false) ||
                                    (reservation.getLieuRetour() != null ? reservation.getLieuRetour().toLowerCase().contains(lowerCaseFilter) : false) ||
                                    (reservation.getDateDebut() != null ? reservation.getDateDebut().toString().contains(lowerCaseFilter) : false))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            reservationListView.setItems(filteredList);
        }
    }

    @FXML
    void refreshVehicleList(ActionEvent event) {
        loadVehicles(null);
    }

    @FXML
    void refreshReservationList(ActionEvent event) {
        loadReservations(null);
    }

    @FXML
    void addNewVehicle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddVehiclePopup.fxml"));
            Parent root = loader.load();
            AddVehiclePopupController controller = loader.getController();
            controller.setRefreshCallback(this::loadVehicles);
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node)event.getSource()).getScene().getWindow());
            popupStage.setTitle("Ajouter un véhicule");
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
    void addNewReservation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddReservationPopup.fxml"));
            Parent root = loader.load();
            AddReservationPopupController controller = loader.getController();
            controller.setRefreshCallback(this::loadReservations);
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node)event.getSource()).getScene().getWindow());
            popupStage.setTitle("Ajouter une Réservation");
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
    void editSelectedVehicle(ActionEvent event, Vehicule vehicle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditVehiclePopup.fxml"));
            Parent root = loader.load();
            EditVehiclePopupController controller = loader.getController();
            controller.setVehicle(vehicle);
            controller.setRefreshCallback(this::loadVehicles);
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node)event.getSource()).getScene().getWindow());
            popupStage.setTitle("Modifier le véhicule");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/admin-common.css").toExternalForm());
            popupStage.setScene(scene);
            popupStage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger le formulaire de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void removeSelectedVehicle(ActionEvent event, Vehicule vehicle) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le véhicule");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le véhicule " + vehicle.getType() + " ?");
        ButtonType yesButton = new ButtonType("Oui");
        ButtonType noButton = new ButtonType("Non");
        confirmation.getButtonTypes().setAll(yesButton, noButton);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                try {
                    serviceVehicule.supprimer(vehicle.getId());
                    loadVehicles(null);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Véhicule supprimé",
                            "Le véhicule a été supprimé avec succès.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de suppression",
                            "Une erreur est survenue lors de la suppression: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    void viewReservationDetails(ActionEvent event, ReservationVehicule reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewReservationPopup.fxml"));
            Parent root = loader.load();
            ViewReservationController controller = loader.getController();
            controller.setReservationVehicule(reservation);
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node)event.getSource()).getScene().getWindow());
            popupStage.setTitle("Détails de la Réservation");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/admin-common.css").toExternalForm());
            popupStage.setScene(scene);
            popupStage.setResizable(false);
            controller.setStage(popupStage);
            popupStage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement",
                    "Impossible de charger les détails de la réservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void cancelReservation(ActionEvent event, ReservationVehicule reservation) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation d'annulation");
        confirmation.setHeaderText("Annuler la réservation");
        confirmation.setContentText("Êtes-vous sûr de vouloir annuler la réservation de " + (reservation.getClientEmail() != null ? reservation.getClientEmail() : "Inconnu") + " ?");
        ButtonType yesButton = new ButtonType("Oui");
        ButtonType noButton = new ButtonType("Non");
        confirmation.getButtonTypes().setAll(yesButton, noButton);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                try {
                    serviceReservation.supprimer(reservation.getId());
                    loadReservations(null);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation annulée",
                            "La réservation a été annulée avec succès.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'annulation",
                            "Une erreur est survenue lors de l'annulation: " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}