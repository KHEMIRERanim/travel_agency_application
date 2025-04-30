package services;

import entities.Flight;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceFlight implements IService<Flight> {
    private Connection con;

    public ServiceFlight() {
        con = MyDatabase.getInstance().getCnx();
        if (con == null) {
            System.out.println("La connexion est nulle !");
        } else {
            System.out.println("Connexion établie !");
        }
    }

    @Override
    public void ajouter(Flight flight) throws SQLException {
        String req = "INSERT INTO flight (departure, destination, departure_time, arrival_time, flight_date, flight_duration, " +
                "flight_number, airline, available_seats, price, image_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        PreparedStatement ps = con.prepareStatement(req);

        // Remplir les paramètres dans le bon ordre
        ps.setString(1, flight.getDeparture());
        ps.setString(2, flight.getDestination());
        ps.setTimestamp(3, flight.getDeparture_Time());
        ps.setTimestamp(4, flight.getArrival_Time());
        ps.setDate(5, flight.getFlight_date());
        ps.setInt(6, flight.getFlight_duration());
        ps.setString(7, flight.getFlight_number());
        ps.setString(8, flight.getAirline());
        ps.setInt(9, flight.getAvailable_seats());
        ps.setDouble(10, flight.getPrice());
        ps.setString(11, flight.getImage_url());

        ps.executeUpdate();
        System.out.println("Vol ajouté");
    }

    @Override
    public void modifier(Flight flight) throws SQLException {
        String req = "UPDATE flight SET departure=?, destination=?, departure_time=?, arrival_time=?, " +
                "flight_date=?, flight_duration=?, flight_number=?, airline=?, available_seats=?, price=?, image_url=? " +
                "WHERE flight_id=?";
        PreparedStatement ps = con.prepareStatement(req);

        ps.setString(1, flight.getDeparture());
        ps.setString(2, flight.getDestination());
        ps.setTimestamp(3, flight.getDeparture_Time());
        ps.setTimestamp(4, flight.getArrival_Time());
        ps.setDate(5, flight.getFlight_date());
        ps.setInt(6, flight.getFlight_duration());
        ps.setString(7, flight.getFlight_number());
        ps.setString(8, flight.getAirline());
        ps.setInt(9, flight.getAvailable_seats());
        ps.setDouble(10, flight.getPrice());
        ps.setString(11, flight.getImage_url());
        ps.setInt(12, flight.getFlight_id());

        ps.executeUpdate();
        System.out.println("Vol modifié");
    }

    @Override
    public void supprimer(Flight flight) throws SQLException {
        String req = "DELETE FROM flight WHERE flight_id=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, flight.getFlight_id());
        ps.executeUpdate();
        System.out.println("Vol supprimé");
    }

    @Override
    public List<Flight> recuperer() throws SQLException {
        List<Flight> flights = new ArrayList<>();

        String req = "SELECT * FROM flight";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int flight_id = rs.getInt("flight_id");
            String departure = rs.getString("departure");
            String destination = rs.getString("destination");
            Timestamp departure_time = rs.getTimestamp("departure_time");
            Timestamp arrival_time = rs.getTimestamp("arrival_time");
            Date flight_date = rs.getDate("flight_date");
            int flight_duration = rs.getInt("flight_duration");
            String flight_number = rs.getString("flight_number");
            String airline = rs.getString("airline");
            int available_seats = rs.getInt("available_seats");
            double price = rs.getDouble("price");
            String image_url = rs.getString("image_url");

            // Création de l'objet Flight avec les données récupérées
            Flight flight = new Flight(flight_id, flight_duration, flight_number, available_seats,
                    departure, destination, airline, arrival_time, departure_time, flight_date, price, image_url);

            flights.add(flight);
        }

        return flights;
    }
}