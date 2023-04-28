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
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class EmailService {

    @Inject
    private Properties applicationProperties;

    protected static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

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
            LOGGER.log(Level.INFO, "Error while setting email sender", e.getCause());
        }
    }

    /**
     * Sends email with link to action.
     *
     * @param receiverAddress Email address of the receiver.
     * @param name            Name used in greeting.
     * @param content         Body of the message.
     * @param signature       Signature.
     * @param action          Name of the action.
     * @param link            Link to action.
     * @param subject         Subject of the message.
     * @param title           Title.
     * @param greeting        Greeting.
     */
    private void sendMessageWithLink(
        String receiverAddress, String name, String content, String signature,
        String action, String link, String subject, String title, String greeting) {
        StringBuilder builder = new StringBuilder();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("templates/template.html");
             BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String str;
            while ((str = in.readLine()) != null) {
                builder.append(str);
            }
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Error while reading email message template file", e.getCause());
        }
        String templateMessage = builder.toString()
            .replace("$username", name)
            .replace("$link", link)
            .replace("$message", content)
            .replace("$last", signature)
            .replace("$action", action)
            .replace("$title", title)
            .replace("$greeting", greeting);
        try {
            InternetAddress[] addresses = {new InternetAddress(receiverAddress)};
            mimeMessage.setRecipients(Message.RecipientType.TO, addresses);
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(templateMessage, "text/html; charset=utf-8");
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.log(Level.INFO, "Error while sending an email " + receiverAddress, e.getCause());
        }
    }

    @Asynchronous
    public void resetPasswordEmail(String to, String name, String link, String language) {
        this.sendMessageWithLink(
            to,
            name,
            I18n.getMessage(I18n.EMAIL_MESSAGE_RESET_PASSWORD_MESSAGE, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_SIGNATURE, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_RESET_PASSWORD_ACTION, language),
            link,
            I18n.getMessage(I18n.EMAIL_MESSAGE_RESET_PASSWORD_SUBJECT, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_RESET_PASSWORD_TITLE, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_GREETING, language));
    }

    @Asynchronous
    public void sendConfirmRegistrationEmail(
        String receiverAddress, String name, String linkToAction, String language) {
        this.sendMessageWithLink(
            receiverAddress,
            name,
            I18n.getMessage(I18n.EMAIL_MESSAGE_CONFIRM_ACCOUNT_MESSAGE, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_SIGNATURE, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_CONFIRM_ACCOUNT_ACTION, language),
            linkToAction,
            I18n.getMessage(I18n.EMAIL_MESSAGE_CONFIRM_ACCOUNT_SUBJECT, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_CONFIRM_ACCOUNT_TITLE, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_GREETING, language)
        );
    }

    @Asynchronous
    public void changeEmailAddress(String receiverAddress, String name, String link, String language) {
    }

    @Asynchronous
    void notifyAboutAdminLogin(String receiverAddress, String name, String language) {
    }
}
