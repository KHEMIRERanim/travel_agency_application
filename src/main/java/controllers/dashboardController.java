package controllers;
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File logoFile = new File("Hotels/images/logo.jpg");
        Image logoImage = new Image(logoFile.toURI().toString());
        logoHotel.setImage(logoImage);

        File lobbyHotelFile = new File("Hotels/images/lobbyHotel.jpg");
        Image lobbyHotelImage = new Image(lobbyHotelFile.toURI().toString());
        lobbyBackground.setImage(lobbyHotelImage);
        /*
        id_utilisateur= loginController.user_id;
        firstname= loginController.firstname;
        welcomeLabel.setText("Welcome " + firstname + "!");
*/

        id_utilisateur= 5;
        firstname= "test";
        welcomeLabel.setText("Welcome " + firstname + "!");

    }

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

    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


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