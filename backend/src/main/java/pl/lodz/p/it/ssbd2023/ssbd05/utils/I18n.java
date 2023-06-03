package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import jakarta.ejb.Stateless;

import java.util.Locale;
import java.util.ResourceBundle;

@Stateless
public class I18n {
    public static final String ACCESS_LEVEL_ADMINISTRATOR = "access-level.administrator";
    public static final String ACCESS_LEVEL_MANAGER = "access-level.manager";
    public static final String ACCESS_LEVEL_OWNER = "access-level.owner";
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
    public static final String EMAIL_MESSAGE_CONFIRM_ACCOUNT_REMINDER_TITLE =
        "email.message.confirm-account.reminder.title";
    public static final String EMAIL_MESSAGE_CONFIRM_ACCOUNT_REMINDER_SUBJECT =
        "email.message.confirm-account.reminder.subject";
    public static final String EMAIL_MESSAGE_CONFIRM_ACCOUNT_REMINDER_MESSAGE =
        "email.message.confirm-account.reminder.message";
    public static final String EMAIL_MESSAGE_CONFIRM_ACCOUNT_FAIL_MESSAGE =
        "email.message.confirm-account.fail.message";
    public static final String EMAIL_MESSAGE_CONFIRM_ACCOUNT_FAIL_SUBJECT =
        "email.message.confirm-account.fail.subject";
    public static final String EMAIL_MESSAGE_CONFIRM_ACCOUNT_FAIL_TITLE = "email.message.confirm-account.fail.title";
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
    public static final String EMAIL_MESSAGE_ADMIN_AUTH_SUCCESS_TITLE =
        "email.message.admin-auth-success.title";
    public static final String EMAIL_MESSAGE_ADMIN_AUTH_SUCCESS_MESSAGE =
        "email.message.admin-auth-success.message";
    public static final String EMAIL_MESSAGE_ADMIN_AUTH_SUCCESS_SUBJECT =
        "email.message.admin-auth-success.subject";
    public static final String EMAIL_MESSAGE_BLOCKED_ACCOUNT_STATUS_ACTION =
        "email.message.blocked.account.status.action";
    public static final String EMAIL_MESSAGE_BLOCKED_ACCOUNT_STATUS_MESSAGE =
        "email.message.blocked.account.status.message";
    public static final String EMAIL_MESSAGE_BLOCKED_ACCOUNT_STATUS_SUBJECT =
        "email.message.blocked.account.status.subject";
    public static final String EMAIL_MESSAGE_UNBLOCKED_ACCOUNT_STATUS_ACTION =
        "email.message.unblocked.account.status.action";
    public static final String EMAIL_MESSAGE_UNBLOCKED_ACCOUNT_STATUS_MESSAGE =
        "email.message.unblocked.account.status.message";
    public static final String EMAIL_MESSAGE_UNBLOCKED_ACCOUNT_STATUS_SUBJECT =
        "email.message.unblocked.account.status.subject";
    public static final String EMAIL_MESSAGE_ACCESS_LEVEL_GRANTED_MESSAGE =
        "email.message.access-level-granted.message";
    public static final String EMAIL_MESSAGE_ACCESS_LEVEL_GRANTED_SUBJECT =
        "email.message.access-level-granted.subject";
    public static final String EMAIL_MESSAGE_ACCESS_LEVEL_GRANTED_TITLE = "email.message.access-level-granted.title";
    public static final String EMAIL_MESSAGE_ACCESS_LEVEL_REVOKED_MESSAGE =
        "email.message.access-level-revoked.message";
    public static final String EMAIL_MESSAGE_ACCESS_LEVEL_REVOKED_SUBJECT =
        "email.message.access-level-revoked.subject";
    public static final String EMAIL_MESSAGE_ACCESS_LEVEL_REVOKED_TITLE =
        "email.message.access-level-revoked.title";
    public static final String EMAIL_MESSAGE_FORCE_PASSWORD_CHANGE_TITLE = "email.message.force-password-change.title";
    public static final String EMAIL_MESSAGE_FORCE_PASSWORD_CHANGE_SUBJECT =
        "email.message.force-password-change.subject";
    public static final String EMAIL_MESSAGE_FORCE_PASSWORD_CHANGE_MESSAGE =
        "email.message.force-password-change.message";
    public static final String EMAIL_MESSAGE_FORCE_PASSWORD_CHANGE_ACTION =
        "email.message.force-password-change.action";

    public static final String EMAIL_MESSAGE_INACTIVITY_ACCOUNT_LOCKED_MESSAGE =
        "email.message.inactivity-account-locked.message";
    public static final String EMAIL_MESSAGE_INACTIVITY_ACCOUNT_LOCKED_SUBJECT =
        "email.message.inactivity-account-locked.subject";
    public static final String EMAIL_MESSAGE_INACTIVITY_ACCOUNT_LOCKED_TITLE =
        "email.message.inactivity-account-locked.title";
    public static final String EMAIL_MESSAGE_INACTIVITY_ACCOUNT_LOCKED_ACTION =
        "email.message.inactivity-account-locked.action";

    public static final String EMAIL_MESSAGE_TWO_FACTOR_CODE_MESSAGE =
        "email.message.two-factor-code.message";

    public static final String EMAIL_MESSAGE_TWO_FACTOR_CODE_TITLE =
        "email.message.two-factor-code.title";

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
    public static final String AUTHENTICATION_EXCEPTION = "response.message.authentication_exception";
    public static final String INVALID_PASSWORD = "response.message.invalid_password";
    public static final String LANGUAGE_NOT_FOUND = "response.message.language_not_found";
    public static final String LICENSE_NUMBER_ALREADY_TAKEN = "response.message.license-number-already-taken";
    public static final String EMAIL_ADDRESS_ALREADY_TAKEN = "response.message.email-address-already-taken";
    public static final String LOGIN_ALREADY_TAKEN = "response.message.login-already-taken";
    public static final String ACCESS_LEVEL_NOT_FOUND = "response.message.access_level_not_found";
    public static final String FORCE_PASSWORD_CHANGE_DATABASE_EXCEPTION =
        "response.message.force_password_change_database_exception";
    public static final String ILLEGAL_SELF_ACTION = "response.message.illegal_self_action";
    public static final String OVERRIDE_FORCED_PASSWORD_DATABASE_EXCEPTION =
        "response.message.override_forced_password_database_exception";
    public static final String ACCESS_MANAGEMENT_SELF = "response.message.access-management-self";
    public static final String BAD_ACCESS_LEVEL = "response.message.bad-access-level";

    public static final String SIGNATURE_MISMATCH = "response.message.signature_mismatch";

    public static final String ROLLBACK_LIMIT_EXCEEDED = "response.message.rollback.limit.exceeded";

    public static final String BAD_REQUEST = "response.message.bad_request";
    public static final String CONFLICT = "response.message.conflict";
    public static final String FORBIDDEN = "response.message.forbidden";
    public static final String INTERNAL = "response.message.internal";
    public static final String NOT_FOUND = "response.message.not_found";
    public static final String UNAUTHORIZED = "response.message.unauthorized";
    public static final String INVALID_CAPTCHA_CODE = "response.message.invalid_captcha";
    public static final String INVALID_UUID = "response.message.invalid.uuid";
    public static final String NO_ACCESS_LEVEL = "response.message.no_access_level";
    public static final String ACCESS_LEVEL_ALREADY_GRANTED = "response.message.access-level-already-granted";

    // MOW
    public static final String PLACE_NOT_FOUND = "response.message.place_not_found";
    public static final String METER_NOT_FOUND = "response.message.meter_not_found";


    public static String getMessage(String key, String language) {
        Locale locale = new Locale(language);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        return resourceBundle.getString(key);
    }
}
