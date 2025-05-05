package controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.net.URL;

public class homeController implements Initializable {
    @FXML
    private Button cancelButton;
    @FXML
    private ImageView TwinRoom;
    @FXML
    private ImageView SingleRoom;
    @FXML
    private ImageView DoubleRoom;
    @FXML
    private ImageView logoHotel;
    @FXML
    private ImageView lobbyHotel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        Image TwinRoomImage = new Image(getClass().getResource("/images/twinroom.jpg").toExternalForm());
        TwinRoom.setImage(TwinRoomImage);

        Image SingleRoomImage = new Image(getClass().getResource("/images/singleroom.jpg").toExternalForm());
        SingleRoom.setImage(SingleRoomImage);

        Image DoubleRoomImage = new Image(getClass().getResource("/images/doubleroom.jpg").toExternalForm());
        DoubleRoom.setImage(DoubleRoomImage);

        Image logoHotelImage = new Image(getClass().getResource("/images/logo1.png").toExternalForm());
        logoHotel.setImage(logoHotelImage);

        Image lobbyHotelImage = new Image(getClass().getResource("/images/lobbyHotel.jpg").toExternalForm());
        lobbyHotel.setImage(lobbyHotelImage);

    }

    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void loginButtonOnAction(ActionEvent event) {

        goToLoginForm();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void goToLoginForm(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(new Scene(root, 600, 400));
            loginStage.show();
        } catch(Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void registerButtonOnAction(ActionEvent event){
        goToRegisterForm();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

    }

    public void goToRegisterForm(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(new Scene(root, 600, 400));
            loginStage.show();
        } catch(Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void contactusButtonOnAction(ActionEvent event) {
        goToContactUs();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void goToContactUs(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("contact.fxml"));
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(new Scene(root, 600, 400));
            loginStage.show();
        } catch(Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void loginToBook(ActionEvent e){
        Alert alert = new Alert(Alert.AlertType.ERROR, "Please login or register first in order to make a booking.");
        alert.show();
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


}