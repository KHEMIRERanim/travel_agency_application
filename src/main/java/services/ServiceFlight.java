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

    public Flight addRealTimeFlight(Flight flight) throws SQLException {
        String req = "INSERT INTO flight (departure, destination, departure_time, arrival_time, flight_date, flight_duration, " +
                "flight_number, airline, available_seats, price, image_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);

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
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            flight.setFlight_id(rs.getInt(1));
        }
        System.out.println("Vol en temps réel ajouté");
        return flight;
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
            Flight flight = new Flight(
                    rs.getInt("flight_id"),
                    rs.getInt("flight_duration"),
                    rs.getString("flight_number"),
                    rs.getInt("available_seats"),
                    rs.getString("departure"),
                    rs.getString("destination"),
                    rs.getString("airline"),
                    rs.getTimestamp("arrival_time"),
                    rs.getTimestamp("departure_time"),
                    rs.getDate("flight_date"),
                    rs.getDouble("price"),
                    rs.getString("image_url")
            );
            flights.add(flight);
        }
        return flights;
    }

    public Flight getFlightById(int flightId) throws SQLException {
        String req = "SELECT * FROM flight WHERE flight_id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, flightId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Flight(
                        rs.getInt("flight_id"),
                        rs.getInt("flight_duration"),
                        rs.getString("flight_number"),
                        rs.getInt("available_seats"),
                        rs.getString("departure"),
                        rs.getString("destination"),
                        rs.getString("airline"),
                        rs.getTimestamp("arrival_time"),
                        rs.getTimestamp("departure_time"),
                        rs.getDate("flight_date"),
                        rs.getDouble("price"),
                        rs.getString("image_url")
                );
            }
        }
        return null;
    }
    // New method to find flight by flight number
    public Flight findByFlightNumber(String flightNumber) throws SQLException {
        String req = "SELECT * FROM flight WHERE flight_number = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, flightNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Flight(
                        rs.getInt("flight_id"),
                        rs.getInt("flight_duration"),
                        rs.getString("flight_number"),
                        rs.getInt("available_seats"),
                        rs.getString("departure"),
                        rs.getString("destination"),
                        rs.getString("airline"),
                        rs.getTimestamp("arrival_time"),
                        rs.getTimestamp("departure_time"),
                        rs.getDate("flight_date"),
                        rs.getDouble("price"),
                        rs.getString("image_url")
                );
            }
        }
        return null;
    }
}