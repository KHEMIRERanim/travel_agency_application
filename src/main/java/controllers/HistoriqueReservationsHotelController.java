package controllers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;

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

    @FXML
    private TableView<ReservationHotel> table;
    @FXML
    private TableColumn<ReservationHotel, String> hotelNameCol;
    @FXML
    private TableColumn<ReservationHotel, String> hotelPriceCol;
    @FXML
    private TableColumn<ReservationHotel, String> checkinCol;
    @FXML
    private TableColumn<ReservationHotel, String> checkoutCol;
    @FXML
    private TableColumn<ReservationHotel, String> colPrixTotal;
    @FXML
    private TableColumn<ReservationHotel, String> statusCol;
    @FXML
    private Button generateReceiptButton;

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

        // Disable the button to prevent double clicks
        generateReceiptButton.setDisable(true);

        try {
            // Fetch client and hotel details
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

            // Define the PDF output path
            String outputDir = "src/main/resources/output";
            new File(outputDir).mkdirs();
            String outputPath = outputDir + "/receipt_" + client.getNom() + "_" + selected.getId_reservation() + ".pdf";
            PdfWriter writer = new PdfWriter(new File(outputPath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Load the Canva design PNG from the absolute path
            String imagePath = "/images/reciteReserv.png";
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl == null) {
                showAlert("Erreur", "Le fichier image n'a pas été trouvé à : " + imagePath +
                        ". Vérifiez que reciteReserv.png est dans src/main/resources/images/ et que le chemin est correct.");
                return;
            }
            ImageData imageData = ImageDataFactory.create(imageUrl);

            Image img = new Image(imageData);
            pdf.setDefaultPageSize(new PageSize(img.getImageWidth(), img.getImageHeight()));
            document.setMargins(0, 0, 0, 0);

            // Print image dimensions for debugging
            System.out.println("Image dimensions: " + img.getImageWidth() + "x" + img.getImageHeight());

            // Add the image with full opacity
            PdfCanvas canvas = new PdfCanvas(pdf.addNewPage());
            PdfExtGState gs = new PdfExtGState();
            gs.setFillOpacity(1.0f); // Full opacity for the background image
            canvas.saveState().setExtGState(gs);
            canvas.addImageAt(imageData, 0, 0, false);
            canvas.restoreState();

            // Define styling for text fields
            DeviceRgb textColor = new DeviceRgb(0, 0, 0); // Black text
            DeviceRgb backgroundColor = new DeviceRgb(255, 255, 255); // White background with slight transparency
            float fontSize = 14; // Font size for readability

            // Positioning for the fields (estimated based on image with height 1000 pixels)
            // Adjust these values if the actual imageHeight differs: pdfY = imageHeight - imageY
            // Coordinates align text with the non-empty parts (e.g., "Smith John") in the fields

            // Lead Guest Name
            document.add(new Paragraph("Lead Guest Name: " + client.getNom() + " " + client.getPrenom())
                    .setFontSize(fontSize)
                    .setBold()
                    .setFontColor(textColor)
                    .setBackgroundColor(backgroundColor)
                    .setFixedPosition(150, 800, 300)); // Estimated: (150, 200) -> 1000 - 200 = 800

            // Room Type
            String roomType = "Standard Room"; // Adjust if you can fetch this from Hotels
            document.add(new Paragraph("Room Type: " + roomType)
                    .setFontSize(fontSize)
                    .setBold()
                    .setFontColor(textColor)
                    .setBackgroundColor(backgroundColor)
                    .setFixedPosition(150, 750, 300)); // Estimated: (150, 250) -> 1000 - 250 = 750

            // Check-in Date
            document.add(new Paragraph("Check-in Date: " + selected.getCheckin_date())
                    .setFontSize(fontSize)
                    .setBold()
                    .setFontColor(textColor)
                    .setBackgroundColor(backgroundColor)
                    .setFixedPosition(150, 700, 300)); // Estimated: (150, 300) -> 1000 - 300 = 700

            // Check-out Date
            document.add(new Paragraph("Check-out Date: " + selected.getCheckout_date())
                    .setFontSize(fontSize)
                    .setBold()
                    .setFontColor(textColor)
                    .setBackgroundColor(backgroundColor)
                    .setFixedPosition(150, 650, 300)); // Estimated: (150, 350) -> 1000 - 350 = 650

            // Close the document
            document.close();
            System.out.println("Receipt generated at: " + outputPath);

            // Open the PDF in the default PDF viewer
            File pdfFile = new File(outputPath);
            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    showAlert("Erreur", "L'ouverture automatique du PDF n'est pas supportée sur ce système. " +
                            "Veuillez ouvrir manuellement le fichier à : " + outputPath);
                }
            } else {
                showAlert("Erreur", "Le fichier PDF n'a pas été généré correctement à : " + outputPath);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la génération du reçu : " + e.getMessage());
        } finally {
            // Re-enable the button after processing
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
}