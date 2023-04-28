package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import jakarta.ejb.Stateless;

import java.util.Locale;
import java.util.ResourceBundle;

@Stateless
public class I18n {
    public static final String EMAIL_MESSAGE_SIGNATURE = "email.message.signature";
    public static final String EMAIL_MESSAGE_GREETING = "email.message.greeting";

    public static final String EMAIL_MESSAGE_RESET_PASSWORD_ACTION = "email.message.reset-password.action";
    public static final String EMAIL_MESSAGE_RESET_PASSWORD_MESSAGE = "email.message.reset-password.message";
    public static final String EMAIL_MESSAGE_RESET_PASSWORD_SUBJECT = "email.message.reset-password.subject";
    public static final String EMAIL_MESSAGE_RESET_PASSWORD_TITLE = "email.message.reset-password.title";

    public static final String EMAIL_MESSAGE_CONFIRM_ACCOUNT_ACTION = "email.message.confirm-account.action";
    public static final String EMAIL_MESSAGE_CONFIRM_ACCOUNT_MESSAGE = "email.message.confirm-account.message";
    public static final String EMAIL_MESSAGE_CONFIRM_ACCOUNT_SUBJECT = "email.message.confirm-account.subject";
    public static final String EMAIL_MESSAGE_CONFIRM_ACCOUNT_TITLE = "email.message.confirm-account.title";

    public static final String EMAIL_MESSAGE_CHANGE_EMAIL_ACTION = "email.message.change-email.action";
    public static final String EMAIL_MESSAGE_CHANGE_EMAIL_MESSAGE = "email.message.change-email.message";
    public static final String EMAIL_MESSAGE_CHANGE_EMAIL_SUBJECT = "email.message.change-email.subject";
    public static final String EMAIL_MESSAGE_CHANGE_EMAIL_TITLE = "email.message.change-email.title";

    public static final String EMAIL_MESSAGE_INCORRECT_LOGIN_LIMIT_ACCOUNT_BLOCKED_MESSAGE =
        "email.message.incorrect-login-limit-account-blocked.message";
    public static final String EMAIL_MESSAGE_INCORRECT_LOGIN_LIMIT_ACCOUNT_BLOCKED_SUBJECT =
        "email.message.incorrect-login-limit-account-blocked.subject";
    public static final String EMAIL_MESSAGE_INCORRECT_LOGIN_LIMIT_ACCOUNT_BLOCKED_TITLE =
        "email.message.incorrect-login-limit-account-blocked.title";

    public static String getMessage(String key, String language) {
        Locale locale = new Locale(language);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        return resourceBundle.getString(key);
    }
}
