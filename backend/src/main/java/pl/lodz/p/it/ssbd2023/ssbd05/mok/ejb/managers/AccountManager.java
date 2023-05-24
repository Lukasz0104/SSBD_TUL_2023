package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.CityDict;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Language;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.ManagerData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Token;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.InvalidTokenException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.LanguageNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.RepeatedPasswordException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.BadAccessLevelException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.IllegalSelfActionException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.InactiveAccountException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.NoAccessLevelException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.SelfAccessManagementException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.UnverifiedAccountException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.unauthorized.InvalidPasswordException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.AccessLevelFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.CityDictFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.TokenFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.AppProperties;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.EmailService;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.HashGenerator;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericManagerExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class AccountManager extends AbstractManager implements AccountManagerLocal, SessionSynchronization {

    @Inject
    private AccountFacade accountFacade;

    @Inject
    private CityDictFacade cityDictFacade;

    @Inject
    private AccessLevelFacade accessLevelFacade;

    @Inject
    private TokenFacade tokenFacade;

    @Inject
    private HashGenerator hashGenerator;

    @Inject
    private EmailService emailService;

    @Inject
    private AppProperties appProperties;

    @Override
    @PermitAll
    public void registerAccount(Account account) throws AppBaseException {
        String hashedPwd = hashGenerator.generate(account.getPassword().toCharArray());
        account.setPassword(hashedPwd);

        accountFacade.create(account);

        Token token =
            new Token(account, appProperties.getAccountConfirmationTime(), TokenType.CONFIRM_REGISTRATION_TOKEN);

        tokenFacade.create(token);

        String actionLink = appProperties.getFrontendUrl() + "/confirm-account?token=" + token.getToken();

        emailService.sendConfirmRegistrationEmail(
            account.getEmail(),
            account.getFullName(),
            actionLink,
            account.getLanguage().toString());
    }

    @Override
    @PermitAll
    public void confirmRegistration(String confirmToken)
        throws AppBaseException {
        Token token = tokenFacade.findByTokenAndTokenType(confirmToken, TokenType.CONFIRM_REGISTRATION_TOKEN)
            .orElseThrow(TokenNotFoundException::new);

        token.validateSelf();

        Account account = token.getAccount();
        account.setVerified(true);

        accountFacade.edit(account);
        saveAddresses(account.getAccessLevels()); //here changes
        tokenFacade.remove(token);
    }

    @Override
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public void changeEmail(String login)
        throws AppBaseException {

        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

        Token token = new Token(account, TokenType.CONFIRM_EMAIL_TOKEN);
        tokenFacade.create(token);

        String link = appProperties.getFrontendUrl() + "/confirm-email/" + token.getToken();
        emailService.changeEmailAddress(
            account.getEmail(), account.getFullName(), link,
            account.getLanguage().toString());
    }

    @Override
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public void confirmEmail(String email, String confirmToken, String login)
        throws AppBaseException {

        Token token = tokenFacade.findByTokenAndTokenType(confirmToken, TokenType.CONFIRM_EMAIL_TOKEN)
            .orElseThrow(TokenNotFoundException::new);

        token.validateSelf();

        Account account = token.getAccount();

        if (!Objects.equals(account.getLogin(), login)) {
            throw new InvalidTokenException();
        }

        tokenFacade.remove(token);

        account.setEmail(email);

        accountFacade.edit(account);

        tokenFacade.removeTokensByAccountIdAndTokenType(account.getId(), TokenType.CONFIRM_EMAIL_TOKEN);
    }

    @Override
    @RolesAllowed({"MANAGER"})
    public void changeActiveStatusAsManager(String managerLogin, Long userId, boolean status)
        throws AppBaseException {
        Account account = accountFacade.find(userId).orElseThrow(AccountNotFoundException::new);
        if (account.hasAccessLevel(AccessType.MANAGER) || account.hasAccessLevel(AccessType.ADMIN)) {
            throw new BadAccessLevelException();
        }
        changeActiveStatus(managerLogin, account, status);
    }

    @Override
    @RolesAllowed({"ADMIN"})
    public void changeActiveStatusAsAdmin(String adminLogin, Long userId, boolean status)
        throws AppBaseException {
        Account account = accountFacade.find(userId).orElseThrow(AccountNotFoundException::new);
        changeActiveStatus(adminLogin, account, status);
    }

    @RolesAllowed({"MANAGER", "ADMIN"})
    private void changeActiveStatus(String adminOrManagerLogin, Account account, boolean status)
        throws AppBaseException {

        if (Objects.equals(adminOrManagerLogin, account.getLogin())) {
            throw new IllegalSelfActionException();
        }
        if (account.isActive() == status) {
            return;
        }

        account.setActive(status);

        accountFacade.edit(account);

        emailService.changeActiveStatusEmail(account.getEmail(), account.getFullName(),
            account.getLanguage().toString(), status);
    }

    @Override
    @PermitAll
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
            appProperties.getFrontendUrl() + "/reset-password-confirm/" + resetPasswordToken.getToken(),
            account.getLanguage().toString());
    }

    @Override
    @PermitAll
    public void resetPassword(String password, String token) throws AppBaseException {
        Token resetPasswordToken = tokenFacade.findByTokenAndTokenType(token, TokenType.PASSWORD_RESET_TOKEN)
            .orElseThrow(TokenNotFoundException::new);
        resetPasswordToken.validateSelf();
        Account account = resetPasswordToken.getAccount();
        if (!account.isActive()) {
            throw new InactiveAccountException();
        }
        tokenFacade.removeTokensByAccountIdAndTokenType(account.getId(), TokenType.PASSWORD_RESET_TOKEN);
        setAccountPassword(account, password.toCharArray());
        accountFacade.edit(account);
    }

    @Override
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public void changePassword(String oldPass, String newPass, String login) throws AppBaseException {

        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

        // check if old password is correct
        if (!hashGenerator.verify(oldPass.toCharArray(), account.getPassword())) {
            throw new InvalidPasswordException();
        }

        setAccountPassword(account, newPass.toCharArray());
        accountFacade.edit(account);
    }


    @Override
    @RolesAllowed("ADMIN")
    public Account getAccountDetails(Long id) throws AppBaseException {
        return accountFacade.find(id).orElseThrow(AccountNotFoundException::new);
    }

    @Override
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public Account getAccountDetails(String login) throws AppBaseException {
        return accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
    }

    @Override
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public AccessType changeAccessLevel(String login, AccessType accessType) throws AppBaseException {
        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

        if (!account.hasAccessLevel(accessType)) {
            throw new NoAccessLevelException();
        }

        return accessType;

    }

    @Override
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
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
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public void changePreferredTheme(String login, boolean lightTheme) throws AppBaseException {
        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
        account.setLightThemePreferred(lightTheme);
        accountFacade.edit(account);
    }

    @Override
    @RolesAllowed({"ADMIN", "MANAGER"})
    public Page<Account> getAllAccounts(boolean active, int page, int pageSize, boolean asc, String phrase,
                                        String login) {
        return accountFacade.findByActive(active, page, pageSize, asc, phrase, login);
    }

    @Override
    @RolesAllowed({"ADMIN", "MANAGER"})
    public Page<Account> getOwnerAccounts(boolean active, int page, int pageSize, boolean asc, String phrase,
                                          String login) {
        return accountFacade.findByActiveAccessLevel(AccessType.OWNER, active, page, pageSize, asc, phrase, login);
    }

    @Override
    @RolesAllowed({"MANAGER"})
    public Page<Account> getUnapprovedOwnerAccounts(int page, int pageSize, boolean asc, String phrase, String login) {
        return accountFacade.findAccountsThatNeedApprovalByAccessLevel(AccessType.OWNER, page, pageSize, asc, phrase,
            login);
    }

    @Override
    @RolesAllowed({"ADMIN"})
    public Page<Account> getManagerAccounts(boolean active, int page, int pageSize, boolean asc, String phrase,
                                            String login) {
        return accountFacade.findByActiveAccessLevel(AccessType.MANAGER, active, page, pageSize, asc, phrase, login);
    }

    @Override
    @RolesAllowed({"ADMIN"})
    public Page<Account> getUnapprovedManagerAccounts(int page, int pageSize, boolean asc, String phrase,
                                                      String login) {
        return accountFacade.findAccountsThatNeedApprovalByAccessLevel(AccessType.MANAGER, page, pageSize, asc, phrase,
            login);
    }

    @Override
    @RolesAllowed({"ADMIN"})
    public Page<Account> getAdminAccounts(boolean active, int page, int pageSize, boolean asc, String phrase,
                                          String login) {
        return accountFacade.findByActiveAccessLevel(AccessType.ADMIN, active, page, pageSize, asc, phrase, login);
    }

    @Override
    @RolesAllowed({"MANAGER", "ADMIN"})
    public List<String> getAccountsLogins(String login) {
        return accountFacade.findAccountsLoginsByLoginLike(login);
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
            if (token.getTokenType() == TokenType.BLOCKED_ACCOUNT_TOKEN) {
                Account account = token.getAccount();
                if (!account.isActive()) {
                    account.setActive(true);
                    accountFacade.edit(account);
                    emailService.changeActiveStatusEmail(account.getEmail(), account.getFullName(),
                        account.getLanguage().toString(), true);
                }
            }
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
            if (timeLeft < (appProperties.getAccountConfirmationTime() / 2.0)) {

                String actionLink = appProperties.getFrontendUrl() + "/confirm-account?token=" + token.getToken();
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
    @RolesAllowed({"ADMIN", "MANAGER"})
    public void grantAccessLevel(Long id, AccessLevel accessLevel, String login) throws AppBaseException {
        Account account = accountFacade.find(id).orElseThrow(AccountNotFoundException::new);

        if (Objects.equals(login, account.getLogin())) {
            throw new SelfAccessManagementException();
        }

        AtomicBoolean wasActive = new AtomicBoolean(false);

        Optional<AccessLevel> accessLevelOptional = account.getAccessLevels()
            .stream()
            .filter(al -> al.getLevel() == accessLevel.getLevel())
            .findFirst();
        if (accessLevelOptional.isPresent()) {
            AccessLevel al = accessLevelOptional.get();
            wasActive.set(al.isActive());

            al.setVerified(true);
            al.setActive(true);
            accessLevelFacade.edit(al);
        } else {
            accessLevel.setAccount(account);
            accessLevel.setActive(true);
            accessLevel.setVerified(true);
            account.getAccessLevels().add(accessLevel);
            accessLevelFacade.create(accessLevel);
        }


        if (!wasActive.get()) {
            emailService.notifyAboutNewAccessLevel(
                account.getEmail(),
                account.getFullName(),
                account.getLanguage().toString(),
                accessLevel.getLevel());
        }
    }

    @Override
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public Account editPersonalData(Account newData, String login) throws AppBaseException {
        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
        if (account.getVersion() != newData.getVersion()) {
            throw new AppOptimisticLockException();
        }
        account.setFirstName(newData.getFirstName());
        account.setLastName(newData.getLastName());
        editAccessLevels(account.getAccessLevels(), newData);
        accountFacade.edit(account);
        saveAddresses(account.getAccessLevels()); //here changes
        return account;
    }

    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    private void editAccessLevels(Set<AccessLevel> accessLevels, Account newData) throws AppBaseException {
        for (AccessLevel newAccessLevel : newData.getAccessLevels()) {
            Optional<AccessLevel> optAccessLevel =
                accessLevels.stream().filter(x -> x.getLevel().equals(newAccessLevel.getLevel()))
                    .findFirst();

            if (optAccessLevel.isPresent()) {
                AccessLevel accessLevel = optAccessLevel.get();

                if (!accessLevel.isActive()) {
                    continue;
                }

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
    @RolesAllowed({"ADMIN"})
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
        saveAddresses(accountOrig.getAccessLevels()); //here changes
        return accountOrig;
    }

    @Override
    @RolesAllowed({"ADMIN"})
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
        setAccountPassword(account, new String(array, StandardCharsets.UTF_8).toCharArray());
        account.setActive(false);
        accountFacade.edit(account);

        Token passwordChangeToken = new Token(account, TokenType.OVERRIDE_PASSWORD_CHANGE_TOKEN);
        tokenFacade.create(passwordChangeToken);
        String link = appProperties.getFrontendUrl() + "/force-password-override/" + passwordChangeToken.getToken();
        emailService.forcePasswordChangeEmail(account.getEmail(), account.getFullName(),
            account.getLanguage().toString(), link);
    }

    @Override
    @PermitAll
    public void overrideForcedPassword(String password, String token) throws AppBaseException {
        Token overridePasswordChangeToken =
            tokenFacade.findByTokenAndTokenType(token, TokenType.OVERRIDE_PASSWORD_CHANGE_TOKEN)
                .orElseThrow(TokenNotFoundException::new);
        overridePasswordChangeToken.validateSelf();

        Account account = overridePasswordChangeToken.getAccount();
        setAccountPassword(account, password.toCharArray());
        tokenFacade.removeTokensByAccountIdAndTokenType(account.getId(), TokenType.OVERRIDE_PASSWORD_CHANGE_TOKEN);
        account.setActive(true);
        accountFacade.edit(account);
    }

    @Override
    @RolesAllowed({"ADMIN", "MANAGER"})
    public void revokeAccessLevel(Long id, AccessType accessType, String login) throws AppBaseException {
        Account account = accountFacade.find(id).orElseThrow(AccountNotFoundException::new);

        if (Objects.equals(login, account.getLogin())) {
            throw new SelfAccessManagementException();
        }

        Optional<AccessLevel> accessLevel = account.getAccessLevels()
            .stream()
            .filter(al -> al.getLevel() == accessType)
            .findFirst();

        if (accessLevel.isPresent()) {
            accessLevel.get().setActive(false);
            accessLevel.get().setVerified(true);
            accountFacade.edit(account);

            emailService.notifyAboutRevokedAccessLevel(
                account.getEmail(),
                account.getFullName(),
                account.getLanguage().toString(),
                accessType
            );
        }
    }

    @Override
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public void changeTwoFactorAuthStatus(String login, Boolean status)
        throws AppBaseException {
        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
        account.setTwoFactorAuth(status);
        accountFacade.edit(account);
    }

    @PermitAll
    private void saveAddresses(Set<AccessLevel> levelSet) throws AppBaseException {
        for (AccessLevel level : levelSet) {
            if (level instanceof OwnerData ownerData) {
                cityDictFacade.create(new CityDict(ownerData.getAddress().getCity()));
            } else if (level instanceof ManagerData managerData) {
                cityDictFacade.create(new CityDict(managerData.getAddress().getCity()));
            }
        }
    }

    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    private void setAccountPassword(Account account, char[] newPasswd) throws RepeatedPasswordException {
        if (account.getPastPasswords().stream().anyMatch((oldPass) -> hashGenerator.verify(newPasswd, oldPass))) {
            throw new RepeatedPasswordException();
        }

        account.getPastPasswords().add(account.getPassword());
        account.setPassword(hashGenerator.generate(newPasswd));
    }
}
