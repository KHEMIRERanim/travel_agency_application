package controllers;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import entities.Client;
import entities.Hotels;
import entities.ReservationHotel;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import services.ServiceClient;
import services.ServiceHotel;
import services.ServiceReservationHotel;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HistoriqueReservationsHotelController implements Initializable {

    @FXML private TableView<ReservationHotel> table;
    @FXML private TableColumn<ReservationHotel, String> hotelNameCol;
    @FXML private TableColumn<ReservationHotel, String> hotelPriceCol;
    @FXML private TableColumn<ReservationHotel, String> checkinCol;
    @FXML private TableColumn<ReservationHotel, String> checkoutCol;
    @FXML private TableColumn<ReservationHotel, String> colPrixTotal;
    @FXML private TableColumn<ReservationHotel, String> statusCol;
    @FXML private Button generateReceiptButton;

    private int clientId;
    private final ServiceReservationHotel reservationService = new ServiceReservationHotel();
    private final ServiceHotel hotelService = new ServiceHotel();
    private final ServiceClient clientService = new ServiceClient();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void setClientId(int clientId) throws SQLException {
        this.clientId = clientId;
        loadData();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        hotelNameCol.setCellValueFactory(cellData -> {
            try {
                Hotels h = hotelService.recuperer().get(cellData.getValue().getHotel_id());
                return new ReadOnlyStringWrapper(h != null ? h.getNom_hotel() : "Inconnu");
            } catch (Exception e) {
                e.printStackTrace();
                return new ReadOnlyStringWrapper("Erreur");
            }
        });

        hotelPriceCol.setCellValueFactory(cellData -> {
            try {
                Hotels h = hotelService.recuperer().get(cellData.getValue().getHotel_id());
                return new ReadOnlyStringWrapper(h != null ? String.format("%.2f", h.getPrix()) : "0.00");
            } catch (Exception e) {
                e.printStackTrace();
                return new ReadOnlyStringWrapper("Erreur");
            }
        });

        checkinCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCheckin_date()));
        checkoutCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCheckout_date()));

        colPrixTotal.setCellValueFactory(cellData -> {
            try {
                LocalDate checkin = LocalDate.parse(cellData.getValue().getCheckin_date(), formatter);
                LocalDate checkout = LocalDate.parse(cellData.getValue().getCheckout_date(), formatter);
                long nbNuits = ChronoUnit.DAYS.between(checkin, checkout);
                Hotels hotel = hotelService.recuperer().get(cellData.getValue().getHotel_id());
                double total = hotel != null ? hotel.getPrix() * nbNuits : 0.0;
                return new ReadOnlyStringWrapper(String.format("%.2f TND", total));
            } catch (Exception e) {
                e.printStackTrace();
                return new ReadOnlyStringWrapper("Erreur");
            }
        });

        statusCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper("Confirmée"));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            generateReceiptButton.setDisable(newSelection == null);
        });
    }

    private void loadData() throws SQLException {
        List<ReservationHotel> filteredReservations = reservationService.recuperer()
                .stream()
                .filter(r -> r.getId_utilisateur() == clientId)
                .collect(Collectors.toList());

        table.setItems(FXCollections.observableArrayList(filteredReservations));
    }

    @FXML
    public void generateReceipt(ActionEvent actionEvent) {
        ReservationHotel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une réservation pour générer le reçu.");
            return;
        }

        generateReceiptButton.setDisable(true);

        try {
            Client client = clientService.getById(selected.getId_utilisateur());
            if (client == null) {
                showAlert("Erreur", "Client non trouvé pour cette réservation.");
                return;
            }

            Hotels hotel = hotelService.recuperer().get(selected.getHotel_id());
            if (hotel == null) {
                showAlert("Erreur", "Hôtel non trouvé pour cette réservation.");
                return;
            }

            String outputDir = "src/main/resources/output";
            new File(outputDir).mkdirs();

            String outputPath = outputDir + "/receipt_" + client.getNom() + "_" + selected.getId_reservation() + ".pdf";
            PdfWriter writer = new PdfWriter(outputPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            String imagePath = "/images/reciteReserv.png";
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl == null) {
                showAlert("Erreur", "Image non trouvée à : " + imagePath);
                return;
            }

            ImageData imageData = ImageDataFactory.create(imageUrl);
            Image img = new Image(imageData);
            pdf.setDefaultPageSize(new PageSize(img.getImageWidth(), img.getImageHeight()));
            document.setMargins(0, 0, 0, 0);

            PdfCanvas canvas = new PdfCanvas(pdf.addNewPage());
            PdfExtGState gs = new PdfExtGState().setFillOpacity(1.0f);
            canvas.saveState().setExtGState(gs);
            canvas.addImageAt(imageData, 0, 0, false);
            canvas.restoreState();

            DeviceRgb textColor = new DeviceRgb(0, 0, 0);
            float fontSize = 45f;
            float baseY = 860; // Adjusted to align with blue arrow
            float step = 40;
            float xPosition = 40; // Adjusted to align with blue arrow

            document.add(new Paragraph("Lead Guest Name: " + client.getNom() + " " + client.getPrenom())
                    .setFontSize(fontSize)
                    .setBold()
                    .setFontColor(textColor)
                    .setFixedPosition(xPosition, baseY, 700));

            document.add(new Paragraph("Hotel Name: " + hotel.getNom_hotel() + ", " + hotel.getDestination())
                    .setFontSize(fontSize)
                    .setBold()
                    .setFontColor(textColor)
                    .setFixedPosition(xPosition, baseY - step, 700));

            document.add(new Paragraph("Check-in Date: " + selected.getCheckin_date())
                    .setFontSize(fontSize)
                    .setBold()
                    .setFontColor(textColor)
                    .setFixedPosition(xPosition, baseY - 2 * step, 700));

            document.add(new Paragraph("Check-out Date: " + selected.getCheckout_date())
                    .setFontSize(fontSize)
                    .setBold()
                    .setFontColor(textColor)
                    .setFixedPosition(xPosition, baseY - 3 * step, 700));

            document.add(new Paragraph("Room Type: " + hotel.getType_chambre())
                    .setFontSize(fontSize)
                    .setBold()
                    .setFontColor(textColor)
                    .setFixedPosition(xPosition, baseY - 4 * step, 700));

            document.add(new Paragraph("Price per Night: " + String.format("%.2f", hotel.getPrix()) + " TND")
                    .setFontSize(fontSize)
                    .setBold()
                    .setFontColor(textColor)
                    .setFixedPosition(xPosition, baseY - 5 * step, 700));

            LocalDate checkin = LocalDate.parse(selected.getCheckin_date(), formatter);
            LocalDate checkout = LocalDate.parse(selected.getCheckout_date(), formatter);
            long nbNuits = ChronoUnit.DAYS.between(checkin, checkout);
            double totalPrice = hotel.getPrix() * nbNuits;
            document.add(new Paragraph("Total Price: " + String.format("%.2f", totalPrice) + " TND")
                    .setFontSize(fontSize)
                    .setBold()
                    .setFontColor(textColor)
                    .setFixedPosition(xPosition, baseY - 6 * step, 700));

            document.close();

            File pdfFile = new File(outputPath);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                showInfo("Succès", "Reçu généré avec succès : " + outputPath);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite : " + e.getMessage());
        } finally {
            generateReceiptButton.setDisable(false);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}