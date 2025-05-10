package entities;

import java.time.LocalDate;

public class ReservationVehicule {
    private int id;
    private int id_vehicule;
    private int id_client;
    private String lieu_prise;
    private String lieu_retour;
    private LocalDate date_location;
    private LocalDate date_retour;
    private String clientEmail;

    public ReservationVehicule() {}

    public ReservationVehicule(int id, int id_vehicule, int id_client, String lieu_prise, String lieu_retour,
                               LocalDate date_location, LocalDate date_retour) {
        this.id = id;
        this.id_vehicule = id_vehicule;
        this.id_client = id_client;
        this.lieu_prise = lieu_prise;
        this.lieu_retour = lieu_retour;
        this.date_location = date_location;
        this.date_retour = date_retour;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getId_vehicule() { return id_vehicule; }
    public void setId_vehicule(int id_vehicule) { this.id_vehicule = id_vehicule; }
    public int getId_client() { return id_client; }
    public void setId_client(int id_client) { this.id_client = id_client; }
    public String getLieu_prise() { return lieu_prise; }
    public void setLieu_prise(String lieu_prise) { this.lieu_prise = lieu_prise; }
    public String getLieu_retour() { return lieu_retour; }
    public void setLieu_retour(String lieu_retour) { this.lieu_retour = lieu_retour; }
    public LocalDate getDate_location() { return date_location; }
    public void setDate_location(LocalDate date_location) { this.date_location = date_location; }
    public LocalDate getDate_retour() { return date_retour; }
    public void setDate_retour(LocalDate date_retour) { this.date_retour = date_retour; }
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }
}