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
        String req = "INSERT INTO reservation_vol (client_id, flight_id, passenger_name, reservation_date, status, price, selected_seats) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, res.getClient_id());
            ps.setInt(2, res.getFlight_id());
            ps.setString(3, res.getPassenger_name());
            ps.setDate(4, res.getReservationvol_date());
            ps.setString(5, res.getStatus());
            ps.setDouble(6, res.getPrice());
            ps.setString(7, res.getSelectedSeats());
            ps.executeUpdate();
        }
    }

    @Override
    public void modifier(ReservationVol res) throws SQLException {
        String req = "UPDATE reservation_vol SET client_id=?, flight_id=?, passenger_name=?, reservation_date=?, status=?, price=?, selected_seats=? WHERE reservationvol_id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, res.getClient_id());
            ps.setInt(2, res.getFlight_id());
            ps.setString(3, res.getPassenger_name());
            ps.setDate(4, res.getReservationvol_date());
            ps.setString(5, res.getStatus());
            ps.setDouble(6, res.getPrice());
            ps.setString(7, res.getSelectedSeats());
            ps.setInt(8, res.getReservationvol_id());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(ReservationVol res) throws SQLException {
        String req = "DELETE FROM reservation_vol WHERE reservationvol_id=?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, res.getReservationvol_id());
            ps.executeUpdate();
        }
    }

    @Override
    public List<ReservationVol> recuperer() throws SQLException {
        List<ReservationVol> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation_vol";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                ReservationVol reservation = new ReservationVol(
                        rs.getInt("reservationvol_id"),
                        rs.getInt("client_id"),
                        rs.getString("status"),
                        rs.getDate("reservation_date"),
                        rs.getDouble("price"),
                        rs.getString("passenger_name"),
                        rs.getInt("flight_id"),
                        rs.getString("selected_seats") != null ? rs.getString("selected_seats") : ""
                );
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    public List<ReservationVol> recupererReservationsByClientId(int clientId) throws SQLException {
        List<ReservationVol> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation_vol WHERE client_id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReservationVol reservation = new ReservationVol(
                            rs.getInt("reservationvol_id"),
                            rs.getInt("client_id"),
                            rs.getString("status"),
                            rs.getDate("reservation_date"),
                            rs.getDouble("price"),
                            rs.getString("passenger_name"),
                            rs.getInt("flight_id"),
                            rs.getString("selected_seats") != null ? rs.getString("selected_seats") : ""
                    );
                    reservations.add(reservation);
                }
            }
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