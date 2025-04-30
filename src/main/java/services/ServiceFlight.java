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
        } else {            System.out.println("Connexion établie !");
        }

    }
    @Override
    public void ajouter(Flight flight) throws SQLException {
        String req = "INSERT INTO flight (departure, destination, departure_time, arrival_time, flight_date, flight_duration, flight_number, airline, available_seats, price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        PreparedStatement ps = con.prepareStatement(req);

        // Remplir les paramètres dans le bon ordre, sans flight_id car c'est auto-incrémenté
        ps.setString(1, flight.getDeparture());         // departure
        ps.setString(2, flight.getDestination());        // destination
        ps.setTimestamp(3, flight.getDeparture_Time());   // departure_time
        ps.setTimestamp(4, flight.getArrival_Time());     // arrival_time
        ps.setDate(5, flight.getFlight_date());           // flight_date
        ps.setInt(6, flight.getFlight_duration());        // flight_duration
        ps.setString(7, flight.getFlight_number());       // flight_number
        ps.setString(8, flight.getAirline());            // airline
        ps.setInt(9, flight.getAvailable_seats());        // available_seats
        ps.setDouble(10, flight.getPrice());             // price

        ps.executeUpdate();
        System.out.println("Vol ajouté");
    }


    @Override
    public void modifier(Flight flight) throws SQLException {
        String req = "UPDATE flight SET departure=?, destination=?, departure_time=?, arrival_time=?, " +
                "flight_date=?, flight_duration=?, flight_number=?, airline=?, available_seats=?, price=? " +
                "WHERE flight_id=?";
        PreparedStatement ps = con.prepareStatement(req);

        // Respecter l'ordre : departure, destination, departure_time, arrival_time,
        // flight_date, flight_duration, flight_number, airline, available_seats, price
        ps.setString(1, flight.getDeparture());
        ps.setString(2, flight.getDestination());
        ps.setTimestamp(3, flight.getDeparture_Time());
        ps.setTimestamp(4, flight.getArrival_Time());
        ps.setDate(5, new java.sql.Date(flight.getFlight_date().getTime()));
        ps.setInt(6, flight.getFlight_duration());
        ps.setString(7, flight.getFlight_number());
        ps.setString(8, flight.getAirline());
        ps.setInt(9, flight.getAvailable_seats());
        ps.setDouble(10, flight.getPrice());
        ps.setInt(11, flight.getFlight_id()); // WHERE condition

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

        String req = "SELECT * FROM flight";  // Récupérer tous les vols de la table flight
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

            // Création de l'objet Flight avec les données récupérées
            Flight flight = new Flight(flight_id, flight_duration, flight_number, available_seats,
                    departure, destination, airline, departure_time, arrival_time, flight_date, price);

            flights.add(flight); // Ajout du vol à la liste
        }

        return flights;  // Retourne tous les vols
    }





    public List<Flight> searchFlights(String departure, String destination, Date flightDate, int numberOfAdults, int numberOfChildren) throws SQLException {
        List<Flight> flights = new ArrayList<>();
        // Nombre total de passagers
        int totalPassengers = numberOfAdults + numberOfChildren;

        // Requête SQL modifiée pour inclure departure, destination, flight_date et available_seats
        String query = "SELECT * FROM flight " +
                "WHERE departure = ? " +
                "AND destination = ? " +
                "AND flight_date = ? " +
                "AND available_seats >= ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            // Paramètres de la requête
            ps.setString(1, departure);  // lieu de départ
            ps.setString(2, destination); // destination
            ps.setDate(3, flightDate);    // date du vol
            ps.setInt(4, totalPassengers); // nombre total de passagers

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Créer un objet vol (Flight) et l'ajouter à la liste
                    Flight flight = new Flight(
                            rs.getInt("flight_id"),
                            rs.getInt("flight_duration"),
                            rs.getString("flight_number"),
                            rs.getInt("available_seats"),
                            rs.getString("departure"),
                            rs.getString("destination"),
                            rs.getString("airline"),
                            rs.getTimestamp("departure_time"),
                            rs.getTimestamp("arrival_time"),
                            rs.getDate("flight_date"),
                            rs.getDouble("price")
                    );
                    flights.add(flight);
                }
            }
        }
        return flights;
    }




}
