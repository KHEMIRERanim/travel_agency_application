package services;

import entities.Vehicule;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceVehicule {
    private final Connection conn;

    public ServiceVehicule() {
        this.conn = MyDatabase.getInstance().getCnx();
    }

    public void ajouter(Vehicule vehicule) throws SQLException {
        String query = "INSERT INTO vehicule (type, lieu_prise, lieu_retour, date_location, date_retour, image_path, prix) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, vehicule.getType());
            stmt.setString(2, vehicule.getLieuPrise());
            stmt.setString(3, vehicule.getLieuRetour());
            stmt.setDate(4, vehicule.getDateLocation() != null ? Date.valueOf(vehicule.getDateLocation()) : null);
            stmt.setDate(5, vehicule.getDateRetour() != null ? Date.valueOf(vehicule.getDateRetour()) : null);
            stmt.setString(6, vehicule.getImagePath());
            stmt.setDouble(7, vehicule.getPrix());
            stmt.executeUpdate();
            System.out.println("Vehicle added: " + vehicule.getType());
        }
    }

    public List<Vehicule> getAll() throws SQLException {
        List<Vehicule> vehicules = new ArrayList<>();
        String query = "SELECT * FROM vehicule";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Vehicule v = new Vehicule();
                v.setId(rs.getInt("id_vehicule"));
                v.setType(rs.getString("type"));
                v.setLieuPrise(rs.getString("lieu_prise"));
                v.setLieuRetour(rs.getString("lieu_retour"));
                v.setDateLocation(rs.getDate("date_location") != null ? rs.getDate("date_location").toLocalDate() : null);
                v.setDateRetour(rs.getDate("date_retour") != null ? rs.getDate("date_retour").toLocalDate() : null);
                v.setImagePath(rs.getString("image_path"));
                v.setPrix(rs.getDouble("prix"));
                vehicules.add(v);
            }
            System.out.println("Retrieved " + vehicules.size() + " vehicles.");
        }
        return vehicules;
    }

    public void modifier(Vehicule vehicule) throws SQLException {
        String query = "UPDATE vehicule SET type = ?, lieu_prise = ?, lieu_retour = ?, date_location = ?, date_retour = ?, image_path = ?, prix = ? WHERE id_vehicule = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, vehicule.getType());
            stmt.setString(2, vehicule.getLieuPrise());
            stmt.setString(3, vehicule.getLieuRetour());
            stmt.setDate(4, vehicule.getDateLocation() != null ? Date.valueOf(vehicule.getDateLocation()) : null);
            stmt.setDate(5, vehicule.getDateRetour() != null ? Date.valueOf(vehicule.getDateRetour()) : null);
            stmt.setString(6, vehicule.getImagePath());
            stmt.setDouble(7, vehicule.getPrix());
            stmt.setInt(8, vehicule.getId());
            stmt.executeUpdate();
            System.out.println("Vehicle updated: " + vehicule.getType());
        }
    }

    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM vehicule WHERE id_vehicule = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Vehicle deleted: ID " + id);
        }
    }
}