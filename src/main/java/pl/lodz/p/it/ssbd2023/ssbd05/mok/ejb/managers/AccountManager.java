package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Token;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.ConstraintViolationException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.TokenFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.EmailService;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.HashGenerator;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AccountManager extends AbstractManager implements AccountManagerLocal, SessionSynchronization {

    @Inject
    private AccountFacade accountFacade;

    @Inject
    private TokenFacade tokenFacade;

    @Inject
    private HashGenerator hashGenerator;

    @Inject
    private EmailService emailService;

    @Override
    public void registerAccount(Account account) throws AppBaseException {
        String hashedPwd = hashGenerator.generate(account.getPassword().toCharArray());
        account.setPassword(hashedPwd);

        try {
            accountFacade.create(account);
        } catch (DatabaseException exc) {
            throw new ConstraintViolationException(exc.getMessage(), exc);
        }

        Token token = new Token(account, TokenType.CONFIRM_REGISTRATION_TOKEN);

        tokenFacade.create(token);

        emailService.sendMessage();
    }
}
