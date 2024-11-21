package com.byft;

import android.os.AsyncTask;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

    public static void sendEmail(final String toEmail, final String subject, final String body) {
        new SendEmailTask().execute(toEmail, subject, body);
    }

    private static class SendEmailTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            final String toEmail = params[0];
            final String subject = params[1];
            final String body = params[2];
            final String fromEmail = "playlog1234@gmail.com"; // Replace with your email
            final String password = "sbyr nhga ejdl nbxv"; // Replace with your email password

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);
                System.out.println("Email sent successfully!");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
}