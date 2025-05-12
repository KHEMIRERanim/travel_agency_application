package entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.regex.Pattern;

public class User {
    private int id_user;
    private String nom;
    private String prenom;
    private String email;
    private int numero_telephone;
    private String date_de_naissance;
    private String mot_de_passe;
    private String profilePicture;
    private String role; // New role field

    // Constructors
    public User(int id_user, String nom, String prenom, String email,
                int numero_telephone, String date_de_naissance, String mot_de_passe,
                String profilePicture, String role) {
        this.id_user = id_user;
        this.nom = nom;
        this.prenom = prenom;
        setEmail(email);
        setNumero_telephone(numero_telephone);
        setDate_de_naissance(date_de_naissance);
        this.mot_de_passe = mot_de_passe;
        this.profilePicture = profilePicture != null ? profilePicture : "/images/default_profile.png";
        setRole(role);
    }

    public User(String nom, String prenom, String email, int numero_telephone,
                String date_de_naissance, String mot_de_passe, String profilePicture, String role) {
        this(0, nom, prenom, email, numero_telephone, date_de_naissance, mot_de_passe, profilePicture, role);
    }

    public User() {
        this.profilePicture = "/images/default_profile.png";
        this.role = "user";
    }

    // Getters and Setters
    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (role == null || (!role.equals("user") && !role.equals("admin"))) {
            throw new IllegalArgumentException("Role must be either 'user' or 'admin'");
        }
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id_user=" + id_user +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", numero_telephone=" + numero_telephone +
                ", date_de_naissance='" + date_de_naissance + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}