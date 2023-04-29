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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class EmailService {

    protected static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    @Inject
    I18n i18n;

    private Properties applicationProperties;

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
        this.sendMessageWithLink(
            receiverAddress,
            name,
            I18n.getMessage(I18n.EMAIL_MESSAGE_CHANGE_EMAIL_MESSAGE, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_SIGNATURE, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_CHANGE_EMAIL_ACTION, language),
            link,
            I18n.getMessage(I18n.EMAIL_MESSAGE_CHANGE_EMAIL_SUBJECT, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_CHANGE_EMAIL_TITLE, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_GREETING, language)
        );
    }

    @Asynchronous
    public void notifyAboutAdminLogin(String receiverAddress, String name, String language,
                                      String ip, LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withLocale(new Locale(language));

        String content = I18n.getMessage(I18n.EMAIL_MESSAGE_ADMIN_AUTH_SUCCESS_MESSAGE, language)
            .replace("$ip", ip)
            .replace(
                "$timestamp", timestamp.format(formatter)
            );

        this.sendMessageWithoutLink(
            receiverAddress, name, content,
            I18n.getMessage(I18n.EMAIL_MESSAGE_SIGNATURE, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_ADMIN_AUTH_SUCCESS_SUBJECT, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_ADMIN_AUTH_SUCCESS_TITLE, language),
            I18n.getMessage(I18n.EMAIL_MESSAGE_GREETING, language)
        );
    }

    @Asynchronous
    public void notifyBlockedAccIncorrectLoginLimit(String receiver, String name, String lang) {
        this.sendMessageWithoutLink(
            receiver, name,
            I18n.getMessage(I18n.EMAIL_MESSAGE_INCORRECT_LOGIN_LIMIT_ACCOUNT_BLOCKED_MESSAGE, lang),
            I18n.getMessage(I18n.EMAIL_MESSAGE_SIGNATURE, lang),
            I18n.getMessage(I18n.EMAIL_MESSAGE_INCORRECT_LOGIN_LIMIT_ACCOUNT_BLOCKED_SUBJECT, lang),
            I18n.getMessage(I18n.EMAIL_MESSAGE_INCORRECT_LOGIN_LIMIT_ACCOUNT_BLOCKED_TITLE, lang),
            I18n.getMessage(I18n.EMAIL_MESSAGE_GREETING, lang)
        );
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

        String templateMessage = loadTemplate("template-with-link.html")
            .replace("$username", name)
            .replace("$link", link)
            .replace("$message", content)
            .replace("$last", signature)
            .replace("$action", action)
            .replace("$title", title)
            .replace("$greeting", greeting);

        sendMimeMessage(receiverAddress, subject, templateMessage);
    }

    /**
     * Sends email with simple message.
     *
     * @param receiverAddress Email address of the receiver.
     * @param name            Name used in greeting.
     * @param content         Body of the message.
     * @param signature       Signature.
     * @param subject         Subject of the message.
     * @param title           Title.
     * @param greeting        Greeting.
     */
    private void sendMessageWithoutLink(
        String receiverAddress, String name, String content, String signature,
        String subject, String title, String greeting) {

        String templateMessage = loadTemplate("template-without-link.html")
            .replace("$username", name)
            .replace("$message", content)
            .replace("$last", signature)
            .replace("$title", title)
            .replace("$greeting", greeting);

        sendMimeMessage(receiverAddress, subject, templateMessage);
    }

    /**
     * Send message with filled in data to user.
     *
     * @param receiverAddress Email address of the receiver.
     * @param subject         Subject of the message.
     * @param message         Message with filled in data.
     */
    private void sendMimeMessage(String receiverAddress, String subject, String message) {
        try {
            InternetAddress[] addresses = {new InternetAddress(receiverAddress)};
            mimeMessage.setRecipients(Message.RecipientType.TO, addresses);
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(message, "text/html; charset=utf-8");
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.log(Level.INFO, "Error while sending an email to" + receiverAddress, e.getCause());
        }
    }

    /**
     * Load message template with given name.
     *
     * @param templateName Name of the template.
     * @return Content of the template.
     */
    private String loadTemplate(String templateName) {
        StringBuilder builder = new StringBuilder();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("templates/" + templateName);
             BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String str;
            while ((str = in.readLine()) != null) {
                builder.append(str);
            }
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Error while reading email message template file", e.getCause());
        }
        return builder.toString();
    }

    public void changeActiveStatusEmail(String to, String name, String language, boolean status) {

        if (status) {
            this.sendMessageWithoutLink(to,
                name,
                I18n.getMessage(I18n.EMAIL_MESSAGE_BLOCKED_ACCOUNT_STATUS_MESSAGE, language),
                I18n.getMessage(I18n.EMAIL_MESSAGE_SIGNATURE, language),
                I18n.getMessage(I18n.EMAIL_MESSAGE_BLOCKED_ACCOUNT_STATUS_ACTION, language),
                I18n.getMessage(I18n.EMAIL_MESSAGE_BLOCKED_ACCOUNT_STATUS_SUBJECT, language),
                I18n.getMessage(I18n.EMAIL_MESSAGE_GREETING, language));
        } else {
            this.sendMessageWithoutLink(to,
                name,
                I18n.getMessage(I18n.EMAIL_MESSAGE_UNBLOCKED_ACCOUNT_STATUS_MESSAGE, language),
                I18n.getMessage(I18n.EMAIL_MESSAGE_SIGNATURE, language),
                I18n.getMessage(I18n.EMAIL_MESSAGE_UNBLOCKED_ACCOUNT_STATUS_ACTION, language),
                I18n.getMessage(I18n.EMAIL_MESSAGE_UNBLOCKED_ACCOUNT_STATUS_SUBJECT, language),
                I18n.getMessage(I18n.EMAIL_MESSAGE_GREETING, language));
        }
    }
}
