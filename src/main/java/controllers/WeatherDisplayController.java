package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WeatherDisplayController {

    @FXML
    private Label weatherHeader;

    @FXML
    private Label cityLabel;

    @FXML
    private ImageView weatherIcon;

    @FXML
    private Label temperatureLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label updateTimeLabel;

    private UserDashboardController dashboardController;

    public void setDashboardController(UserDashboardController controller) {
        this.dashboardController = controller;
    }

    public void setWeatherData(String temperature, String description, String updateTime, String iconUrl) {
        temperatureLabel.setText("Température : " + temperature + "°C");
        descriptionLabel.setText("Conditions : " + description);
        updateTimeLabel.setText("Mise à jour : " + updateTime);
        if (iconUrl != null && !iconUrl.isEmpty()) {
            try {
                weatherIcon.setImage(new Image(iconUrl));
            } catch (Exception e) {
                // Fallback if image fails to load
                weatherIcon.setImage(null);
            }
        }
    }
}