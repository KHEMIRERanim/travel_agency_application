package services;

import entities.Commentaires;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommentaire implements IService<Commentaires> {
    private Connection con;

    public ServiceCommentaire() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Commentaires commentaire) throws SQLException {
        String req = "INSERT INTO commentaires (client_id, publication_id, commentaire, date_commentaire, rating) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, commentaire.getClient_id());
        ps.setInt(2, commentaire.getPublication_id());
        ps.setString(3, commentaire.getCommentaire());
        ps.setString(4, commentaire.getDate_commentaire());
        ps.setInt(5, commentaire.getRating());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            commentaire.setId_commentaire(rs.getInt(1));
        }
        System.out.println("Commentaire ajouté avec ID: " + commentaire.getId_commentaire());
    }

    @Override
    public void modifier(Commentaires commentaire) throws SQLException {
        String req = "UPDATE commentaires SET client_id=?, publication_id=?, commentaire=?, date_commentaire=?, rating=? WHERE id_commentaire=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, commentaire.getClient_id());
        ps.setInt(2, commentaire.getPublication_id());
        ps.setString(3, commentaire.getCommentaire());
        ps.setString(4, commentaire.getDate_commentaire());
        ps.setInt(5, commentaire.getRating());
        ps.setInt(6, commentaire.getId_commentaire());
        ps.executeUpdate();
        System.out.println("Commentaire modifié");
    }

    @Override
    public void supprimer(Commentaires commentaire) throws SQLException {
        String req = "DELETE FROM commentaires WHERE id_commentaire=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, commentaire.getId_commentaire());
        ps.executeUpdate();
        System.out.println("Commentaire supprimé");
    }

    @Override
    public List<Commentaires> recuperer() throws SQLException {
        List<Commentaires> commentaires = new ArrayList<>();
        String req = "SELECT * FROM commentaires";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            Commentaires commentaire = new Commentaires(
                    rs.getInt("id_commentaire"),
                    rs.getInt("client_id"),
                    rs.getInt("publication_id"),
                    rs.getString("commentaire"),
                    rs.getString("date_commentaire"),
                    rs.getInt("rating")
            );
            commentaires.add(commentaire);
        }
        return commentaires;
    }

    public Commentaires getById(int id_commentaire) throws SQLException {
        String sql = "SELECT * FROM commentaires WHERE id_commentaire = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id_commentaire);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Commentaires(
                    rs.getInt("id_commentaire"),
                    rs.getInt("client_id"),
                    rs.getInt("publication_id"),
                    rs.getString("commentaire"),
                    rs.getString("date_commentaire"),
                    rs.getInt("rating")
            );
        }
        return null;
    }

    public List<Commentaires> getCommentaires(int currentClientId, String searchText, String sortOption) throws SQLException {
        List<Commentaires> commentaires = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM commentaires");
        List<Object> params = new ArrayList<>();

        // Search filter
        if (searchText != null && !searchText.isEmpty()) {
            sql.append(" WHERE LOWER(commentaire) LIKE ?");
            params.add("%" + searchText + "%");
        }

        // Sort and rating filter
        if (sortOption != null) {
            switch (sortOption) {
                case "Recent First":
                    sql.append(" ORDER BY STR_TO_DATE(date_commentaire, '%d/%m/%Y') DESC");
                    break;
                case "Oldest First":
                    sql.append(" ORDER BY STR_TO_DATE(date_commentaire, '%d/%m/%Y') ASC");
                    break;
                case "1 Star":
                case "2 Stars":
                case "3 Stars":
                case "4 Stars":
                case "5 Stars":
                    String rating = sortOption.split(" ")[0];
                    if (params.isEmpty()) {
                        sql.append(" WHERE rating = ?");
                    } else {
                        sql.append(" AND rating = ?");
                    }
                    params.add(Integer.parseInt(rating));
                    break;
            }
        }

        PreparedStatement ps = con.prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Commentaires commentaire = new Commentaires(
                    rs.getInt("id_commentaire"),
                    rs.getInt("client_id"),
                    rs.getInt("publication_id"),
                    rs.getString("commentaire"),
                    rs.getString("date_commentaire"),
                    rs.getInt("rating")
            );
            commentaires.add(commentaire);
        }
        return commentaires;
    }

    public List<Commentaires> getCommentairesByPublicationId(int publicationId) throws SQLException {
        List<Commentaires> commentaires = new ArrayList<>();
        String query = "SELECT * FROM commentaires WHERE publication_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, publicationId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Commentaires commentaire = new Commentaires();
                commentaire.setId_commentaire(rs.getInt("id_commentaire"));
                commentaire.setClient_id(rs.getInt("client_id"));
                commentaire.setPublication_id(rs.getInt("publication_id"));
                commentaire.setCommentaire(rs.getString("commentaire"));
                commentaire.setDate_commentaire(rs.getString("date_commentaire"));
                commentaire.setRating(rs.getInt("rating"));
                commentaires.add(commentaire);
            }
        }
        return commentaires;
    }

}