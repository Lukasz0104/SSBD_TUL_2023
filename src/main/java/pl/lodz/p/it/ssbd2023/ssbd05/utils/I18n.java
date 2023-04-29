package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import jakarta.ejb.Stateless;

import java.util.Locale;
import java.util.ResourceBundle;

@Stateless
public class I18n {

    public static String RESET_PASSWORD_EMAIL_MESSAGE_ACTION = "reset.password.email.message.action";
    public static String RESET_PASSWORD_EMAIL_MESSAGE_CONTENT = "reset.password.email.message.content";
    public static String RESET_PASSWORD_EMAIL_MESSAGE_SUBJECT = "reset.password.email.message.subject";
    public static String EMAIL_MESSAGE_LAST = "email.message.last";
    public static String EMAIL_MESSAGE_GREETING = "email.message.greeting";

    public static final String EXPIRED_TOKEN = "response.message.expired_token";
    public static final String INVALID_TOKEN_TYPE = "response.message.invalid_token_type";
    public static final String TOKEN_NOT_FOUND = "response.message.token_not_found";
    public static final String PASSWORD_CONSTRAINT = "response.message.password_constraint";
    public static final String PASSWORD_NOT_MATCH = "response.message.password_not_match";
    public static final String REPEATED_PASSWORD = "response.message.repeated_password";
    public static final String OPTIMISTIC_LOCK = "response.message.optimistic_lock";
    public static final String CONSTRAINT_VIOLATION = "response.message.constraint_violation";
    public static final String INACTIVE_ACCOUNT = "response.message.inactive_account";
    public static final String ACCOUNT_NOT_FOUND = "response.message.account_not_found";
    public static final String UNVERIFIED_ACCOUNT = "response.message.unverified_account";
    public static final String AUTHENTICATION_EXCEPTION = "response.message.authentiaction_exception";
    public static final String INVALID_PASSWORD = "response.message.invalid_password";

    public static final String BAD_REQUEST = "response.message.bad_request";
    public static final String CONFLICT = "response.message.conflict";
    public static final String FORBIDDEN = "response.message.forbidden";
    public static final String INTERNAL = "response.message.internal";
    public static final String NOT_FOUND = "response.message.not_found";
    public static final String UNAUTHORIZED = "response.message.unauthorized";


    public String getMessage(String key, String language) {
        Locale locale = new Locale(language);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        return resourceBundle.getString(key);
    }
}
