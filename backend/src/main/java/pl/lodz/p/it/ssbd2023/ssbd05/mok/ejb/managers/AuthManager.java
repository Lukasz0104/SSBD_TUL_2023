package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Token;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.unauthorized.AuthenticationException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.AuthManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.JwtRefreshTokenDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.TokenFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.AppProperties;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.EmailService;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.HashGenerator;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.JwtUtils;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.TokenFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericManagerExceptionsInterceptor.class,
    AuthManagerExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class AuthManager extends AbstractManager implements AuthManagerLocal, SessionSynchronization {

    @Inject
    private AccountFacade accountFacade;

    @Inject
    private TokenFacade tokenFacade;

    @Inject
    private EmailService emailService;

    @Inject
    private JwtUtils jwtUtils;

    @Inject
    private AppProperties appProperties;

    @Inject
    private HashGenerator hashGenerator;


    @Inject
    private TokenFactory tokenFactory;

    @Override
    @PermitAll
    public JwtRefreshTokenDto registerSuccessfulLogin(String login, String ip, boolean confirmed)
        throws AppBaseException {
        Account account = accountFacade.findByLogin(login)
            .orElseThrow(AuthenticationException::new);

        if (account.isTwoFactorAuth() && !confirmed) {
            this.sendCodeToUser(login);
            return null;
        }

        account.registerSuccessfulLogin(ip);

        Token refreshToken = tokenFactory.createRefreshToken(account);

        tokenFacade.create(refreshToken);
        accountFacade.edit(account);

        account.getAccessLevels()
            .stream()
            .filter(accessLevel -> accessLevel.getLevel() == AccessType.ADMIN)
            .findFirst()
            .ifPresent((al) -> emailService.notifyAboutAdminLogin(
                account.getEmail(), account.getEmail(), account.getLanguage().toString(),
                account.getActivityTracker().getLastSuccessfulLoginIp(),
                account.getActivityTracker().getLastSuccessfulLogin()
            ));

        String jwt = jwtUtils.generateJWT(account);
        return new JwtRefreshTokenDto(
            jwt, refreshToken.getToken(), account.getLanguage().toString(), account.isLightThemePreferred());
    }


    @Override
    @PermitAll
    public void registerUnsuccessfulLogin(String login, String ip) throws AppBaseException {
        Account account = accountFacade.findByLogin(login)
            .orElseThrow(AuthenticationException::new);

        if (account.isAbleToAuthenticate()) {
            account.registerUnsuccessfulLogin(ip);
            if (account.getActivityTracker().getUnsuccessfulLoginChainCounter()
                >= appProperties.getUnsuccessfulLoginChainLimit()) {
                account.setActive(false);
                account.getActivityTracker().setUnsuccessfulLoginChainCounter(0);

                Token blockedAccountToken = tokenFactory.createBlockedAccountToken(account);
                tokenFacade.create(blockedAccountToken);

                emailService.notifyBlockedAccIncorrectLoginLimit(
                    account.getEmail(), account.getFullName(), account.getLanguage().toString());
            }
            accountFacade.edit(account);
        }
    }

    @Override
    @PermitAll
    public JwtRefreshTokenDto refreshJwt(String token, String login) throws AppBaseException {
        Token refreshToken = tokenFacade.findByToken(token)
            .orElseThrow(AuthenticationException::new);

        refreshToken.validateSelf(TokenType.REFRESH_TOKEN);

        Account account = refreshToken.getAccount();
        if (!Objects.equals(account.getLogin(), login) || !account.isAbleToAuthenticate()) {
            throw new AuthenticationException();
        }
        String jwt = jwtUtils.generateJWT(account);
        Token newRefreshToken = tokenFactory.createRefreshToken(account);

        tokenFacade.remove(refreshToken);
        tokenFacade.create(newRefreshToken);

        return new JwtRefreshTokenDto(
            jwt, newRefreshToken.getToken(), account.getLanguage().toString(), account.isLightThemePreferred());
    }

    @RolesAllowed({ADMIN, MANAGER, OWNER})
    public void logout(String token, String login) throws AppBaseException {
        Optional<Token> optionalToken =
            tokenFacade.findByTokenAndTokenType(token, TokenType.REFRESH_TOKEN);

        if (optionalToken.isPresent()) {
            Token refreshToken = optionalToken.get();
            Account account = refreshToken.getAccount();

            if (!Objects.equals(account.getLogin(), login)) {
                return;
            }
            tokenFacade.remove(refreshToken);
        }
    }

    @Override
    @PermitAll
    public void confirmLogin(String login, String code) throws AppBaseException {
        Account account = accountFacade.findByLogin(login).orElseThrow(AuthenticationException::new);
        if (!account.isAbleToAuthenticate()) {
            throw new AuthenticationException();
        }

        List<Token> twoFactorTokens = tokenFacade.findByTokenTypeAndAccountId(
            TokenType.TWO_FACTOR_AUTH_TOKEN, account.getId());
        Token twoFactorToken = null;
        for (Token tft : twoFactorTokens) {
            if (hashGenerator.verify(code.toCharArray(), tft.getToken())) {
                tft.validateSelf();
                twoFactorToken = tft;
                break;
            }
        }
        if (twoFactorToken == null) {
            throw new TokenNotFoundException();
        }
        tokenFacade.remove(twoFactorToken);
    }

    @PermitAll
    private void sendCodeToUser(String login) throws AppBaseException {
        Account account = accountFacade.findByLogin(login).orElseThrow(AuthenticationException::new);
        Random random = new Random();
        int code = 10000000 + random.nextInt(90000000);
        emailService.twoFactorAuthEmail(account.getEmail(), account.getFullName(), account.getLanguage().toString(),
            Integer.toString(code));
        tokenFacade.create(
            tokenFactory.createTwoFactorAuthToken(account,
                hashGenerator.generate(Integer.toString(code).toCharArray()))
        );
    }
}
