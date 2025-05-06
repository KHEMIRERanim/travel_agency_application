package entities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class Reclamation {
    private int id_reclamation;
    private String type;
    private String dateIncident;
    private String description;
    private Client client = new Client(); // Initialize Client object

    // Constructors
    public Reclamation(int clientId, String type, String dateIncident, String description) {
        this.client.setId_client(clientId);
        setType(type);
        setDateIncident(dateIncident);
        setDescription(description);
    }

    public Reclamation(int id_reclamation, int clientId, String type, String dateIncident, String description) {
        this(clientId, type, dateIncident, description);
        this.id_reclamation = id_reclamation;
    }

    // Getters and Setters
    public int getId_reclamation() {
        return id_reclamation;
    }

    public void setId_reclamation(int id_reclamation) {
        this.id_reclamation = id_reclamation;
    }

    public int getClientId() {
        return client.getId_client();
    }

    public void setClientId(int id_client) {
        this.client.setId_client(id_client);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        this.client = client;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
        this.type = type;
    }

    public String getDateIncident() {
        return dateIncident;
    }

    public void setDateIncident(String dateIncident) {
        if (Pattern.matches("^\\d{2}/\\d{2}/\\d{4}$", dateIncident)) {
            this.dateIncident = dateIncident;
        } else {
            throw new IllegalArgumentException("Date must be in format jj/mm/aaaa");
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        this.description = description;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id_reclamation=" + id_reclamation +
                ", client_id=" + client.getId_client() +
                ", type='" + type + '\'' +
                ", dateIncident='" + dateIncident + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}