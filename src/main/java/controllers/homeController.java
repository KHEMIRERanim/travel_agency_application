package controllers;

import entities.Hotels;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import services.ServiceHotel;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class homeController implements Initializable {

    @FXML private ImageView selectedImage;
    @FXML private Label selectedTitle;
    @FXML private Label selectedDetails;
    @FXML private HBox carousel;
    @FXML private Button cancelButton;

    private Hotels selectedHotel;
    private final ServiceHotel service = new ServiceHotel();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            List<Hotels> hotels = service.recuperer();
            for (Hotels h : hotels) {
                ImageView thumb = new ImageView(new Image(getClass().getResource("/images/doubleroom.jpg").toExternalForm()));
                thumb.setFitWidth(150);
                thumb.setFitHeight(100);
                thumb.setPreserveRatio(true);
                thumb.setOnMouseClicked(e -> selectHotel(h));
                carousel.getChildren().add(thumb);
            }

            if (!hotels.isEmpty()) {
                selectHotel(hotels.get(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectHotel(Hotels h) {
        selectedHotel = h;
        selectedTitle.setText(h.getNom_hotel());
        selectedDetails.setText(
                "Destination: " + h.getDestination() + "\n" +
                        "Prix: " + h.getPrix() + " €/nuit\n" +
                        "Type de chambre: " + h.getType_chambre() + "\n" +
                        "Wifi: " + h.isWifi() + "\n" +
                        "Piscine: " + h.isPiscine() + "\n" +
                        "Status: " + h.getStatus()
        );
        selectedImage.setImage(new Image(getClass().getResource("/images/doubleroom.jpg").toExternalForm()));
    }

    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void loginButtonOnAction(ActionEvent event) {
        goToLoginForm();
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    public void goToLoginForm() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(new Scene(root, 600, 400));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerButtonOnAction(ActionEvent event) {
        goToRegisterForm();
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    public void goToRegisterForm() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
            Stage registerStage = new Stage();
            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.setScene(new Scene(root, 600, 400));
            registerStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToAddHotel() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjoutHotel.fxml"));
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginToBook(ActionEvent e){
        Alert alert = new Alert(Alert.AlertType.ERROR, "Please login or register first in order to make a booking.");
        alert.show();
    }

    @FXML
    private void goToEditHotel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutHotel.fxml"));
            Parent root = loader.load();

            AjoutHotelController controller = loader.getController();
            controller.setHotelForEdit(selectedHotel); // You must store selectedHotel when showing its details

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Hôtel");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
