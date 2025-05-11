package controllers;

import entities.Client;
import entities.Flight;
import entities.ReservationVol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import services.ServiceFlight;
import services.ServiceReservationVol;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import javafx.scene.web.WebView;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;

public class MyReservationsController {

    @FXML
    private GridPane reservationsContainer;

    @FXML
    private Button backButton;

    private Client currentClient;
    private ServiceReservationVol reservationService = new ServiceReservationVol();
    private ServiceFlight flightService = new ServiceFlight();
    private UserDashboardController dashboardController;
    private boolean isEmbeddedInDashboard = false;

    public void setClient(Client client) {
        this.currentClient = client;
        loadReservations();
    }

    public void setDashboardController(UserDashboardController controller) {
        this.dashboardController = controller;
        this.isEmbeddedInDashboard = (controller != null);
    }

    private void loadReservations() {
        try {
            if (currentClient == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun client connecté", "Veuillez vous connecter pour voir vos réservations.");
                return;
            }

            List<ReservationVol> reservations = reservationService.recupererReservationsByClientId(currentClient.getId_client());
            reservationsContainer.getChildren().clear();

            if (reservations == null || reservations.isEmpty()) {
                Label noReservationsLabel = new Label("Aucune réservation trouvée.");
                noReservationsLabel.setFont(Font.font("System", 16));
                reservationsContainer.add(noReservationsLabel, 0, 0, 5, 1);
            } else {
                for (int i = 0; i < reservations.size(); i++) {
                    ReservationVol reservation = reservations.get(i);
                    VBox card = createReservationCard(reservation);
                    int row = i / 5;
                    int col = i % 5;
                    reservationsContainer.add(card, col, row);
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération des réservations", e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createReservationCard(ReservationVol reservation) {
        VBox card = new VBox();
        card.setPrefWidth(180);
        card.setPrefHeight(420);
        card.setStyle("-fx-background-color: white; -fx-border-color: #039BE5; -fx-border-radius: 0; " +
                "-fx-padding: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 5);");
        card.setSpacing(10);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), card);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), card);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        card.setOnMouseEntered(e -> scaleIn.play());
        card.setOnMouseExited(e -> scaleOut.play());

        ImageView reservationImage = new ImageView();
        reservationImage.setFitWidth(160);
        reservationImage.setFitHeight(120);
        reservationImage.setPreserveRatio(true);
        reservationImage.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 4;");

        try {
            reservationImage.setImage(new Image(getClass().getResource("/images/img.png").toExternalForm()));
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image /images/img.png : " + e.getMessage());
            reservationImage.setImage(new Image("https://via.placeholder.com/160x120?text=Image+Non+Trouvée"));
        }

        Label passengerName = new Label("Passager: " + reservation.getPassenger_name());
        passengerName.setFont(Font.font("System", FontWeight.BOLD, 14));
        passengerName.setWrapText(true);

        Label date = new Label("Date: " + (reservation.getReservationvol_date() != null ? new SimpleDateFormat("dd/MM/yyyy").format(reservation.getReservationvol_date()) : "N/A"));
        date.setFont(Font.font("System", 12));
        date.setStyle("-fx-text-fill: #555;");
        date.setWrapText(true);

        Label price = new Label("Prix: $" + String.format("%.2f", reservation.getPrice()));
        price.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-font-size: 12px;");
        price.setWrapText(true);

        Label status = new Label("Statut: " + reservation.getStatus());
        status.setFont(Font.font("System", 12));
        status.setWrapText(true);

        VBox buttonsContainer = new VBox(8);

        Button modifyButton = new Button("Modifier");
        modifyButton.setStyle("-fx-background-color: #039BE5; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 6 10; -fx-background-radius: 5;");
        modifyButton.setPrefWidth(160);

        ScaleTransition modifyScaleIn = new ScaleTransition(Duration.millis(150), modifyButton);
        modifyScaleIn.setToX(1.1);
        modifyScaleIn.setToY(1.1);

        ScaleTransition modifyScaleOut = new ScaleTransition(Duration.millis(150), modifyButton);
        modifyScaleOut.setToX(1.0);
        modifyScaleOut.setToY(1.0);

        modifyButton.setOnMouseEntered(e -> modifyScaleIn.play());
        modifyButton.setOnMouseExited(e -> modifyScaleOut.play());
        modifyButton.setOnAction(e -> modifyReservation(reservation));

        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 6 10; -fx-background-radius: 5;");
        deleteButton.setPrefWidth(160);

        ScaleTransition deleteScaleIn = new ScaleTransition(Duration.millis(150), deleteButton);
        deleteScaleIn.setToX(1.1);
        deleteScaleIn.setToY(1.1);

        ScaleTransition deleteScaleOut = new ScaleTransition(Duration.millis(150), deleteButton);
        deleteScaleOut.setToX(1.0);
        deleteScaleOut.setToY(1.0);

        deleteButton.setOnMouseEntered(e -> deleteScaleIn.play());
        deleteButton.setOnMouseExited(e -> deleteScaleOut.play());
        deleteButton.setOnAction(e -> deleteReservation(reservation));

        Button ticketButton = new Button("Afficher le billet");
        ticketButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 6 10; -fx-background-radius: 5;");
        ticketButton.setPrefWidth(160);

        ScaleTransition ticketScaleIn = new ScaleTransition(Duration.millis(150), ticketButton);
        ticketScaleIn.setToX(1.1);
        ticketScaleIn.setToY(1.1);

        ScaleTransition ticketScaleOut = new ScaleTransition(Duration.millis(150), ticketButton);
        ticketScaleOut.setToX(1.0);
        ticketScaleOut.setToY(1.0);

        ticketButton.setOnMouseEntered(e -> ticketScaleIn.play());
        ticketButton.setOnMouseExited(e -> ticketScaleOut.play());
        ticketButton.setOnAction(e -> showTicket(reservation));

        buttonsContainer.getChildren().addAll(modifyButton, deleteButton, ticketButton);

        card.getChildren().addAll(
                reservationImage,
                passengerName,
                date,
                price,
                status,
                buttonsContainer
        );

        return card;
    }

    private void showTicket(ReservationVol reservation) {
        try {
            Flight flight = flightService.getFlightById(reservation.getFlight_id());
            if (flight == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Vol non trouvé", "Impossible de récupérer les détails du vol.");
                return;
            }

            Stage ticketStage = new Stage();
            ticketStage.setTitle("Billet de vol");

            WebView webView = new WebView();
            webView.setPrefSize(800, 300);

            String htmlContent = buildTicketHtml(reservation, flight);
            webView.getEngine().loadContent(htmlContent);

            Button saveImageButton = new Button("Enregistrer comme image");
            saveImageButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
            saveImageButton.setOnAction(e -> saveTicketAsImage(webView, ticketStage));

            Button closeButton = new Button("Fermer");
            closeButton.setStyle("-fx-background-color: #0288D1; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
            closeButton.setOnAction(e -> ticketStage.close());

            HBox buttonBox = new HBox(10, saveImageButton, closeButton);
            buttonBox.setPadding(new Insets(10));
            buttonBox.setStyle("-fx-alignment: center;");

            VBox ticketContainer = new VBox(10, webView, buttonBox);
            ticketContainer.setPadding(new Insets(10));
            ticketContainer.setStyle("-fx-background-color: #E3F2FD;");

            Scene ticketScene = new Scene(ticketContainer);
            ticketStage.setScene(ticketScene);
            ticketStage.show();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération des détails du vol", e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildTicketHtml(ReservationVol reservation, Flight flight) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='fr'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Billet ").append(escapeHtml(flight.getAirline())).append("</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; background-color: #f5f5f5; }");
        html.append(".ticket { display: flex; width: 800px; height: 300px; background-color: white; border-radius: 15px; overflow: hidden; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); border: 2px solid #8b8b8b; }");
        html.append(".sidebar { width: 60px; background-color: #0e4d80; color: white; padding: 10px 0; display: flex; flex-direction: column; align-items: center; justify-content: center; }");
        html.append(".sidebar-text { writing-mode: vertical-rl; text-orientation: mixed; transform: rotate(180deg); font-weight: bold; font-size: 16px; letter-spacing: 1px; }");
        html.append(".content { flex-grow: 1; display: flex; }");
        html.append(".left-section { width: 60%; padding: 20px; background-color: #0e4d80; color: white; }");
        html.append(".passenger-info, .flight-info, .location-info { border-bottom: 1px dotted rgba(255, 255, 255, 0.3); padding-bottom: 15px; margin-bottom: 15px; }");
        html.append(".label { font-size: 12px; margin-bottom: 4px; }");
        html.append(".value { font-weight: bold; font-size: 18px; }");
        html.append(".flight-details { display: flex; justify-content: space-between; margin-bottom: 8px; }");
        html.append(".flight-detail { width: 30%; }");
        html.append(".location-details { display: flex; justify-content: space-between; }");
        html.append(".location { width: 45%; }");
        html.append(".barcode { margin-top: 20px; text-align: center; }");
        html.append(".barcode img { max-width: 90%; height: 50px; }");
        html.append(".right-section { width: 40%; background-color: #0e4d80; display: flex; flex-direction: column; align-items: center; justify-content: center; position: relative; overflow: hidden; padding: 20px; }");
        html.append(".plane-logo { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 120px; height: 120px; opacity: 0.2; }");
        html.append(".airline-name { position: relative; z-index: 2; color: white; font-size: 24px; font-weight: bold; text-align: center; letter-spacing: 2px; margin-bottom: 20px; }");
        html.append(".flight-times { position: relative; z-index: 2; color: white; text-align: center; width: 100%; }");
        html.append(".time-label { font-size: 12px; margin-bottom: 4px; }");
        html.append(".time-value { font-weight: bold; font-size: 16px; margin-bottom: 12px; }");
        html.append(".price-tag { position: relative; z-index: 2; color: white; background-color: rgba(255, 255, 255, 0.2); padding: 8px 15px; border-radius: 20px; font-weight: bold; margin-top: 15px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='ticket'>");
        html.append("<div class='sidebar'>");
        html.append("<div class='sidebar-text'>").append(escapeHtml(flight.getAirline())).append("</div>");
        html.append("</div>");
        html.append("<div class='content'>");
        html.append("<div class='left-section'>");
        html.append("<div class='passenger-info'>");
        html.append("<div class='label'>Passager</div>");
        html.append("<div class='value'>").append(escapeHtml(reservation.getPassenger_name())).append("</div>");
        html.append("</div>");
        html.append("<div class='flight-info'>");
        html.append("<div class='flight-details'>");
        html.append("<div class='flight-detail'>");
        html.append("<div class='label'>Embarquement</div>");
        html.append("<div class='value'>").append(timeFormat.format(flight.getDeparture_Time())).append("</div>");
        html.append("</div>");
        html.append("<div class='flight-detail'>");
        html.append("<div class='label'>Porte</div>");
        html.append("<div class='value'>N/A</div>");
        html.append("</div>");
        html.append("<div class='flight-detail'>");
        html.append("<div class='label'>Vol</div>");
        html.append("<div class='value'>").append(escapeHtml(flight.getFlight_number())).append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div class='flight-details'>");
        html.append("<div class='flight-detail'>");
        html.append("<div class='label'>Date</div>");
        html.append("<div class='value'>").append(dateFormat.format(flight.getFlight_date())).append("</div>");
        html.append("</div>");
        html.append("<div class='flight-detail'>");
        html.append("<div class='label'>Classe</div>");
        html.append("<div class='value'>Économie</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div class='location-info'>");
        html.append("<div class='location-details'>");
        html.append("<div class='location'>");
        html.append("<div class='label'>De</div>");
        html.append("<div class='value'>").append(escapeHtml(flight.getDeparture())).append("</div>");
        html.append("</div>");
        html.append("<div class='location'>");
        html.append("<div class='label'>À</div>");
        html.append("<div class='value'>").append(escapeHtml(flight.getDestination())).append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div class='barcode'>");
        html.append("<svg width='240' height='50'>");
        html.append("<rect x='0' y='0' width='4' height='50' fill='white'></rect>");
        html.append("<rect x='8' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='12' y='0' width='4' height='50' fill='white'></rect>");
        html.append("<rect x='22' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='28' y='0' width='6' height='50' fill='white'></rect>");
        html.append("<rect x='38' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='44' y='0' width='4' height='50' fill='white'></rect>");
        html.append("<rect x='52' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='60' y='0' width='6' height='50' fill='white'></rect>");
        html.append("<rect x='70' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='76' y='0' width='4' height='50' fill='white'></rect>");
        html.append("<rect x='84' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='90' y='0' width='6' height='50' fill='white'></rect>");
        html.append("<rect x='100' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='106' y='0' width='4' height='50' fill='white'></rect>");
        html.append("<rect x='114' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='120' y='0' width='6' height='50' fill='white'></rect>");
        html.append("<rect x='132' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='138' y='0' width='4' height='50' fill='white'></rect>");
        html.append("<rect x='146' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='152' y='0' width='6' height='50' fill='white'></rect>");
        html.append("<rect x='164' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='170' y='0' width='4' height='50' fill='white'></rect>");
        html.append("<rect x='178' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='184' y='0' width='6' height='50' fill='white'></rect>");
        html.append("<rect x='194' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='200' y='0' width='4' height='50' fill='white'></rect>");
        html.append("<rect x='208' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='214' y='0' width='6' height='50' fill='white'></rect>");
        html.append("<rect x='224' y='0' width='2' height='50' fill='white'></rect>");
        html.append("<rect x='230' y='0' width='4' height='50' fill='white'></rect>");
        html.append("<rect x='238' y='0' width='2' height='50' fill='white'></rect>");
        html.append("</svg>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div class='right-section'>");
        html.append("<div class='plane-logo'>");
        html.append("<svg viewBox='0 0 24 24' fill='white'>");
        html.append("<path d='M21,16V14L13,9V3.5A1.5,1.5 0 0,0 11.5,2A1.5,1.5 0 0,0 10,3.5V9L2,14V16L10,13.5V19L8,20.5V22L11.5,21L15,22V20.5L13,19V13.5L21,16Z' />");
        html.append("</svg>");
        html.append("</div>");
        html.append("<div class='airline-name'>").append(escapeHtml(flight.getAirline())).append("</div>");
        html.append("<div class='flight-times'>");
        html.append("<div class='time-label'>Départ</div>");
        html.append("<div class='time-value'>").append(timeFormat.format(flight.getDeparture_Time())).append("</div>");
        html.append("<div class='time-label'>Arrivée</div>");
        html.append("<div class='time-value'>").append(timeFormat.format(flight.getArrival_Time())).append("</div>");
        html.append("</div>");
        html.append("<div class='price-tag'>$").append(String.format("%.2f", reservation.getPrice())).append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }

    private void saveTicketAsImage(WebView webView, Stage stage) {
        WritableImage image = webView.snapshot(null, null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le billet comme image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PNG", "*.png"));
        fileChooser.setInitialFileName("Billet_" + System.currentTimeMillis() + ".png");
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Billet sauvegardé", "Le billet a été enregistré comme image à : " + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la sauvegarde de l'image", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private void modifyReservation(ReservationVol reservation) {
        showAlert(Alert.AlertType.INFORMATION, "Modification", "Fonction de modification non implémentée encore.", "Veuillez ajouter la logique de modification.");
    }

    private void deleteReservation(ReservationVol reservation) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réservation ?");
        confirmationAlert.setContentText("La réservation sera marquée comme supprimée.");

        ButtonType buttonTypeYes = new ButtonType("Oui");
        ButtonType buttonTypeNo = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeYes) {
                try {
                    reservationService.updateReservationStatus(reservation, "Deleted");
                    reservation.setStatus("Deleted");

                    for (javafx.scene.Node node : reservationsContainer.getChildren()) {
                        if (node instanceof VBox) {
                            VBox card = (VBox) node;
                            for (javafx.scene.Node child : card.getChildren()) {
                                if (child instanceof Label && ((Label) child).getText().startsWith("Statut: ")) {
                                    ((Label) child).setText("Statut: Deleted");
                                    break;
                                }
                            }
                            for (javafx.scene.Node child : card.getChildren()) {
                                if (child instanceof VBox) {
                                    for (javafx.scene.Node button : ((VBox) child).getChildren()) {
                                        if (button instanceof Button) {
                                            ((Button) button).setDisable(true);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation marquée comme supprimée.", "");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour du statut", e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Annulé", "Suppression annulée.", "");
            }
        });
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            if (isEmbeddedInDashboard && dashboardController != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
                Parent root = loader.load();
                ChercherVolController controller = loader.getController();
                controller.setClient(currentClient);
                controller.setDashboardController(dashboardController);
                dashboardController.loadContentToArea(root);
            } else {
                Stage stage = (Stage) backButton.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
                Parent root = loader.load();
                ChercherVolController controller = loader.getController();
                controller.setClient(currentClient);
                stage.setScene(new Scene(root));
                stage.setTitle("Recherche de vol");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement FXML", "Impossible de charger la page de recherche : " + e.getMessage());
            e.printStackTrace();
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