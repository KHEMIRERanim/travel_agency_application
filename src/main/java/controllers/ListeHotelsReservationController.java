package controllers;

import entities.Client;
import entities.Hotels;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import services.ServiceHotel;
import services.ServiceReservationHotel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListeHotelsReservationController implements Initializable {

    @FXML private ImageView selectedImage;
    @FXML private Label selectedTitle;
    @FXML private Label selectedDetails;
    @FXML private HBox carousel;
    @FXML private HBox ratingBox;

    private final Image STAR_FULL = new Image(getClass().getResource("/images/star.png").toExternalForm());
    private final Image STAR_EMPTY = new Image(getClass().getResource("/images/star_empty.png").toExternalForm());
    private Hotels selectedHotel;
    private final ServiceHotel service = new ServiceHotel();
    private Client currentClient;
    private final ServiceReservationHotel serviceReservationHotel = new ServiceReservationHotel();

    public void setClient(Client client) {
        this.currentClient = client;
    }

    private void refreshHotels() {
        carousel.getChildren().clear();

        try {
            List<Hotels> hotels = service.recuperer();
            for (Hotels h : hotels) {
                ImageView thumb = new ImageView();
                Rectangle clip2 = new Rectangle(150, 100);
                clip2.setArcWidth(40);
                clip2.setArcHeight(40);
                thumb.setClip(clip2);

                byte[] hotelImage = h.getImage();
                if (hotelImage != null && hotelImage.length > 0) {
                    Image image = new Image(new ByteArrayInputStream(hotelImage));
                    thumb.setImage(image);
                } else {
                    thumb.setImage(new Image(getClass().getResource("/images/doubleroom.jpg").toExternalForm()));
                }

                thumb.setFitWidth(150);
                thumb.setFitHeight(100);
                thumb.setOnMouseClicked(e -> selectHotel(h));
                thumb.setCursor(Cursor.HAND);


                carousel.getChildren().add(thumb);
            }

            if (!hotels.isEmpty()) {
                selectHotel(hotels.get(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Rectangle clip = new Rectangle(
                selectedImage.getFitWidth(),
                selectedImage.getFitHeight()
        );
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        selectedImage.setClip(clip);

        refreshHotels();
    }



    private void selectHotel(Hotels h) {
        selectedHotel = h;
        System.out.println(selectedHotel.getHotel_id() + " " + currentClient.getId_client() );
        selectedTitle.setText(h.getNom_hotel());
        selectedDetails.setText(
                "Destination: " + h.getDestination() + "\n" +
                        "Prix: " + h.getPrix() + " €/nuit\n" +
                        "Type de chambre: " + h.getType_chambre() + "\n" +
                        "Wifi: " + (h.isWifi() ? "Oui" : "Non") + "\n" +
                        "Piscine: " + (h.isPiscine() ? "Oui" : "Non") + "\n" +
                        "Status: " + h.getStatus()
        );

        ratingBox.getChildren().clear();
        int note = h.getNote();
        for (int i = 1; i <= 5; i++) {
            ImageView star = new ImageView(i <= note ? STAR_FULL : STAR_EMPTY);
            star.setFitWidth(16);
            star.setFitHeight(16);
            ratingBox.getChildren().add(star);
        }

        byte[] hotelImage = h.getImage();
        if (hotelImage != null && hotelImage.length > 0) {
            Image image = new Image(new ByteArrayInputStream(hotelImage));
            selectedImage.setImage(image);
        } else {
            selectedImage.setImage(new Image(getClass().getResource("/images/doubleroom.jpg").toExternalForm()));
        }
    }

    public void goToReservationHotel() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutReservationHotel.fxml"));
            Parent root = loader.load();

            AjoutReservationHotelController ctrl = loader.getController();
            System.out.println(selectedHotel.getHotel_id() + " " + currentClient.getId_client() );
            ctrl.setContext(selectedHotel.getHotel_id(), currentClient.getId_client());
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
