package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Language;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.ManagerData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Token;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.LanguageNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.RepeatedPasswordException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.ConstraintViolationException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.BadAccessLevelException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.IllegalSelfActionException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.InactiveAccountException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.NoAccessLevelException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.SelfAccessManagementException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.UnverifiedAccountException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.unauthorized.AuthenticationException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.unauthorized.InvalidPasswordException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.TokenFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.EmailService;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.HashGenerator;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.Properties;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericManagerExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
public class AccountManager extends AbstractManager implements AccountManagerLocal, SessionSynchronization {

    @Inject
    private AccountFacade accountFacade;

    @Inject
    private TokenFacade tokenFacade;

    @Inject
    private HashGenerator hashGenerator;

    @Inject
    private EmailService emailService;

    @Inject
    private Properties properties;

    @Override
    public void registerAccount(Account account) throws AppBaseException {
        String hashedPwd = hashGenerator.generate(account.getPassword().toCharArray());
        account.setPassword(hashedPwd);

        accountFacade.create(account);

        Token token = new Token(account, properties.getAccountConfirmationTime(), TokenType.CONFIRM_REGISTRATION_TOKEN);

        tokenFacade.create(token);

        String actionLink = properties.getFrontendUrl() + "/confirm-account?token=" + token.getToken();

        emailService.sendConfirmRegistrationEmail(
            account.getEmail(),
            account.getFullName(),
            actionLink,
            account.getLanguage().toString());
    }

    @Override
    public void confirmRegistration(UUID confirmToken)
        throws AppBaseException {
        Token token = tokenFacade.findByToken(confirmToken).orElseThrow(TokenNotFoundException::new);

        token.validateSelf(TokenType.CONFIRM_REGISTRATION_TOKEN);

        Account account = token.getAccount();
        account.setVerified(true);

        accountFacade.edit(account); // TODO Catch and handle DatabaseException
        tokenFacade.remove(token);
    }

    @Override
    public void changeEmail(String login)
        throws AppBaseException {

        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

        List<Token> tokenList = tokenFacade.findByAccountLoginAndTokenType(login, TokenType.CONFIRM_EMAIL_TOKEN);

        for (Token token : tokenList) {
            tokenFacade.remove(token);
        }

        Token token = new Token(account, TokenType.CONFIRM_EMAIL_TOKEN);
        tokenFacade.create(token);

        String link = properties.getFrontendUrl() + "/change-email?token=" + token.getToken();
        emailService.changeEmailAddress(
            account.getEmail(), account.getFullName(), link,
            account.getLanguage().toString());
    }

    @Override
    public void confirmEmail(String email, UUID confirmToken, String login)
        throws AppBaseException {

        Token token = tokenFacade.findByToken(confirmToken).orElseThrow(TokenNotFoundException::new);

        token.validateSelf(TokenType.CONFIRM_EMAIL_TOKEN);

        Account account = token.getAccount();

        if (!Objects.equals(account.getLogin(), login)) {
            throw new AuthenticationException();
        }

        tokenFacade.remove(token);

        account.setEmail(email);

        try {
            accountFacade.edit(account);
        } catch (AppDatabaseException de) {
            throw new ConstraintViolationException(de.getMessage(), de);
        }
    }

    @Override
    public void changeActiveStatusAsManager(String managerLogin, Long userId, boolean status)
        throws AppBaseException {
        Account account = accountFacade.find(userId).orElseThrow(AccountNotFoundException::new);
        if (account.hasAccessLevel(AccessType.MANAGER) || account.hasAccessLevel(AccessType.ADMIN)) {
            throw new BadAccessLevelException();
        }
        changeActiveStatus(managerLogin, account, status);
    }

    private void changeActiveStatus(String adminOrManagerLogin, Account account, boolean status)
        throws AppBaseException {

        if (Objects.equals(adminOrManagerLogin, account.getLogin())) {
            throw new IllegalSelfActionException();
        }
        if (account.isActive() == status) {
            return;
        }

        account.setActive(status);
        try {
            accountFacade.edit(account);
        } catch (AppDatabaseException ade) {
            throw new ConstraintViolationException(ade.getMessage(), ade);
        }

        emailService.changeActiveStatusEmail(account.getEmail(), account.getFullName(),
            account.getLanguage().toString(), status);
    }

    @Override
    public void changeActiveStatusAsAdmin(String adminLogin, Long userId, boolean status)
        throws AppBaseException {
        Account account = accountFacade.find(userId).orElseThrow(AccountNotFoundException::new);
        changeActiveStatus(adminLogin, account, status);
    }

    @Override
    public void sendResetPasswordMessage(String email) throws AppBaseException {
        Account account = accountFacade.findByEmail(email).orElseThrow(AccountNotFoundException::new);
        if (!account.isVerified()) {
            throw new UnverifiedAccountException();
        }
        if (!account.isActive()) {
            throw new InactiveAccountException();
        }

        Token resetPasswordToken = new Token(account, TokenType.PASSWORD_RESET_TOKEN);
        tokenFacade.create(resetPasswordToken);
        emailService.resetPasswordEmail(account.getEmail(), account.getFullName(),
            properties.getFrontendUrl() + "/" + resetPasswordToken.getToken(), account.getLanguage().toString());
    }

    @Override
    public void resetPassword(String password, UUID token) throws AppBaseException {
        Token resetPasswordToken = tokenFacade.findByTokenAndTokenType(token, TokenType.PASSWORD_RESET_TOKEN)
            .orElseThrow(TokenNotFoundException::new);
        resetPasswordToken.validateSelf();
        Account account = resetPasswordToken.getAccount();
        if (!account.isActive()) {
            throw new InactiveAccountException();
        }
        tokenFacade.removeTokensByAccountIdAndTokenType(account.getId(), TokenType.PASSWORD_RESET_TOKEN);
        account.setPassword(hashGenerator.generate(password.toCharArray()));
        accountFacade.edit(account);
    }

    @Override
    public void changePassword(String oldPass, String newPass, String login) throws AppBaseException {

        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

        // check if old password is correct
        if (!hashGenerator.verify(oldPass.toCharArray(), account.getPassword())) {
            throw new InvalidPasswordException();
        } else if (hashGenerator.verify(newPass.toCharArray(), account.getPassword())) {
            // check if new password is same as old password
            // TODO: zmienić na sprawdzanie histori haseł jak będziemy robić to rozszerzenie
            throw new RepeatedPasswordException();
        }

        account.setPassword(hashGenerator.generate(newPass.toCharArray()));
        accountFacade.edit(account);
    }


    @Override
    public Account getAccountDetails(Long id) throws AppBaseException {
        return accountFacade.find(id).orElseThrow(AccountNotFoundException::new);
    }

    @Override
    public Account getAccountDetails(String login) throws AppBaseException {
        return accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
    }

    @Override
    public AccessType changeAccessLevel(String login, AccessType accessType) throws AppBaseException {
        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

        if (!account.hasAccessLevel(accessType)) {
            throw new NoAccessLevelException();
        }

        return accessType;

    }

    @Override
    public void changeAccountLanguage(String login, String language) throws AppBaseException {
        Account account = accountFacade.findByLogin(login)
            .orElseThrow(AccountNotFoundException::new);
        try {
            account.setLanguage(Language.valueOf(language));
        } catch (IllegalArgumentException e) {
            throw new LanguageNotFoundException();
        }
        accountFacade.edit(account);
    }

    @Override
    public List<Account> getAllAccounts(boolean active) {
        return accountFacade.findByActive(active);
    }

    @Override
    public List<Account> getOwnerAccounts(boolean active) {
        return accountFacade.findByActiveAccessLevel(AccessType.OWNER, active);
    }

    @Override
    public List<Account> getManagerAccounts(boolean active) {
        return accountFacade.findByActiveAccessLevel(AccessType.MANAGER, active);
    }

    @Override
    public List<Account> getAdminAccounts(boolean active) {
        return accountFacade.findByActiveAccessLevel(AccessType.ADMIN, active);
    }

    @Override
    public void deleteUnverifiedAccounts(LocalDateTime now) throws AppBaseException {
        List<Token> unverifiedTokens =
            tokenFacade.findByTokenTypeAndExpiresAtBefore(TokenType.CONFIRM_REGISTRATION_TOKEN, now);
        for (Token token : unverifiedTokens) {
            Account account = token.getAccount();
            tokenFacade.remove(token);
            accountFacade.remove(account);
            emailService.sendConfirmRegistrationFailEmail(
                account.getEmail(),
                account.getFullName(),
                account.getLanguage().toString()
            );
        }
    }

    @Override
    public void deleteExpiredTokens(LocalDateTime now) throws AppBaseException {
        List<Token> expiredTokens = tokenFacade
            .findByNotTokenTypeAndExpiresAtBefore(TokenType.CONFIRM_REGISTRATION_TOKEN, now);
        for (Token token : expiredTokens) {
            tokenFacade.remove(token);
        }
    }

    @Override
    public void remindToConfirmRegistration(LocalDateTime now) {
        List<Token> unverifiedTokens =
            tokenFacade.findByTokenTypeAndExpiresAtAfter(TokenType.CONFIRM_REGISTRATION_TOKEN, now);
        for (Token token : unverifiedTokens) {
            Account account = token.getAccount();
            if (account.isReminded()) {
                continue;
            }
            long timeLeft = Duration.between(now, token.getExpiresAt()).toMillis();
            if (timeLeft < (properties.getAccountConfirmationTime() / 2.0)) {

                String actionLink = properties.getFrontendUrl() + "/confirm-account?token=" + token.getToken();
                account.setReminded(true);
                emailService.sendConfirmRegistrationReminderEmail(
                    account.getEmail(),
                    account.getFullName(),
                    actionLink,
                    token.getExpiresAt(),
                    account.getLanguage().toString());
            }
        }
    }

    /**
     * Add access level to given account or mark it as active if it already exists.
     *
     * @param id          account id
     * @param accessLevel access level to be added
     * @param login       login of currently authenticated user
     * @throws AppBaseException When account was not found or adding access level failed.
     */
    @Override
    public void grantAccessLevel(Long id, AccessLevel accessLevel, String login) throws AppBaseException {
        Account account = accountFacade.find(id).orElseThrow(AccountNotFoundException::new);

        if (Objects.equals(login, account.getLogin())) {
            throw new SelfAccessManagementException();
        }

        AtomicBoolean wasActive = new AtomicBoolean(false);

        account.getAccessLevels()
            .stream()
            .filter(al -> al.getLevel() == accessLevel.getLevel())
            .findFirst()
            .ifPresentOrElse(al -> {
                wasActive.set(al.isActive());

                al.setVerified(true);
                al.setActive(true);

                if (accessLevel instanceof OwnerData ownerData) {
                    ((OwnerData) al).setAddress(ownerData.getAddress());
                } else if (accessLevel instanceof ManagerData managerData) {
                    ManagerData md = (ManagerData) al;
                    md.setLicenseNumber(managerData.getLicenseNumber());
                    md.setAddress(managerData.getAddress());
                }
            }, () -> {
                accessLevel.setAccount(account);
                accessLevel.setActive(true);
                accessLevel.setVerified(true);
                account.getAccessLevels().add(accessLevel);
            });

        accountFacade.edit(account);

        if (!wasActive.get()) {
            emailService.notifyAboutNewAccessLevel(
                account.getEmail(),
                account.getFullName(),
                account.getLanguage().toString(),
                accessLevel.getLevel());
        }
    }

    @Override
    public Account editPersonalData(Account newData, String login) throws AppBaseException {
        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
        if (account.getVersion() != newData.getVersion()) {
            throw new AppOptimisticLockException();
        }
        account.setFirstName(newData.getFirstName());
        account.setLastName(newData.getLastName());
        editAccessLevels(account.getAccessLevels(), newData);
        accountFacade.edit(account);
        return account;
    }

    private void editAccessLevels(Set<AccessLevel> accessLevels, Account newData) throws AppBaseException {
        for (AccessLevel newAccessLevel : newData.getAccessLevels()) {
            Optional<AccessLevel> optAccessLevel =
                accessLevels.stream().filter(x -> x.getLevel().equals(newAccessLevel.getLevel()))
                    .findFirst();

            if (optAccessLevel.isPresent()) {
                AccessLevel accessLevel = optAccessLevel.get();

                if (newAccessLevel.getVersion() != accessLevel.getVersion()) {
                    throw new AppOptimisticLockException();
                }

                switch (newAccessLevel.getLevel()) {
                    case OWNER -> {
                        OwnerData ownerData = (OwnerData) accessLevel;
                        OwnerData newOwnerData = (OwnerData) newAccessLevel;
                        ownerData.setAddress(newOwnerData.getAddress());
                    }
                    case MANAGER -> {
                        ManagerData managerData = (ManagerData) accessLevel;
                        ManagerData newManagerData = (ManagerData) newAccessLevel;
                        managerData.setAddress(newManagerData.getAddress());
                        managerData.setLicenseNumber(newManagerData.getLicenseNumber());
                    }
                    default -> {
                    }
                }
            }

        }
    }

    @Override
    public Account editPersonalDataByAdmin(Account newData) throws AppBaseException {

        Account accountOrig = accountFacade.findByLogin(newData.getLogin()).orElseThrow(AccountNotFoundException::new);
        if (accountOrig.getVersion() != newData.getVersion()) {
            throw new AppOptimisticLockException();
        }

        accountOrig.setEmail(newData.getEmail());
        accountOrig.setFirstName(newData.getFirstName());
        accountOrig.setLastName(newData.getLastName());
        accountOrig.setLanguage(newData.getLanguage());

        editAccessLevels(accountOrig.getAccessLevels(), newData);
        accountFacade.edit(accountOrig);
        return accountOrig;
    }

    @Override
    public void forcePasswordChange(String login) throws AppBaseException {
        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

        if (!account.isActive()) {
            throw new InactiveAccountException();
        }
        if (!account.isVerified()) {
            throw new UnverifiedAccountException();
        }

        byte[] array = new byte[28];
        new Random().nextBytes(array);
        account.setPassword(hashGenerator.generate(new String(array, StandardCharsets.UTF_8).toCharArray()));
        account.setActive(false);
        accountFacade.edit(account);

        Token passwordChangeToken = new Token(account, TokenType.OVERRIDE_PASSWORD_CHANGE_TOKEN);
        tokenFacade.create(passwordChangeToken);
        String link = properties.getFrontendUrl() + "/" + passwordChangeToken.getToken();
        emailService.forcePasswordChangeEmail(account.getEmail(), account.getFullName(),
            account.getLanguage().toString(), link);
    }

    @Override
    public void overrideForcedPassword(String password, UUID token) throws AppBaseException {
        Token overridePasswordChangeToken =
            tokenFacade.findByTokenAndTokenType(token, TokenType.OVERRIDE_PASSWORD_CHANGE_TOKEN)
                .orElseThrow(TokenNotFoundException::new);
        overridePasswordChangeToken.validateSelf();

        Account account = overridePasswordChangeToken.getAccount();
        tokenFacade.removeTokensByAccountIdAndTokenType(account.getId(), TokenType.OVERRIDE_PASSWORD_CHANGE_TOKEN);
        account.setPassword(hashGenerator.generate(password.toCharArray()));
        account.setActive(true);
        accountFacade.edit(account);
    }

    @Override
    public void revokeAccessLevel(Long id, AccessType accessType, String login) throws AppBaseException {
        Account account = accountFacade.find(id).orElseThrow(AccountNotFoundException::new);

        if (Objects.equals(login, account.getLogin())) {
            throw new SelfAccessManagementException();
        }

        Optional<AccessLevel> accessLevel = account.getAccessLevels()
            .stream()
            .filter(al -> al.isValidAccessLevel(accessType))
            .findFirst();

        if (accessLevel.isPresent()) {
            accessLevel.get().setActive(false);
            accountFacade.edit(account);

            emailService.notifyAboutRevokedAccessLevel(
                account.getEmail(),
                account.getFullName(),
                account.getLanguage().toString(),
                accessType
            );
        }
    }
}
