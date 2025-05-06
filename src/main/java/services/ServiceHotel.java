package services;

import entities.Hotels;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceHotel implements IService<Hotels> {
    private Connection con;

    public ServiceHotel() {
        con = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Hotels hotel) throws SQLException {
        String req = "INSERT INTO hotels(nom_hotel, destination, prix, type_chambre, status, wifi, piscine, image, note) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, hotel.getNom_hotel());
        ps.setString(2, hotel.getDestination());
        ps.setDouble(3, hotel.getPrix());
        ps.setString(4, hotel.getType_chambre());
        ps.setString(5, hotel.getStatus());
        ps.setBoolean(6, hotel.isWifi());
        ps.setBoolean(7, hotel.isPiscine());
        ps.setBytes(8, hotel.getImage());
        ps.setInt(9, hotel.getNote());
        ps.executeUpdate();
        System.out.println("Hôtel ajouté avec succès !");
    }

    @Override
    public void modifier(Hotels hotel) throws SQLException {
        String req = "UPDATE hotels SET nom_hotel=?, destination=?, prix=?, type_chambre=?, status=?, wifi=?, piscine=?, image=?, note=? WHERE hotel_id=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, hotel.getNom_hotel());
        ps.setString(2, hotel.getDestination());
        ps.setDouble(3, hotel.getPrix());
        ps.setString(4, hotel.getType_chambre());
        ps.setString(5, hotel.getStatus());
        ps.setBoolean(6, hotel.isWifi());
        ps.setBoolean(7, hotel.isPiscine());
        ps.setBytes(8, hotel.getImage());
        ps.setInt(9, hotel.getNote());
        ps.setInt(10, hotel.getHotel_id());
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
            double prix = rs.getDouble("prix");
            String type = rs.getString("type_chambre");
            String status = rs.getString("status");
            boolean wifi = rs.getBoolean("wifi");
            boolean piscine = rs.getBoolean("piscine");
            byte[] image = rs.getBytes("image");
            int note = rs.getInt("note");
            Hotels h = new Hotels(id, nom, dest, prix, type, status, wifi, piscine, image, note);
            hotels.add(h);
        }
        return hotels;
    }
}
