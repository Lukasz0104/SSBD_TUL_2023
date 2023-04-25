package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Token;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.ExpiredTokenException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.InvalidTokenTypeException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.PasswordConstraintViolationException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.ConstraintViolationException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.unauthorized.InvalidPasswordException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.TokenFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.EmailService;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.HashGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AccountManager extends AbstractManager implements AccountManagerLocal {

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

    @Override
    public void confirmRegistration(UUID confirmToken)
        throws AppBaseException {
        Token token = tokenFacade.findByToken(confirmToken).orElseThrow(TokenNotFoundException::new);

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException();
        }

        if (token.getTokenType() != TokenType.CONFIRM_REGISTRATION_TOKEN) {
            throw new InvalidTokenTypeException();
        }

        Account account = token.getAccount();
        account.setVerified(true);

        accountFacade.edit(account); // TODO Catch and handle DatabaseException
        tokenFacade.remove(token);
    }

    @Override
    public void changePassword(String oldPass, String newPass, String login) throws AppBaseException {

        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

        // check if old password is correct
        if (!hashGenerator.verify(oldPass.toCharArray(), account.getPassword())) {
            throw new InvalidPasswordException();
        }

        try {
            account.setPassword(hashGenerator.generate(newPass.toCharArray()));
            accountFacade.edit(account);
        } catch (DatabaseException e) {
            throw new PasswordConstraintViolationException();
        }
    }



    @Override
    public Account getAccountDetails(Long id) throws AppBaseException {
        return accountFacade.find(id).orElseThrow(AccountNotFoundException::new);
    }

    @Override
    public Account getAccountDetails(String login) throws AppBaseException {
        return accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
    }
}
