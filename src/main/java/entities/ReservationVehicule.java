package entities;

import java.time.LocalDate;

public class ReservationVehicule {
    private int id;
    private int idVehicule;
    private Integer idClient;
    private String lieuPrise;
    private String lieuRetour;
    private LocalDate dateLocation;
    private LocalDate dateRetour;

    public ReservationVehicule() {}

    public ReservationVehicule(int id, int idVehicule, Integer idClient, String lieuPrise, String lieuRetour, LocalDate dateLocation, LocalDate dateRetour) {
        this.id = id;
        this.idVehicule = idVehicule;
        this.idClient = idClient;
        this.lieuPrise = lieuPrise;
        this.lieuRetour = lieuRetour;
        this.dateLocation = dateLocation;
        this.dateRetour = dateRetour;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdVehicule() { return idVehicule; }
    public void setIdVehicule(int idVehicule) { this.idVehicule = idVehicule; }
    public Integer getIdClient() { return idClient; }
    public void setIdClient(Integer idClient) { this.idClient = idClient; }
    public String getLieuPrise() { return lieuPrise; }
    public void setLieuPrise(String lieuPrise) { this.lieuPrise = lieuPrise; }
    public String getLieuRetour() { return lieuRetour; }
    public void setLieuRetour(String lieuRetour) { this.lieuRetour = lieuRetour; }
    public LocalDate getDateLocation() { return dateLocation; }
    public void setDateLocation(LocalDate dateLocation) { this.dateLocation = dateLocation; }
    public LocalDate getDateRetour() { return dateRetour; }
    public void setDateRetour(LocalDate dateRetour) { this.dateRetour = dateRetour; }
}