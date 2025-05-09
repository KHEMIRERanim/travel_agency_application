package entities;

public class Client {
    private int id_client;
    private String nom;
    private String prenom;
    private String email;
    private int numero_telephone;
    private String date_de_naissance;
    private String mot_de_passe;

    public Client() {}

    public Client(int id_client, String nom, String prenom, String email, int numero_telephone, String date_de_naissance, String mot_de_passe) {
        this.id_client = id_client;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.numero_telephone = numero_telephone;
        this.date_de_naissance = date_de_naissance;
        this.mot_de_passe = mot_de_passe;
    }

    public Client(String nom, String prenom, String email, int numero_telephone, String date_de_naissance, String mot_de_passe) {
        this(0, nom, prenom, email, numero_telephone, date_de_naissance, mot_de_passe);
    }

    public int getId_client() { return id_client; }
    public void setId_client(int id_client) { this.id_client = id_client; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getNumero_telephone() { return numero_telephone; }
    public void setNumero_telephone(int numero_telephone) { this.numero_telephone = numero_telephone; }
    public String getDate_de_naissance() { return date_de_naissance; }
    public void setDate_de_naissance(String date_de_naissance) { this.date_de_naissance = date_de_naissance; }
    public String getMot_de_passe() { return mot_de_passe; }
    public void setMot_de_passe(String mot_de_passe) { this.mot_de_passe = mot_de_passe; }
}