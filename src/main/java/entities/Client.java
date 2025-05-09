package entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.regex.Pattern;

public class Client {
    private int id_client;
    private String nom;
    private String prenom;
    private String email;
    private int numero_telephone;
    private String date_de_naissance;
    private String mot_de_passe;
    private String profilePicture; // New field for profile picture path

    // Constructors
    public Client(int id_client, String nom, String prenom, String email,
                  int numero_telephone, String date_de_naissance, String mot_de_passe, String profilePicture) {
        this.id_client = id_client;
        this.nom = nom;
        this.prenom = prenom;
        setEmail(email);
        setNumero_telephone(numero_telephone);
        setDate_de_naissance(date_de_naissance);
        this.mot_de_passe = mot_de_passe;
        this.profilePicture = profilePicture != null ? profilePicture : "/images/default_profile.png";
    }

    public Client(String nom, String prenom, String email, int numero_telephone,
                  String date_de_naissance, String mot_de_passe, String profilePicture) {
        this(0, nom, prenom, email, numero_telephone, date_de_naissance, mot_de_passe, profilePicture);
    }

    public Client() {
        this.profilePicture = "/images/default_profile.png";
    }

    // Getters and Setters
    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email must contain '@'");
        }
        this.email = email;
    }

    public int getNumero_telephone() {
        return numero_telephone;
    }

    public void setNumero_telephone(int numero_telephone) {
        if (String.valueOf(numero_telephone).length() != 8) {
            throw new IllegalArgumentException("Phone number must be 8 digits");
        }
        this.numero_telephone = numero_telephone;
    }

    public String getDate_de_naissance() {
        return date_de_naissance;
    }

    public void setDate_de_naissance(String date_de_naissance) {
        if (!Pattern.matches("^\\d{2}/\\d{2}/\\d{4}$", date_de_naissance)) {
            throw new IllegalArgumentException("Date must be in format jj/mm/aaaa");
        }
        this.date_de_naissance = date_de_naissance;
    }

    public String getMot_de_passe() {
        return mot_de_passe;
    }

    public void setMot_de_passe(String mot_de_passe) {
        if (mot_de_passe == null || mot_de_passe.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.mot_de_passe = mot_de_passe;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture != null ? profilePicture : "/images/default_profile.png";
    }

    @Override
    public String toString() {
        return "Client{" +
                "id_client=" + id_client +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", numero_telephone=" + numero_telephone +
                ", date_de_naissance='" + date_de_naissance + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                '}';
    }
}