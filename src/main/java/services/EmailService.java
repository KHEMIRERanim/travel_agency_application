package services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import entities.Client;
import entities.Publication;
import entities.Commentaires;
import java.util.Properties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.MyDatabase;

public class EmailService {

    private static final String USERNAME = "bs2094894@gmail.com";
    private static final String PASSWORD = "qleoxfbtwcqpxltq";
    private static final String SUPPORT_EMAIL = "support@your-app.com";

    private static void sendEmail(String recipient, String subject, String content) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // <- ajout essentiel

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setContent(content, "text/html");

            Transport.send(message);
            System.out.println("✅ Email sent to " + recipient);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("❌ Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public static void sendCommentNotification(Client commenter, Publication publication, Commentaires commentaire) {
        if (commenter == null || publication == null || commentaire == null) {
            System.err.println("❌ Invalid input for email notification: commenter, publication, or commentaire is null");
            return;
        }

        int clientId = publication.getClient_id();
        if (clientId <= 0) {
            System.err.println("❌ Invalid client ID for publication: " + clientId);
            return;
        }

        String recipient = getClientEmail(clientId);
        if (recipient == null || recipient.isEmpty()) {
            System.err.println("❌ No email found for client ID: " + clientId);
            return;
        }

        String subject = "Nouveau Commentaire sur Votre Publication #" + publication.getId_publication();
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<html><body>")
                .append("<h2 style='color: #4CAF50;'>Cher(e) Utilisateur,</h2>")
                .append("<p>Un nouveau commentaire a été ajouté à votre publication <strong>")
                .append(publication.getTitre()).append("</strong> par <strong>")
                .append(commenter.getEmail()).append("</strong>.</p>")
                .append("<p><strong>Détails du Commentaire :</strong></p>")
                .append("<ul>")
                .append("<li><strong>Publication ID :</strong> ").append(publication.getId_publication()).append("</li>")
                .append("<li><strong>Titre de la Publication :</strong> ").append(publication.getTitre()).append("</li>")
                .append("<li><strong>Commentaire :</strong> ").append(commentaire.getCommentaire()).append("</li>")
                .append("<li><strong>Note :</strong> ").append("★".repeat(commentaire.getRating())).append("☆".repeat(5 - commentaire.getRating())).append("</li>")
                .append("<li><strong>Date du Commentaire :</strong> ").append(commentaire.getDate_commentaire()).append("</li>")
                .append("</ul>")
                .append("<p>Connectez-vous à l'application pour voir le commentaire et répondre si nécessaire.</p>")
                .append("<p>Pour toute question, contactez-nous à <a href='mailto:").append(SUPPORT_EMAIL).append("'>")
                .append(SUPPORT_EMAIL).append("</a>.</p>")
                .append("<hr>")
                .append("<p style='color:gray; font-size:12px;'>Ceci est un email automatique, merci de ne pas y répondre.</p>")
                .append("</body></html>");

        sendEmail(recipient, subject, emailContent.toString());
    }

    private static synchronized String getClientEmail(int clientId) {
        if (clientId <= 0) {
            System.err.println("❌ Invalid client ID in getClientEmail: " + clientId);
            return null;
        }

        System.out.println("Fetching email for client ID: " + clientId);
        String email = null;
        String query = "SELECT email FROM client WHERE id_client = ?";

        Connection conn = MyDatabase.getInstance().getCnx();  // Do NOT close this!
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    email = rs.getString("email");
                    System.out.println("Email found: " + email);
                } else {
                    System.out.println("No email found for client ID: " + clientId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Failed to fetch client email for client ID " + clientId + ": " + e.getMessage());
        }
        return email;
    }

}