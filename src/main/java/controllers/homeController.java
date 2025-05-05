package controllers;// Importing essential packages
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
import java.util.ResourceBundle;
import java.net.URL;

//FXML Loader call all the methods and classes defined in the controller
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

    //Initializing a controller after the root has been processed
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        File TwinRoomFile = new File(getClass().getResource("/images/twinroom.jpg").toExternalForm());
        Image TwinRoomImage = new Image(TwinRoomFile.toURI().toString());
        TwinRoom.setImage(TwinRoomImage);

        File SingleRoomFile = new File(getClass().getResource("/images/singleroom.jpg").toExternalForm());
        Image SingleRoomImage = new Image(SingleRoomFile.toURI().toString());
        SingleRoom.setImage(SingleRoomImage);

        File DoubleRoomFile = new File(getClass().getResource("/images/doubleroom.jpg").toExternalForm());
        Image DoubleRoomImage = new Image(DoubleRoomFile.toURI().toString());
        DoubleRoom.setImage(DoubleRoomImage);

        File logoHotelFile = new File(getClass().getResource("/images/logo1.png").toExternalForm());
        Image logoHotelImage = new Image(logoHotelFile.toURI().toString());
        logoHotel.setImage(logoHotelImage);

        File lobbyHotelFile = new File(getClass().getResource("/images/lobbyHotel.jpg").toExternalForm());
        Image lobbyHotelImage = new Image(lobbyHotelFile.toURI().toString());
        lobbyHotel.setImage(lobbyHotelImage);

    }
    //Creating a function for user to stop the process of the program with cancelButton via GUI
    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    //Taking the user to login FXML file
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
    //Taking the user to register FXML file
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
    //Taking the user to contact FXML file
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

    //Displaying the banner if the user is trying to book the room without being logged in to the system
    public void loginToBook(ActionEvent e){
        Alert alert = new Alert(Alert.AlertType.ERROR, "Please login or register first in order to make a booking.");
        alert.show();
    }


}