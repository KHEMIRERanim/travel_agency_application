package services;

import entities.ReservationHotel;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservationHotel implements IService<ReservationHotel> {
    private Connection con;

    public ServiceReservationHotel() {
        con = MyDatabase.getInstance().getConnection();

    }

    @Override
    public void ajouter(ReservationHotel reservationHotel) throws SQLException {
        String req = "INSERT INTO Reservation(hotel_id, id_utilisateur, checkin_date, checkout_date) " +
                "VALUES ('" + reservationHotel.getHotel_id() + "', '" + reservationHotel.getId_utilisateur() +
                "', '" + reservationHotel.getCheckin_date() + "', '" + reservationHotel.getCheckout_date() + "')";

        Statement st = con.createStatement();
        st.executeUpdate(req);
        System.out.println("ReservationHotel ajouté avec Succes !");

    }

    @Override
    public void modifier(ReservationHotel reservationHotel) throws SQLException {
        String req = "UPDATE reservation SET hotel_id=?,id_utilisateur=?,checkin_date=?,checkout_date=? WHERE id_reservation=? ";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservationHotel.getHotel_id());
        ps.setInt(2, reservationHotel.getId_utilisateur());
        ps.setString(3, reservationHotel.getCheckin_date());
        ps.setString(4, reservationHotel.getCheckout_date());
        ps.setInt(5, reservationHotel.getId_reservation());
        ps.executeUpdate();
        System.out.println("Modifier avec Succès !");
    }

    @Override
    public void supprimer(ReservationHotel reservationHotel) throws SQLException {
        String req = "DELETE FROM Reservation WHERE id_reservation=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservationHotel.getId_reservation());
        ps.executeUpdate();
        System.out.println("Supprimer avec Succès !");

    }

    @Override
    public List<ReservationHotel> recuperer() throws SQLException {
        List<ReservationHotel> reservationHotels = new ArrayList<>();
        String req = "SELECT * FROM reservation";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int id_reserv = rs.getInt("id_reservation");
            int id_h = rs.getInt("hotel_id");
            int id_u = rs.getInt("id_utilisateur");
            String date_in = rs.getString("checkin_date");
            String date_out = rs.getString("checkout_date");


            ReservationHotel r = new ReservationHotel(id_reserv, id_h, id_u, date_in, date_out);


            reservationHotels.add(r);
        }


        return reservationHotels;
    }
}