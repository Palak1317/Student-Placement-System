package service;

import database.DBConnection;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class EmailService {

    private static final String FROM_EMAIL = System.getenv("SENDER_EMAIL");

    // 🔒 IMPORTANT: Use environment variable (DO NOT hardcode in real projects)
    private static final String APP_PASSWORD = System.getenv("EMAIL_APP_PASSWORD");

    // 🔥 BULK EMAIL FUNCTION
    public static void sendBulkEmails(String status) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                SELECT DISTINCT s.email
                FROM applications a
                JOIN students s ON a.student_id = s.id
                WHERE a.status = ?
            """;

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                String email = rs.getString("email");

                if ("Selected".equals(status)) {
                    sendEmail(email,
                            "Congratulations!",
                            "You have been SELECTED 🎉");
                } else if ("Rejected".equals(status)) {
                    sendEmail(email,
                            "Application Update",
                            "We regret to inform you that you were not selected.");
                }
            }

            System.out.println("Bulk emails sent!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📧 SINGLE EMAIL
    public static void sendEmail(String toEmail, String subject, String messageText) {

        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                    }
                });

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toEmail));

            message.setSubject(subject);
            message.setText(messageText);

            Transport.send(message);

            System.out.println("Email Sent to: " + toEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}