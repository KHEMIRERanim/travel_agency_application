package services;

import entities.ReservationVol;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservationVol implements IService<ReservationVol> {
    private Connection con;

    public ServiceReservationVol() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(ReservationVol res) throws SQLException {
        String req = "INSERT INTO reservation_vol (client_id, flight_id, passenger_name, reservation_date, status, price) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, res.getClient_id());
        ps.setInt(2, res.getFlight_id());
        ps.setString(3, res.getPassenger_name());
        ps.setDate(4, res.getReservationvol_date());
        ps.setString(5, res.getStatus());
        ps.setDouble(6, res.getPrice());

        ps.executeUpdate();
        System.out.println("Réservation ajoutée");
    }

    @Override
    public void modifier(ReservationVol res) throws SQLException {
        String req = "UPDATE reservation_vol SET client_id=?, flight_id=?, passenger_name=?, " +
                "reservation_date=?, status=?, price=? WHERE reservationvol_id=?";
        PreparedStatement ps = con.prepareStatement(req);

        ps.setInt(1, res.getClient_id());
        ps.setInt(2, res.getFlight_id());
        ps.setString(3, res.getPassenger_name());
        ps.setDate(4, res.getReservationvol_date());
        ps.setString(5, res.getStatus());
        ps.setDouble(6, res.getPrice());
        ps.setInt(7, res.getReservationvol_id());

        ps.executeUpdate();
        System.out.println("Réservation modifiée");
    }


    @Override
    public void supprimer(ReservationVol res) throws SQLException {
        String req = "DELETE FROM reservation_vol WHERE reservationvol_id=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, res.getReservationvol_id());
        ps.executeUpdate();
        System.out.println("Réservation supprimée");
    }

    @Override
    public List<ReservationVol> recuperer() throws SQLException {
        List<ReservationVol> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation_vol"; // Récupérer toutes les réservations de la table reservation
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int reservationvol_id = rs.getInt("reservationvol_id");
            int client_id = rs.getInt("client_id");
            int flight_id = rs.getInt("flight_id");
            String passenger_name = rs.getString("passenger_name");
            Date reservationvol_date = rs.getDate("reservation_date");
            String status = rs.getString("status");
            double price = rs.getDouble("price");

            // Création de l'objet ReservationVol avec les données récupérées
            ReservationVol reservation = new ReservationVol(reservationvol_id, client_id, status, reservationvol_date, price, passenger_name, flight_id);

            reservations.add(reservation); // Ajout de la réservation à la liste
        }

        return reservations;
    }
    // Nouvelle méthode pour récupérer les réservations d'un client spécifique
    public List<ReservationVol> recupererReservationsByClientId(int clientId) throws SQLException {
        List<ReservationVol> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation_vol WHERE client_id = ?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, clientId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int reservationvol_id = rs.getInt("reservationvol_id");
            int client_id = rs.getInt("client_id");
            int flight_id = rs.getInt("flight_id");
            String passenger_name = rs.getString("passenger_name");
            Date reservationvol_date = rs.getDate("reservation_date");
            String status = rs.getString("status");
            double price = rs.getDouble("price");

            ReservationVol reservation = new ReservationVol(reservationvol_id, client_id, status, reservationvol_date, price, passenger_name, flight_id);
            reservations.add(reservation);
        }

        return reservations;
    }
    public void updateReservationStatus(ReservationVol reservation, String newStatus) throws SQLException {
        String req = "UPDATE reservation_vol SET status = ? WHERE reservationvol_id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, newStatus);
            ps.setInt(2, reservation.getReservationvol_id());
            ps.executeUpdate();
        }
    }

}
