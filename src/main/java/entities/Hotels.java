package entities;

import java.time.LocalDate;

public class Hotels {
    private int hotel_id;
    private String nom_hotel;
    private String destination;
    private String check_in;
    private String check_out;
    private double prix;
    private String type_chambre;
    private String status;

    // Constructeur avec id
    public Hotels(int hotel_id, String nom_hotel, String destination, String check_in, String check_out, double prix, String type_chambre, String status) {
        this.hotel_id = hotel_id;
        this.nom_hotel = nom_hotel;
        this.destination = destination;
        this.check_in = check_in;
        this.check_out = check_out;
        this.prix = prix;
        this.type_chambre = type_chambre;
        this.status = status;

    }
    // Constructeur sans id
    public Hotels(String nom_hotel, String destination, String check_in, String check_out, double prix, String type_chambre, String status){
        this.nom_hotel = nom_hotel;
        this.destination = destination;
        this.check_in = check_in;
        this.check_out = check_out;
        this.prix = prix;
        this.type_chambre = type_chambre;
        this.status = status;

    }

    public Hotels(String hotelName, String destination, LocalDate value, LocalDate value1, double price, String roomType, String status) {
    }

    public int getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(int hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getNom_hotel() {
        return nom_hotel;
    }

    public void setNom_hotel(String nom_hotel) {
        this.nom_hotel = nom_hotel;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCheck_in() {
        return check_in;
    }

    public void setCheck_in(String check_in) {
        this.check_in = check_in;
    }

    public String getCheck_out() {
        return check_out;
    }

    public void setCheck_out(String check_out) {
        this.check_out = check_out;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getType_chambre() {
        return type_chambre;
    }

    public void setType_chambre(String type_chambre) {
        this.type_chambre = type_chambre;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                ", nom_hotel='" + nom_hotel + '\'' +
                ", destination='" + destination + '\'' +
                ", check_in='" + check_in + '\'' +
                ", check_out='" + check_out + '\'' +
                ", prix=" + prix +
                ", type_chambre='" + type_chambre + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
