package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Token;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.LoginResponseDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.TokenFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.EmailService;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.JwtUtils;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.Properties;

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


    public LoginResponseDto registerSuccessfulLogin(String login, String ip) throws AppBaseException {
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

        return new LoginResponseDto(jwt, refreshToken.getToken());
    }

    public void registerUnsuccessfulLogin(String login, String ip) throws AppBaseException {
        Account account = accountFacade.findByLogin(login)
            .orElseThrow(AccountNotFoundException::new);

        boolean hasActiveAccessLevels = account.getAccessLevels().stream()
            .anyMatch(AccessLevel::isActive);

        if (account.isActive() && account.isVerified() && hasActiveAccessLevels) {
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
}
