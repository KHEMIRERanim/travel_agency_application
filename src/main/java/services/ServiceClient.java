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
        String req = "INSERT INTO client (nom, prenom, email, numero_telephone, date_de_naissance, mot_de_passe) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, client.getNom());
        ps.setString(2, client.getPrenom());
        ps.setString(3, client.getEmail());
        ps.setInt(4, client.getNumero_telephone());
        ps.setString(5, client.getDate_de_naissance());
        ps.setString(6, client.getMot_de_passe());

        ps.executeUpdate();

        // Set the generated id_client
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            client.setId_client(rs.getInt(1));
        }
        System.out.println("Client ajouté avec ID: " + client.getId_client());
    }

    @Override
    public void modifier(Client client) throws SQLException {
        String req = "UPDATE client SET nom=?, prenom=?, email=?, " +
                "numero_telephone=?, date_de_naissance=?, mot_de_passe=? WHERE id_client=?";
        PreparedStatement ps = con.prepareStatement(req);

        ps.setString(1, client.getNom());
        ps.setString(2, client.getPrenom());
        ps.setString(3, client.getEmail());
        ps.setInt(4, client.getNumero_telephone());
        ps.setString(5, client.getDate_de_naissance());
        ps.setString(6, client.getMot_de_passe());
        ps.setInt(7, client.getId_client());

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
                    rs.getString("mot_de_passe")
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
                    rs.getString("mot_de_passe")
            );
        }
        return null;
    }
}