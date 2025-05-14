package entities;

import java.time.LocalDate;

public class Vehicule {
    private int id;
    private String type;
    private String lieuPrise;
    private String lieuRetour;
    private LocalDate dateLocation;
    private LocalDate dateRetour;
    private String imagePath;
    private double prix;

    public Vehicule() {}

    public Vehicule(int id, String type, String lieuPrise, String lieuRetour, LocalDate dateLocation, LocalDate dateRetour, String imagePath, double prix) {
        this.id = id;
        this.type = type;
        this.lieuPrise = lieuPrise;
        this.lieuRetour = lieuRetour;
        this.dateLocation = dateLocation;
        this.dateRetour = dateRetour;
        this.imagePath = imagePath;
        this.prix = prix;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLieuPrise() { return lieuPrise; }
    public void setLieuPrise(String lieuPrise) { this.lieuPrise = lieuPrise; }
    public String getLieuRetour() { return lieuRetour; }
    public void setLieuRetour(String lieuRetour) { this.lieuRetour = lieuRetour; }
    public LocalDate getDateLocation() { return dateLocation; }
    public void setDateLocation(LocalDate dateLocation) { this.dateLocation = dateLocation; }
    public LocalDate getDateRetour() { return dateRetour; }
    public void setDateRetour(LocalDate dateRetour) { this.dateRetour = dateRetour; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    @Override
    public String toString() {
        return type;
    }
}