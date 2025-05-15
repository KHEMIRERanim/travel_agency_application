        package services;

import entities.Client;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceClient implements IService<Client> {
    private Connection con;

    public ServiceClient() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Client client) throws SQLException {
        String req = "INSERT INTO client (nom, prenom, email, numero_telephone, date_de_naissance, mot_de_passe, profile_picture, role, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, client.getNom());
        ps.setString(2, client.getPrenom());
        ps.setString(3, client.getEmail());
        ps.setInt(4, client.getNumero_telephone());
        ps.setString(5, client.getDate_de_naissance());
        ps.setString(6, client.getMot_de_passe());
        ps.setString(7, client.getProfilePicture());
        ps.setString(8, client.getRole());
        ps.setString(9, client.getGender());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            client.setId_client(rs.getInt(1));
        }
        System.out.println("Client ajouté avec ID: " + client.getId_client());
    }

    @Override
    public void modifier(Client client) throws SQLException {
        String req = "UPDATE client SET nom=?, prenom=?, email=?, numero_telephone=?, date_de_naissance=?, mot_de_passe=?, profile_picture=?, role=?, gender=? WHERE id_client=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, client.getNom());
        ps.setString(2, client.getPrenom());
        ps.setString(3, client.getEmail());
        ps.setInt(4, client.getNumero_telephone());
        ps.setString(5, client.getDate_de_naissance());
        ps.setString(6, client.getMot_de_passe());
        ps.setString(7, client.getProfilePicture());
        ps.setString(8, client.getRole());
        ps.setString(9, client.getGender());
        ps.setInt(10, client.getId_client());
        ps.executeUpdate();
        System.out.println("Client modifié");
    }

    @Override
    public void supprimer(Client client) throws SQLException {
        String req = "DELETE FROM client WHERE id_client=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, client.getId_client());
        ps.executeUpdate();
        System.out.println("Client supprimé");
    }

    @Override
    public List<Client> recuperer() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String req = "SELECT * FROM client";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            Client client = new Client(
                    rs.getInt("id_client"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getInt("numero_telephone"),
                    rs.getString("date_de_naissance"),
                    rs.getString("mot_de_passe"),
                    rs.getString("profile_picture"),
                    rs.getString("role"),
                    rs.getString("gender")
            );
            clients.add(client);
        }
        return clients;
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM client WHERE email = ? LIMIT 1";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, email);
        return ps.executeQuery().next();
    }

    public Client getById(int id_client) throws SQLException {
        String sql = "SELECT * FROM client WHERE id_client = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id_client);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Client(
                    rs.getInt("id_client"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getInt("numero_telephone"),
                    rs.getString("date_de_naissance"),
                    rs.getString("mot_de_passe"),
                    rs.getString("profile_picture"),
                    rs.getString("role"),
                    rs.getString("gender")
            );
        }
        return null;
    }

    public Client authenticate(String email, String password) throws SQLException {
        String sql = "SELECT * FROM client WHERE email = ? AND mot_de_passe = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, email);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Client(
                    rs.getInt("id_client"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getInt("numero_telephone"),
                    rs.getString("date_de_naissance"),
                    rs.getString("mot_de_passe"),
                    rs.getString("profile_picture"),
                    rs.getString("role"),
                    rs.getString("gender")
            );
        }
        return null;
    }

    public Integer getClientIdByEmail(String email) throws SQLException {
        String sql = "SELECT id_client FROM client WHERE email = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("id_client");
        }
        return null;
    }
}
