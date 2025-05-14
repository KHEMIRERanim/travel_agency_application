package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Classe utilitaire pour l'envoi d'emails
 */
public class EmailSender {

    // Gmail SMTP configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    // Use a dedicated Gmail account for sending emails
    private static final String SYSTEM_EMAIL = "travellian.system@gmail.com";
    private static final String SYSTEM_PASSWORD = "vyhq gkfu equl lfqp";

    /**
     * Envoie un email avec les informations spécifiées
     * @param to Adresse email du destinataire
     * @param subject Sujet de l'email
     * @param content Contenu de l'email (peut être du HTML)
     * @throws MessagingException En cas d'erreur lors de l'envoi
     */
    public static void sendEmail(String to, String subject, String content) throws MessagingException {
        // Configuration des propriétés pour la session
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        // Création de la session avec authentification
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SYSTEM_EMAIL, SYSTEM_PASSWORD);
            }
        });

        // Création du message
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(SYSTEM_EMAIL, "Travellian System")); // Set a friendly name
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(content, "text/html; charset=utf-8");

        // Envoi du message
        Transport.send(message);
    }

    /**
     * Envoie un email contenant un code de vérification pour la réinitialisation du mot de passe
     * @param toEmail Adresse email du destinataire
     * @param code Code de vérification
     * @throws MessagingException En cas d'erreur lors de l'envoi
     */
    public static void sendVerificationCode(String toEmail, String code) throws MessagingException {
        // Configure email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        // Create session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SYSTEM_EMAIL, SYSTEM_PASSWORD);
            }
        });

        try {
            // Create message
            Message message = new MimeMessage(session);
            try {
                message.setFrom(new InternetAddress(SYSTEM_EMAIL, "Travellian System")); // Set a friendly name
            } catch (UnsupportedEncodingException e) {
                message.setFrom(new InternetAddress(SYSTEM_EMAIL)); // Fallback without friendly name
            }
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Code de vérification - Réinitialisation du mot de passe");
            
            // Create email body with HTML formatting
            String emailBody = String.format(
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<div style='text-align: center; padding: 20px 0;'>" +
                "<h1 style='color: #1e3a8a;'>TRAVELLIAN</h1>" +
                "</div>" +
                "<div style='background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);'>" +
                "<h2 style='color: #1e3a8a; margin-top: 0;'>Réinitialisation de votre mot de passe</h2>" +
                "<p>Bonjour,</p>" +
                "<p>Vous avez demandé la réinitialisation de votre mot de passe. Voici votre code de vérification :</p>" +
                "<div style='background-color: #f4f6f9; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0;'>" +
                "<h1 style='color: #1e3a8a; margin: 0; letter-spacing: 5px;'>%s</h1>" +
                "</div>" +
                "<p>Ce code est valable pendant 10 minutes.</p>" +
                "<p>Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.</p>" +
                "<p>Cordialement,<br>L'équipe Travellian</p>" +
                "</div>" +
                "<div style='text-align: center; padding: 20px 0; color: #90a4ae; font-size: 12px;'>" +
                "<p>Ce message a été envoyé automatiquement, merci de ne pas y répondre.</p>" +
                "</div>" +
                "</div>",
                code
            );

            message.setContent(emailBody, "text/html; charset=UTF-8");

            // Send message
            Transport.send(message);
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw e;
        }
    }
}