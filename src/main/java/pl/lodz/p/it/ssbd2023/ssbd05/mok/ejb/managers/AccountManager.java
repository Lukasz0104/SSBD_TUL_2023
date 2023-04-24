package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Token;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBadRequestException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.ExpiredTokenException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.InvalidTokenTypeException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.PasswordConstraintViolationException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.RepeatedPasswordException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.ConstraintViolationException;
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
    public void changePassword(String oldPass, String newPass, String newPassRep) throws AppBaseException {
        if (!newPass.equals(newPassRep)) {
            throw new AppBadRequestException();
        } else if (oldPass.equals(newPass)) {
            throw new RepeatedPasswordException();
        }

        Account account = accountFacade.findByLogin("pduda");
        String hashedPwdOld = hashGenerator.generate(oldPass.toCharArray());

        // check if old password is correct
        if (!account.getPassword().equals(hashedPwdOld)) {
            throw new InvalidPasswordException();
        }

        // check if old and new passwords are same
        String hashedPwdNew = hashGenerator.generate(newPass.toCharArray());
        if (account.getPassword().equals(hashedPwdNew)) {
            throw new RepeatedPasswordException();
        }

        try {
            account.setPassword(newPass);
            accountFacade.edit(account);
        } catch (DatabaseException e) {
            throw new PasswordConstraintViolationException();
        }
    }


}
