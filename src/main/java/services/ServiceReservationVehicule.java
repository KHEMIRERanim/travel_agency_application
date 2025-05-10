package services;

import entities.ReservationVehicule;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservationVehicule implements IService<ReservationVehicule> {
    private Connection con;

    public ServiceReservationVehicule() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(ReservationVehicule reservation) throws SQLException {
        String req = "INSERT INTO reservation_vehicule (id_vehicule, id_client, lieu_prise, lieu_retour, date_location, date_retour) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, reservation.getId_vehicule());
        ps.setInt(2, reservation.getId_client());
        ps.setString(3, reservation.getLieu_prise());
        ps.setString(4, reservation.getLieu_retour());
        ps.setDate(5, Date.valueOf(reservation.getDate_location()));
        ps.setDate(6, Date.valueOf(reservation.getDate_retour()));
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            reservation.setId(rs.getInt(1));
        }
    }

    @Override
    public void modifier(ReservationVehicule reservation) throws SQLException {
        String req = "UPDATE reservation_vehicule SET id_vehicule=?, id_client=?, lieu_prise=?, lieu_retour=?, date_location=?, date_retour=? WHERE id_reservation=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getId_vehicule());
        ps.setInt(2, reservation.getId_client());
        ps.setString(3, reservation.getLieu_prise());
        ps.setString(4, reservation.getLieu_retour());
        ps.setDate(5, Date.valueOf(reservation.getDate_location()));
        ps.setDate(6, Date.valueOf(reservation.getDate_retour()));
        ps.setInt(7, reservation.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(ReservationVehicule reservationVehicule) throws SQLException {
        supprimer(reservationVehicule.getId());
    }

    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM reservation_vehicule WHERE id_reservation=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<ReservationVehicule> recuperer() throws SQLException {
        List<ReservationVehicule> reservations = new ArrayList<>();
        String req = "SELECT r.id_reservation, r.id_vehicule, r.id_client, r.lieu_prise, r.lieu_retour, r.date_location, r.date_retour, c.email AS client_email FROM reservation_vehicule r JOIN client c ON r.id_client = c.id_client";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            ReservationVehicule reservation = new ReservationVehicule(
                    rs.getInt("id_reservation"),
                    rs.getInt("id_vehicule"),
                    rs.getInt("id_client"),
                    rs.getString("lieu_prise"),
                    rs.getString("lieu_retour"),
                    rs.getDate("date_location").toLocalDate(),
                    rs.getDate("date_retour").toLocalDate()
            );
            reservation.setClientEmail(rs.getString("client_email"));
            reservations.add(reservation);
        }
        return reservations;
    }

    public List<ReservationVehicule> getAll() throws SQLException {
        return recuperer();
    }
}