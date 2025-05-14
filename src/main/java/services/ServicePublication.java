package services;

import entities.Publication;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePublication implements IService<Publication> {
    private Connection con;

    public ServicePublication() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Publication publication) throws SQLException {
        String req = "INSERT INTO publication (client_id, titre, contenu, date_publication, active, like_count, type_publication) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, publication.getClient_id());
        ps.setString(2, publication.getTitre());
        ps.setString(3, publication.getContenu());
        ps.setString(4, publication.getDate_publication());
        ps.setBoolean(5, publication.isActive());
        ps.setInt(6, publication.getLikeCount());
        ps.setString(7, publication.getTypePublication());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            publication.setId_publication(rs.getInt(1));
        }
        System.out.println("Publication ajoutée avec ID: " + publication.getId_publication());
    }

    @Override
    public void modifier(Publication publication) throws SQLException {
        String req = "UPDATE publication SET client_id=?, titre=?, contenu=?, date_publication=?, active=?, like_count=?, type_publication=? WHERE id_publication=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, publication.getClient_id());
        ps.setString(2, publication.getTitre());
        ps.setString(3, publication.getContenu());
        ps.setString(4, publication.getDate_publication());
        ps.setBoolean(5, publication.isActive());
        ps.setInt(6, publication.getLikeCount());
        ps.setString(7, publication.getTypePublication());
        ps.setInt(8, publication.getId_publication());
        ps.executeUpdate();
        System.out.println("Publication modifiée");
    }

    @Override
    public void supprimer(Publication publication) throws SQLException {
        String req = "DELETE FROM publication WHERE id_publication=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, publication.getId_publication());
        ps.executeUpdate();
        System.out.println("Publication supprimée");
    }

    @Override
    public List<Publication> recuperer() throws SQLException {
        List<Publication> publications = new ArrayList<>();
        String req = "SELECT * FROM publication";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            String typePublication = rs.getString("type_publication");
            if (typePublication == null || typePublication.trim().isEmpty()) {
                typePublication = "Other";
            }
            Publication publication = new Publication(
                    rs.getInt("id_publication"),
                    rs.getInt("client_id"),
                    rs.getString("titre"),
                    rs.getString("contenu"),
                    rs.getString("date_publication"),
                    rs.getBoolean("active"),
                    rs.getInt("like_count"),
                    typePublication
            );
            publications.add(publication);
        }
        return publications;
    }

    public Publication getById(int id_publication) throws SQLException {
        String sql = "SELECT * FROM publication WHERE id_publication = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id_publication);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            String typePublication = rs.getString("type_publication");
            if (typePublication == null || typePublication.trim().isEmpty()) {
                typePublication = "Other";
            }
            return new Publication(
                    rs.getInt("id_publication"),
                    rs.getInt("client_id"),
                    rs.getString("titre"),
                    rs.getString("contenu"),
                    rs.getString("date_publication"),
                    rs.getBoolean("active"),
                    rs.getInt("like_count"),
                    typePublication
            );
        }
        return null;
    }

    public List<Publication> getAllPublications(String searchText, String sortOption) throws SQLException {
        List<Publication> publications = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM publication");
        List<Object> params = new ArrayList<>();

        // Search filter
        if (searchText != null && !searchText.isEmpty()) {
            sql.append(" WHERE LOWER(titre) LIKE ?");
            params.add("%" + searchText + "%");
        }

        // Type filter and sorting
        if (sortOption != null) {
            if (searchText == null || searchText.isEmpty()) {
                sql.append(" WHERE ");
            } else {
                sql.append(" AND ");
            }
            switch (sortOption) {
                case "Show Flights":
                    sql.append("type_publication = ?");
                    params.add("Flight");
                    break;
                case "Show Reservations":
                    sql.append("type_publication = ?");
                    params.add("Reservation");
                    break;
                case "Show Vehicles":
                    sql.append("type_publication = ?");
                    params.add("Vehicle");
                    break;
                case "Show Other":
                    sql.append("type_publication = ?");
                    params.add("Other");
                    break;
                case "Recent First":
                    sql.append("1=1 ORDER BY STR_TO_DATE(date_publication, '%d/%m/%Y') DESC");
                    break;
                case "Oldest First":
                    sql.append("1=1 ORDER BY STR_TO_DATE(date_publication, '%d/%m/%Y') ASC");
                    break;
            }
        }

        PreparedStatement ps = con.prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String typePublication = rs.getString("type_publication");
            if (typePublication == null || typePublication.trim().isEmpty()) {
                typePublication = "Other";
            }
            Publication publication = new Publication(
                    rs.getInt("id_publication"),
                    rs.getInt("client_id"),
                    rs.getString("titre"),
                    rs.getString("contenu"),
                    rs.getString("date_publication"),
                    rs.getBoolean("active"),
                    rs.getInt("like_count"),
                    typePublication
            );
            publications.add(publication);
        }
        return publications;
    }

    public void incrementLikeCount(int publicationId) throws SQLException {
        String sql = "UPDATE publication SET like_count = like_count + 1 WHERE id_publication = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, publicationId);
        ps.executeUpdate();
        System.out.println("Like count incremented for publication ID: " + publicationId);
    }

    public void decrementLikeCount(int publicationId) throws SQLException {
        String sql = "UPDATE publication SET like_count = like_count - 1 WHERE id_publication = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, publicationId);
        ps.executeUpdate();
        System.out.println("Like count decremented for publication ID: " + publicationId);
    }

    public void deactivateAllPublicationsByClientId(int clientId) throws SQLException {
        String sql = "UPDATE publication SET active = false WHERE client_id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, clientId);
        ps.executeUpdate();
        System.out.println("All publications deactivated for client ID: " + clientId);
    }
}