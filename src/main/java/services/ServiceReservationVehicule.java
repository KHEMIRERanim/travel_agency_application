package services;

import entities.ReservationVehicule;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservationVehicule {
    private final Connection conn;

    public ServiceReservationVehicule() {
        this.conn = MyDatabase.getInstance().getCnx();
    }

    public void ajouter(ReservationVehicule reservation) throws SQLException {
        String query = "INSERT INTO reservation_vehicule (id_vehicule, id_client, lieu_prise, lieu_retour, date_location, date_retour) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reservation.getIdVehicule());
            stmt.setObject(2, reservation.getIdClient(), Types.INTEGER);
            stmt.setString(3, reservation.getLieuPrise());
            stmt.setString(4, reservation.getLieuRetour());
            stmt.setDate(5, Date.valueOf(reservation.getDateLocation()));
            stmt.setDate(6, Date.valueOf(reservation.getDateRetour()));
            stmt.executeUpdate();
            System.out.println("Reservation added for vehicle ID: " + reservation.getIdVehicule());
        }
    }

    public List<ReservationVehicule> getAll() throws SQLException {
        List<ReservationVehicule> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation_vehicule";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ReservationVehicule r = new ReservationVehicule();
                r.setId(rs.getInt("id_reservation"));
                r.setIdVehicule(rs.getInt("id_vehicule"));
                r.setIdClient(rs.getInt("id_client") != 0 ? rs.getInt("id_client") : null);
                r.setLieuPrise(rs.getString("lieu_prise"));
                r.setLieuRetour(rs.getString("lieu_retour"));
                r.setDateLocation(rs.getDate("date_location").toLocalDate());
                r.setDateRetour(rs.getDate("date_retour").toLocalDate());
                reservations.add(r);
            }
            System.out.println("Retrieved " + reservations.size() + " reservations.");
        }
        return reservations;
    }

    public void modifier(ReservationVehicule reservation) throws SQLException {
        String query = "UPDATE reservation_vehicule SET id_vehicule = ?, id_client = ?, lieu_prise = ?, lieu_retour = ?, date_location = ?, date_retour = ? WHERE id_reservation = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reservation.getIdVehicule());
            stmt.setObject(2, reservation.getIdClient(), Types.INTEGER);
            stmt.setString(3, reservation.getLieuPrise());
            stmt.setString(4, reservation.getLieuRetour());
            stmt.setDate(5, Date.valueOf(reservation.getDateLocation()));
            stmt.setDate(6, Date.valueOf(reservation.getDateRetour()));
            stmt.setInt(7, reservation.getId());
            stmt.executeUpdate();
            System.out.println("Reservation updated: ID " + reservation.getId());
        }
    }

    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM reservation_vehicule WHERE id_reservation = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Reservation deleted: ID " + id);
        }
    }
}