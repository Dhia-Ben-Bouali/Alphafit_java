package services;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    public static void sendEmail(String recipientEmail, String subject,String htmlBody) throws MessagingException {

        // Paramètres SMTP du fournisseur de messagerie
        String host = "smtp.gmail.com";
        String port = "587"; // Port SMTP (par exemple, 587 pour TLS ou 465 pour SSL)
        String username = "legaltechrec@gmail.com";
        String password = "yxzc rivk amcc neif";

        // Propriétés de la session
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        // Création de la session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);

            // Set HTML content
            message.setContent(htmlBody, "text/html");

            // Send message
            Transport.send(message);

            System.out.println("Email sent successfully to " + recipientEmail);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }

    public static void main(String[] args) {
        String recipientEmail = "ikramsegni28@gmail.com";
        String subject = "Test Email";
        String body = "Hello, this is a test email.";

        try {
            sendEmail(recipientEmail, subject, body);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
