package services;

import entities.Reclamation;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation implements IService<Reclamation> {
    private Connection cnx;

    public ServiceReclamation() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Reclamation reclamation) throws SQLException {
        String req = "INSERT INTO reclamation (client_id, type, date_incident, description, etat) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);

        ps.setInt(1, reclamation.getClientId());
        ps.setString(2, reclamation.getType());
        ps.setString(3, reclamation.getDateIncident());
        ps.setString(4, reclamation.getDescription());
        ps.setString(5, reclamation.getEtat());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            reclamation.setId_reclamation(rs.getInt(1));
        }
    }

    @Override
    public void modifier(Reclamation reclamation) throws SQLException {
        String req = "UPDATE reclamation SET client_id=?, type=?, date_incident=?, description=?, etat=? WHERE id_reclamation=?";
        PreparedStatement ps = cnx.prepareStatement(req);

        ps.setInt(1, reclamation.getClientId());
        ps.setString(2, reclamation.getType());
        ps.setString(3, reclamation.getDateIncident());
        ps.setString(4, reclamation.getDescription());
        ps.setString(5, reclamation.getEtat());
        ps.setInt(6, reclamation.getId_reclamation());

        ps.executeUpdate();
    }

    @Override
    public void supprimer(Reclamation reclamation) throws SQLException {
        String req = "DELETE FROM reclamation WHERE id_reclamation=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, reclamation.getId_reclamation());
        ps.executeUpdate();
    }

    @Override
    public List<Reclamation> recuperer() throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String req = "SELECT * FROM reclamation";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Reclamation r = new Reclamation(
                    rs.getInt("id_reclamation"),
                    rs.getInt("client_id"),
                    rs.getString("type"),
                    rs.getString("date_incident"),
                    rs.getString("description"),
                    rs.getString("etat")
            );
            reclamations.add(r);
        }
        return reclamations;
    }

    public List<Reclamation> getReclamationsByClientId(int clientId) throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String req = "SELECT * FROM reclamation WHERE client_id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, clientId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Reclamation r = new Reclamation(
                    rs.getInt("id_reclamation"),
                    rs.getInt("client_id"),
                    rs.getString("type"),
                    rs.getString("date_incident"),
                    rs.getString("description"),
                    rs.getString("etat")
            );
            reclamations.add(r);
        }
        return reclamations;
    }

    public Reclamation getById(int id) throws SQLException {
        String req = "SELECT * FROM reclamation WHERE id_reclamation = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Reclamation(
                    rs.getInt("id_reclamation"),
                    rs.getInt("client_id"),
                    rs.getString("type"),
                    rs.getString("date_incident"),
                    rs.getString("description"),
                    rs.getString("etat")
            );
        }
        return null;
    }

    public void updateEtat(int id_reclamation, String etat) throws SQLException {
        String req = "UPDATE reclamation SET etat = ? WHERE id_reclamation = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, etat);
        ps.setInt(2, id_reclamation);
        ps.executeUpdate();
    }

    public void addMessage(int id_reclamation, String senderType, String messageText) throws SQLException {
        String req = "INSERT INTO reclamation_messages (id_reclamation, sender_type, message_text) VALUES (?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, id_reclamation);
        ps.setString(2, senderType);
        ps.setString(3, messageText);
        ps.executeUpdate();
    }

    public List<Message> getMessagesByReclamationId(int id_reclamation) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String req = "SELECT * FROM reclamation_messages WHERE id_reclamation = ? ORDER BY sent_at ASC";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, id_reclamation);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Message message = new Message(
                    rs.getInt("id_message"),
                    rs.getInt("id_reclamation"),
                    rs.getString("sender_type"),
                    rs.getString("message_text"),
                    rs.getTimestamp("sent_at")
            );
            messages.add(message);
        }
        return messages;
    }

    public static class Message {
        private int id_message;
        private int id_reclamation;
        private String sender_type;
        private String message_text;
        private Timestamp sent_at;

        public Message(int id_message, int id_reclamation, String sender_type, String message_text, Timestamp sent_at) {
            this.id_message = id_message;
            this.id_reclamation = id_reclamation;
            this.sender_type = sender_type;
            this.message_text = message_text;
            this.sent_at = sent_at;
        }

        public int getId_message() {
            return id_message;
        }

        public int getId_reclamation() {
            return id_reclamation;
        }

        public String getSender_type() {
            return sender_type;
        }

        public String getMessage_text() {
            return message_text;
        }

        public Timestamp getSent_at() {
            return sent_at;
        }
    }
}