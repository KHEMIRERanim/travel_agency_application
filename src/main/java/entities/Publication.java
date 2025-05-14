package entities;

public class Publication {
    private int id_publication;
    private int client_id;
    private String titre;
    private String contenu;
    private String date_publication;
    private boolean active;
    private int likeCount;
    private String typePublication;

    
    public Publication(int id_publication, int client_id, String titre, String contenu, String date_publication, boolean active, int likeCount, String typePublication) {
        this.id_publication = id_publication;
        this.client_id = client_id;
        setTitre(titre);
        setContenu(contenu);
        setDate_publication(date_publication);
        this.active = active;
        setLikeCount(likeCount);
        setTypePublication(typePublication);
    }

    public Publication(int client_id, String titre, String contenu, String date_publication, boolean active, String typePublication) {
        this(0, client_id, titre, contenu, date_publication, active, 0, typePublication); // id=0, likeCount=0 for new publications
    }

    public Publication() {
        this.active = true;
        this.likeCount = 0;
        this.typePublication = "Other"; // Default type
    }

    // Getters and Setters
    public int getId_publication() {
        return id_publication;
    }

    public void setId_publication(int id_publication) {
        this.id_publication = id_publication;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        if (contenu == null || contenu.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
        this.contenu = contenu;
    }

    public String getDate_publication() {
        return date_publication;
    }

    public void setDate_publication(String date_publication) {
        if (date_publication != null && !date_publication.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            throw new IllegalArgumentException("Date must be in format dd/MM/yyyy");
        }
        this.date_publication = date_publication;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        if (likeCount < 0) {
            throw new IllegalArgumentException("Like count cannot be negative");
        }
        this.likeCount = likeCount;
    }

    public String getTypePublication() {
        return typePublication;
    }

    public void setTypePublication(String typePublication) {
        if (typePublication == null || typePublication.trim().isEmpty()) {
            throw new IllegalArgumentException("Publication type cannot be empty");
        }
        String[] validTypes = {"Flight", "Reservation", "Vehicle", "Other"};
        boolean isValid = false;
        for (String type : validTypes) {
            if (type.equalsIgnoreCase(typePublication)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new IllegalArgumentException("Invalid publication type. Must be one of: Flight, Reservation, Vehicle, Other");
        }
        this.typePublication = typePublication;
    }

    @Override
    public String toString() {
        return "Publication{" +
                "id_publication=" + id_publication +
                ", client_id=" + client_id +
                ", titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                ", date_publication='" + date_publication + '\'' +
                ", active=" + active +
                ", likeCount=" + likeCount +
                ", typePublication='" + typePublication + '\'' +
                '}';
    }
}