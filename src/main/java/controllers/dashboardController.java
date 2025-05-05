package controllers;// Importing essential packages
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;
import java.io.File;
import java.util.ResourceBundle;
import java.net.URL;

//FXML Loader call all the methods and classes defined in the controller
public class dashboardController implements Initializable {
    @FXML
    private ImageView logoHotel;
    @FXML
    private ImageView lobbyBackground;
    @FXML
    private Button cancelButton;
    @FXML
    private Label welcomeLabel;

    private Integer id_utilisateur;
    private String firstname;

    //Initializing a controller after the root has been processed
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File logoFile = new File("Hotels/images/logo.jpg");
        Image logoImage = new Image(logoFile.toURI().toString());
        logoHotel.setImage(logoImage);

        File lobbyHotelFile = new File("Hotels/images/lobbyHotel.jpg");
        Image lobbyHotelImage = new Image(lobbyHotelFile.toURI().toString());
        lobbyBackground.setImage(lobbyHotelImage);

        id_utilisateur= loginController.user_id;
        firstname= loginController.firstname;
        welcomeLabel.setText("Welcome " + firstname + "!");

    }
    //Function to go back to the previous fxml file of the program
    public void logoutButtonOnAction(ActionEvent event){
        logout();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    public void logout(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("homepage.fxml"));
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(new Scene(root, 600, 400));
            loginStage.show();
        }catch(Exception e) {
            e.printStackTrace();
            e.getCause();
        }

    }
    //Creating a function to stop the process of the program with cancelButton
    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    //    Taking the user to Rooms FXML file
    public void ViewRoomsOnAction(ActionEvent event) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("rooms.fxml"));
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(new Scene(root, 600, 400));
            loginStage.show();
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        } catch(Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    //    Taking the user to makeBooking FXML file
    public void makeBookingOnAction(ActionEvent event) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("makeBooking.fxml"));
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(new Scene(root, 600, 400));
            loginStage.show();
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        } catch(Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }
    // Taking the user to myBookings FXML file
    public void myBookings(ActionEvent event) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("myBookings.fxml"));
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(new Scene(root, 700, 401));
            loginStage.show();
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        } catch(Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }
}