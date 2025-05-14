package entities;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReservationVehicule {
    private int id;
    private int vehiculeId;
    private int clientId;
    private String lieuPrise;
    private String lieuRetour;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String clientEmail;

    public ReservationVehicule() {}

    public ReservationVehicule(int id, int vehiculeId, int clientId, String lieuPrise, String lieuRetour,
                               LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.vehiculeId = vehiculeId;
        this.clientId = clientId;
        this.lieuPrise = lieuPrise;
        this.lieuRetour = lieuRetour;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(int vehiculeId) { this.vehiculeId = vehiculeId; }
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    public String getLieuPrise() { return lieuPrise; }
    public void setLieuPrise(String lieuPrise) { this.lieuPrise = lieuPrise; }
    public String getLieuRetour() { return lieuRetour; }
    public void setLieuRetour(String lieuRetour) { this.lieuRetour = lieuRetour; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }
}