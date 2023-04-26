package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Token;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.ExpiredTokenException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.InvalidTokenTypeException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.ConstraintViolationException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.TokenFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.EmailService;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.HashGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @Context
    private SecurityContext securityContext;

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
    public void changeEmail()
            throws AppBaseException {
        String login = securityContext.getUserPrincipal().getName();
        System.out.println(login);

        Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

        List<Token> tokenList = tokenFacade.findByAccountLoginAndTokenType(login, TokenType.CONFIRM_EMAIL_TOKEN);

        for (Token token : tokenList) {
            tokenFacade.remove(token);
        }

        Token token = new Token(account, TokenType.CONFIRM_EMAIL_TOKEN);
        tokenFacade.create(token);

        emailService.sendMessage(); //TODO token UUID in message
    }

    @Override
    public void confirmEmail(String email, UUID confirmToken)
            throws AppBaseException {
        Token token = tokenFacade.findByToken(confirmToken).orElseThrow(TokenNotFoundException::new);

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException();
        }

        if (token.getTokenType() != TokenType.CONFIRM_EMAIL_TOKEN) {
            throw new InvalidTokenTypeException();
        }

        Account account = token.getAccount();

        tokenFacade.remove(token);

        account.setEmail(email);

        try {
            accountFacade.edit(account);
        } catch (DatabaseException de) {
            throw new ConstraintViolationException(de.getMessage(), de);
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
