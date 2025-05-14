package entities;

public class Commentaires {
    private int id_commentaire;
    private int client_id;
    private int publication_id; // New field
    private String commentaire;
    private String date_commentaire;
    private int rating;

    // Constructors
    public Commentaires() {
    }

    public Commentaires(int client_id, int publication_id, String commentaire, String date_commentaire, int rating) {
        this.client_id = client_id;
        this.publication_id = publication_id;
        setCommentaire(commentaire);
        setDate_commentaire(date_commentaire);
        setRating(rating);
    }

    public Commentaires(int id_commentaire, int client_id, int publication_id, String commentaire, String date_commentaire, int rating) {
        this.id_commentaire = id_commentaire;
        this.client_id = client_id;
        this.publication_id = publication_id;
        setCommentaire(commentaire);
        setDate_commentaire(date_commentaire);
        setRating(rating);
    }

    // Getters and Setters
    public int getId_commentaire() {
        return id_commentaire;
    }

    public void setId_commentaire(int id_commentaire) {
        this.id_commentaire = id_commentaire;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getPublication_id() {
        return publication_id;
    }

    public void setPublication_id(int publication_id) {
        this.publication_id = publication_id;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        if (commentaire == null || commentaire.trim().isEmpty()) {
            throw new IllegalArgumentException("Commentaire cannot be empty");
        }
        this.commentaire = commentaire;
    }

    public String getDate_commentaire() {
        return date_commentaire;
    }

    public void setDate_commentaire(String date_commentaire) {
        if (date_commentaire != null && !date_commentaire.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            throw new IllegalArgumentException("Date must be in format dd/MM/yyyy");
        }
        this.date_commentaire = date_commentaire;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Commentaires{" +
                "id_commentaire=" + id_commentaire +
                ", client_id=" + client_id +
                ", publication_id=" + publication_id +
                ", commentaire='" + commentaire + '\'' +
                ", date_commentaire='" + date_commentaire + '\'' +
                ", rating=" + rating +
                '}';
    }
}