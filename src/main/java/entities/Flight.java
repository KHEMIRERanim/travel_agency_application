package entities;
import java.sql.Timestamp;
import java.sql.Date;

public class Flight {
    private int flight_id,flight_duration,available_seats;
    private String departure,destination ,airline,flight_number, image_url;
    private Timestamp arrival_Time, departure_Time;
    private Date flight_date;
    private double price;

    public Flight(int flight_id, int flight_duration, String flight_number, int available_seats,
                  String departure, String destination, String airline,
                  Timestamp arrival_Time, Timestamp departure_Time, Date flight_date, double price, String image_url) {
        this.flight_id = flight_id;
        this.flight_duration = flight_duration;
        this.flight_number = flight_number;
        this.available_seats = available_seats;
        this.departure = departure;
        this.destination = destination;
        this.airline = airline;
        this.arrival_Time = arrival_Time;
        this.departure_Time = departure_Time;
        this.flight_date = flight_date;
        this.price = price;
        this.image_url = image_url;
    }

    public Flight(int flight_duration, String flight_number, int available_seats,
                  String departure, String destination, String airline,
                  Timestamp arrival_Time, Timestamp departure_Time, Date flight_date, double price, String image_url) {
        this.flight_duration = flight_duration;
        this.flight_number = flight_number;
        this.available_seats = available_seats;
        this.departure = departure;
        this.destination = destination;
        this.airline = airline;
        this.arrival_Time = arrival_Time;
        this.departure_Time = departure_Time;
        this.flight_date = flight_date;
        this.price = price;
        this.image_url = image_url;
    }

    public int getFlight_id() {
        return flight_id;
    }

    public void setFlight_id(int flight_id) {
        this.flight_id = flight_id;
    }

    public String getFlight_number() {
        return flight_number;
    }

    public void setFlight_number(String flight_number) {
        this.flight_number = flight_number;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public int getFlight_duration() {
        return flight_duration;
    }

    public void setFlight_duration(int flight_duration) {
        this.flight_duration = flight_duration;
    }

    public int getAvailable_seats() {
        return available_seats;
    }

    public void setAvailable_seats(int available_seats) {
        this.available_seats = available_seats;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public Timestamp getArrival_Time() {
        return arrival_Time;
    }

    public void setArrival_Time(Timestamp arrival_Time) {
        this.arrival_Time = arrival_Time;
    }

    public Timestamp getDeparture_Time() {
        return departure_Time;
    }

    public void setDeparture_Time(Timestamp departure_Time) {
        this.departure_Time = departure_Time;
    }

    public Date getFlight_date() {
        return flight_date;
    }

    public void setFlight_date(Date flight_date) {
        this.flight_date = flight_date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flight_id=" + flight_id +
                ", flight_duration=" + flight_duration +
                ", available_seats=" + available_seats +
                ", departure='" + departure + '\'' +
                ", destination='" + destination + '\'' +
                ", airline='" + airline + '\'' +
                ", flight_number='" + flight_number + '\'' +
                ", arrival_Time=" + arrival_Time +
                ", departure_Time=" + departure_Time +
                ", flight_date=" + flight_date +
                ", price=" + price +
                ", image_url='" + image_url + '\'' +
                '}';
    }
}