package services;

import entities.Hotels;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceHotel implements IService<Hotels> {
    private Connection con;
    public ServiceHotel() {
        con= MyDatabase.getInstance().getConnection();

    }
    @Override
    public void ajouter(Hotels hotel) throws SQLException {
        String req = "INSERT INTO hotels(nom_hotel, destination, check_in, check_out, prix, type_chambre, status) " +
                "VALUES ('" + hotel.getNom_hotel() + "', '" + hotel.getDestination() + "', '" + hotel.getCheck_in() +
                "', '" + hotel.getCheck_out() + "', " + hotel.getPrix() + ", '" + hotel.getType_chambre() + "', '" + hotel.getStatus() +   "')";
        Statement st = con.createStatement();
        st.executeUpdate(req);
        System.out.println("Hôtel ajouté avec Succes !");
    }

    @Override
    public void modifier(Hotels hotel) throws SQLException{
        String req = "UPDATE hotels SET nom_hotel=?, destination=?, check_in=?, check_out=?, prix=?, type_chambre=?, status=? WHERE hotel_id=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, hotel.getNom_hotel());
        ps.setString(2, hotel.getDestination());
        ps.setString(3, hotel.getCheck_in());
        ps.setString(4, hotel.getCheck_out());
        ps.setDouble(5, hotel.getPrix());
        ps.setString(6, hotel.getType_chambre());
        ps.setString(7, hotel.getStatus());
        ps.setInt(8, hotel.getHotel_id());
        ps.executeUpdate();
        System.out.println("Hôtel modifié !");
    }

    @Override
    public void supprimer(Hotels hotels) throws SQLException {
        String req = "DELETE FROM hotels WHERE hotel_id=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, hotels.getHotel_id());
        ps.executeUpdate();
        System.out.println("Hôtel supprimé !");

    }


    @Override
    public List<Hotels> recuperer() throws SQLException {
        List<Hotels> hotels = new ArrayList<>();
        String req = "SELECT * FROM hotels";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            int id = rs.getInt("hotel_id");
            String nom = rs.getString("nom_hotel");
            String dest = rs.getString("destination");
            String in = rs.getString("check_in");
            String out = rs.getString("check_out");
            double prix = rs.getDouble("prix");
            String type = rs.getString("type_chambre");
            String status = rs.getString("status");


            Hotels h = new Hotels(id, nom, dest, in, out, prix, type, status);
            hotels.add(h);
        }
        return hotels;
    }
}