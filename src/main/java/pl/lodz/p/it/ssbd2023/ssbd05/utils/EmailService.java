package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Stateless
public class EmailService {

    @Inject
    Properties applicationProperties;

    private Session session;

    private MimeMessage mimeMessage;

    @PostConstruct
    private void init() {
        java.util.Properties mailProperties = new java.util.Properties();
        mailProperties.put("mail.smtp.host", applicationProperties.getSmtpHost());
        mailProperties.put("mail.smtp.starttls.enable", applicationProperties.isSmtpStarttls());
        mailProperties.put("mail.smtp.ssl.enable", applicationProperties.isSmtpSsl());
        mailProperties.put("mail.smtp.auth", applicationProperties.isSmtpAuth());
        mailProperties.put("mail.smtp.port", applicationProperties.getSmtpPort());

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(applicationProperties.getSender(),
                    applicationProperties.getSenderPassword());
            }
        };

        session = Session.getInstance(mailProperties, authenticator);
        mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom("ebok.ssbd05@gmail.com");
        } catch (MessagingException e) {
            //TODO
        }
    }


    private void sendMessage(String recieverAddress, String username, String content, String last, String action,
                             String link,
                             String subject, String title) {
        StringBuilder builder = new StringBuilder();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("templates/template.html");
            BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String str;
            while ((str = in.readLine()) != null) {
                builder.append(str);
            }
            in.close();
        } catch (IOException e) {
            //TODO
        }
        String templateMessage = builder.toString();
        templateMessage = templateMessage
            .replace("$username", username)
            .replace("$link", link)
            .replace("$message", content)
            .replace("$last", last)
            .replace("$action", action)
            .replace("$title", title);
        try {
            InternetAddress[] addresses = {new InternetAddress(recieverAddress)};
            mimeMessage.setRecipients(Message.RecipientType.TO, addresses);
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(templateMessage, "text/html; charset=utf-8");
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            //TODO
        }

    }

    @Asynchronous
    public void resetPasswordEmail(String to, String name, String link) {
        this.sendMessage(to,
            name,
            "Click the link below to reset your password.",
            "eBok team.",
            "Reset your password",
            link,
            "Reset password",
            "Reset password");
    }
}
