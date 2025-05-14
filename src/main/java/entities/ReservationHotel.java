package entities;

public class ReservationHotel {
    private int id_reservation;
    private int hotel_id;
    private int id_utilisateur;
    private String checkin_date;
    private String checkout_date;

    public ReservationHotel(int id_reservation, int hotel_id, int id_utilisateur, String checkin_date, String checkout_date) {
        this.id_reservation =  id_reservation;
        this.hotel_id = hotel_id;
        this.id_utilisateur = id_utilisateur;
        this.checkin_date = checkin_date;
        this.checkout_date = checkout_date;

    }
    public ReservationHotel(int hotel_id, int id_utilisateur, String checkin_date, String checkout_date) {
        this.hotel_id = hotel_id;
        this.id_utilisateur = id_utilisateur;
        this.checkin_date = checkin_date;
        this.checkout_date = checkout_date;

    }

    public int getId_reservation() {
        return id_reservation;
    }

    public void setId_reservation(int id_reservation) {
        this.id_reservation = id_reservation;
    }

    public int getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(int hotel_id) {
        this.hotel_id = hotel_id;
    }

    public int getId_utilisateur() {
        return id_utilisateur;
    }

    public void setId_utilisateur(int id_utilisateur) {
        this.id_utilisateur = id_utilisateur;
    }

    public String getCheckin_date() {
        return checkin_date;
    }

    public void setCheckin_date(String checkin_date) {
        this.checkin_date = checkin_date;
    }

    public String getCheckout_date() {
        return checkout_date;
    }

    public void setCheckout_date(String checkout_date) {
        this.checkout_date = checkout_date;
    }

    @Override
    public String toString() {
        return "ReservationHotel{" +
                "id_reservation=" + id_reservation +
                ", hotel_id=" + hotel_id +
                ", id_utilisateur=" + id_utilisateur +
                ", checkin_date='" + checkin_date + '\'' +
                ", checkout_date='" + checkout_date + '\'' +
                '}';
    }

}
