package entities;

public class Hotels {
    private int hotel_id;
    private String nom_hotel;
    private String destination;
    private double prix;
    private String type_chambre;
    private String status;
    private boolean wifi;
    private boolean piscine;
    private byte[] image;

    public Hotels(int hotel_id, String nom_hotel, String destination, double prix, String type_chambre, String status, boolean wifi, boolean piscine) {
        this.hotel_id = hotel_id;
        this.nom_hotel = nom_hotel;
        this.destination = destination;
        this.prix = prix;
        this.type_chambre = type_chambre;
        this.status = status;
        this.wifi = wifi;
        this.piscine = piscine;
    }

    public Hotels(String nom_hotel, String destination, double prix, String type_chambre, String status, boolean wifi, boolean piscine) {
        this.nom_hotel = nom_hotel;
        this.destination = destination;
        this.prix = prix;
        this.type_chambre = type_chambre;
        this.status = status;
        this.wifi = wifi;
        this.piscine = piscine;
    }


    public Hotels(int hotel_id, String nom_hotel, String destination, double prix, String type_chambre, String status, boolean wifi, boolean piscine, byte[] image) {
        this(hotel_id, nom_hotel, destination, prix, type_chambre, status, wifi, piscine);
        this.image = image;
    }

    public Hotels(String nom_hotel, String destination, double prix, String type_chambre, String status, boolean wifi, boolean piscine, byte[] image) {
        this(nom_hotel, destination, prix, type_chambre, status, wifi, piscine);
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getHotel_id() { return hotel_id; }
    public void setHotel_id(int hotel_id) { this.hotel_id = hotel_id; }

    public String getNom_hotel() { return nom_hotel; }
    public void setNom_hotel(String nom_hotel) { this.nom_hotel = nom_hotel; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public String getType_chambre() { return type_chambre; }
    public void setType_chambre(String type_chambre) { this.type_chambre = type_chambre; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isWifi() { return wifi; }
    public void setWifi(boolean wifi) { this.wifi = wifi; }

    public boolean isPiscine() { return piscine; }
    public void setPiscine(boolean piscine) { this.piscine = piscine; }

    @Override
    public String toString() {
        return "Hotel{" +
                "nom_hotel='" + nom_hotel + '\'' +
                ", destination='" + destination + '\'' +
                ", prix=" + prix +
                ", type_chambre='" + type_chambre + '\'' +
                ", status='" + status + '\'' +
                ", wifi=" + wifi +
                ", piscine=" + piscine +
                '}';
    }
}
