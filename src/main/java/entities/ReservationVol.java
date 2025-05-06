package entities;
import java.sql.Date;
public class ReservationVol {
    private int reservationvol_id,client_id,flight_id;
    private String status,passenger_name;
    private Date reservationvol_date;
    private Double price;

    public ReservationVol(int client_id, String status, Date reservationvol_date, Double price, String passenger_name,int flight_id ) {
        this.client_id = client_id;
        this.status = status;
        this.reservationvol_date = reservationvol_date;
        this.price = price;
        this.passenger_name = passenger_name;
        this.flight_id = flight_id;
    }
    public ReservationVol(int reservationvol_id, int client_id, String status, Date reservationvol_date, Double price, String passenger_name,int flight_id ) {
        this.reservationvol_id = reservationvol_id;
        this.client_id = client_id;
        this.status = status;
        this.reservationvol_date = reservationvol_date;
        this.price = price;
        this.passenger_name = passenger_name;
        this.flight_id = flight_id;
    }

    public int getReservationvol_id() {
        return reservationvol_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public int getFlight_id() {
        return flight_id;
    }

    public String getStatus() {
        return status;
    }

    public String getPassenger_name() {
        return passenger_name;
    }

    public Date getReservationvol_date() {
        return reservationvol_date;
    }

    public Double getPrice() {
        return price;
    }

    public void setReservationvol_id(int reservationvol_id) {
        this.reservationvol_id = reservationvol_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public void setFlight_id(int flight_id) {
        this.flight_id = flight_id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPassenger_name(String passenger_name) {
        this.passenger_name = passenger_name;
    }

    public void setReservationvol_date(Date reservationvol_date) {
        this.reservationvol_date = reservationvol_date;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ReservationVol{" +
                "reservationvol_id=" + reservationvol_id +
                ", client_id=" + client_id +
                ", flight_id=" + flight_id +
                ", status='" + status + '\'' +
                ", passenger_name='" + passenger_name + '\'' +
                ", reservationvol_date=" + reservationvol_date +
                ", price=" + price +
                '}';
    }
}