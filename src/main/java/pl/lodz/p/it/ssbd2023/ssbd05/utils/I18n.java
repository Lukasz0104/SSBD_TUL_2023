package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import jakarta.ejb.Stateless;

import java.util.Locale;
import java.util.ResourceBundle;

@Stateless
public class I18n {

    public static String RESET_PASSWORD_EMAIL_MESSAGE_ACTION = "reset.password.email.message.action";
    public static String RESET_PASSWORD_EMAIL_MESSAGE_CONTENT = "reset.password.email.message.content";
    public static String RESET_PASSWORD_EMAIL_MESSAGE_SUBJECT = "reset.password.email.message.subject";
    public static String BLOCKED_ACCOUNT_STATUS_EMAIL_MESSAGE_ACTION = "blocked.account.status.email.message.action";
    public static String BLOCKED_ACCOUNT_STATUS_EMAIL_MESSAGE_CONTENT = "blocked.account.status.email.message.content";
    public static String BLOCKED_ACCOUNT_STATUS_MESSAGE_SUBJECT = "blocked.account.status.email.message.subject";
    public static String UNBLOCKED_ACCOUNT_STATUS_EMAIL_MESSAGE_ACTION
        = "unblocked.account.status.email.message.action";
    public static String UNBLOCKED_ACCOUNT_STATUS_EMAIL_MESSAGE_CONTENT
        = "unblocked.account.status.email.message.content";
    public static String UNBLOCKED_ACCOUNT_STATUS_MESSAGE_SUBJECT = "unblocked.account.status.email.message.subject";
    public static String EMAIL_MESSAGE_LAST = "email.message.last";
    public static String EMAIL_MESSAGE_GREETING = "email.message.greeting";

    public String getMessage(String key, String language) {
        Locale locale = new Locale(language);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        return resourceBundle.getString(key);
    }
}
