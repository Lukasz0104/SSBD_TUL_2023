package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Token;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.unauthorized.AuthenticationException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.JwtRefreshTokenDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.TokenFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.EmailService;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.JwtUtils;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.Properties;

import java.util.Objects;
import java.util.UUID;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AuthManager extends AbstractManager implements AuthManagerLocal {

    @Inject
    private AccountFacade accountFacade;

    @Inject
    private TokenFacade tokenFacade;

    @Inject
    private EmailService emailService;

    @Inject
    private JwtUtils jwtUtils;

    @Inject
    private Properties properties;


    @Override
    public JwtRefreshTokenDto registerSuccessfulLogin(String login, String ip) throws AppBaseException {
        Account account = accountFacade.findByLogin(login)
            .orElseThrow(AccountNotFoundException::new);

        account.registerSuccessfulLogin(ip);

        account.getAccessLevels()
            .stream()
            .filter(accessLevel -> accessLevel.getLevel() == AccessType.ADMIN)
            .findFirst()
            .ifPresent((al) -> emailService.notifyAboutAdminLogin(
                account.getEmail(), account.getEmail(), account.getLanguage(),
                account.getActivityTracker().getLastSuccessfulLoginIp(),
                account.getActivityTracker().getLastSuccessfulLogin()
            ));

        String jwt = jwtUtils.generateJWT(account);
        Token refreshToken = new Token(UUID.randomUUID(), account, TokenType.REFRESH_TOKEN);

        tokenFacade.create(refreshToken);
        accountFacade.edit(account);

        return new JwtRefreshTokenDto(jwt, refreshToken.getToken());
    }

    @Override
    public void registerUnsuccessfulLogin(String login, String ip) throws AppBaseException {
        Account account = accountFacade.findByLogin(login)
            .orElseThrow(AccountNotFoundException::new);

        if (account.isAbleToAuthenticate()) {
            account.registerUnsuccessfulLogin(ip);
            if (account.getActivityTracker().getUnsuccessfulLoginChainCounter()
                > properties.getUnsuccessfulLoginChainLimit()) {
                account.setActive(false);
                account.getActivityTracker().setUnsuccessfulLoginChainCounter(0);
                accountFacade.edit(account);
                emailService.notifyBlockedAccIncorrectLoginLimit(
                    account.getEmail(), account.getEmail(), account.getLanguage());
            }
        }
    }

    @Override
    public JwtRefreshTokenDto refreshJwt(UUID token, String login) throws AppBaseException {
        Token refreshToken = tokenFacade.findByToken(token)
            .orElseThrow(AuthenticationException::new);

        refreshToken.validateSelf(TokenType.REFRESH_TOKEN);

        Account account = refreshToken.getAccount();
        if (!Objects.equals(account.getLogin(), login) || !account.isAbleToAuthenticate()) {
            throw new AuthenticationException();
        }
        String jwt = jwtUtils.generateJWT(account);
        Token newRefreshToken = new Token(UUID.randomUUID(), account, TokenType.REFRESH_TOKEN);

        tokenFacade.remove(refreshToken);
        tokenFacade.create(newRefreshToken);

        return new JwtRefreshTokenDto(jwt, newRefreshToken.getToken());
    }
}
