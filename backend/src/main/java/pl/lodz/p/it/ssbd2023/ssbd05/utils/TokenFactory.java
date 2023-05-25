package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Token;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.TokenType;

import java.time.LocalDateTime;

@ApplicationScoped
public class TokenFactory {

    @Inject
    private AppProperties appProperties;

    // Basic
    public Token createRefreshToken(Account account) {

        return new Token(account, TokenType.REFRESH_TOKEN,
            LocalDateTime.now().plusMinutes(appProperties.getTokenExpireRefresh()));
    }

    public Token createConfirmRegistrationToken(Account account) {
        return new Token(account, TokenType.CONFIRM_REGISTRATION_TOKEN,
            LocalDateTime.now().plusMinutes(appProperties.getTokenExpireConfirmRegister()));
    }

    public Token createPasswordResetToken(Account account) {
        return new Token(account, TokenType.PASSWORD_RESET_TOKEN,
            LocalDateTime.now().plusMinutes(appProperties.getTokenExpirePasswordReset()));
    }

    public Token createOverridePasswordChangeToken(Account account) {
        return new Token(account, TokenType.OVERRIDE_PASSWORD_CHANGE_TOKEN,
            LocalDateTime.now().plusMinutes(appProperties.getTokenExpireOverridePasswordChange()));
    }

    public Token createConfirmEmailToken(Account account) {
        return new Token(account, TokenType.CONFIRM_EMAIL_TOKEN,
            LocalDateTime.now().plusMinutes(appProperties.getTokenExpireConfirmEmail()));
    }

    public Token createBlockedAccountToken(Account account) {
        return new Token(account, TokenType.BLOCKED_ACCOUNT_TOKEN,
            LocalDateTime.now().plusMinutes(appProperties.getTokenExpireBlockedAccount()));
    }

    public Token createTwoFactorAuthToken(Account account, String token) {
        return new Token(token, account, TokenType.TWO_FACTOR_AUTH_TOKEN,
            LocalDateTime.now().plusMinutes(appProperties.getTokenExpireTwoFactorAuth()));
    }

    public Token createUnlockAccountSelfToken(Account account) {
        return new Token(account, TokenType.UNLOCK_ACCOUNT_SELF_TOKEN,
            LocalDateTime.now().plusMinutes(appProperties.getTokenExpireUnlockAccountSelf()));
    }
}
