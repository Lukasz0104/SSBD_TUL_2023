package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Token;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
public class TokenFacade extends AbstractFacade<Token> {

    @PersistenceContext(unitName = "ssbd05mokPU")
    private EntityManager em;

    public TokenFacade() {
        super(Token.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void create(Token entity) throws AppBaseException {
        super.create(entity);
    }

    @Override
    public void remove(Token entity) throws AppBaseException {
        super.remove(entity);
    }

    public Optional<Token> findByToken(UUID token) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByToken", Token.class);
        tq.setParameter("token", token);
        try {
            return Optional.of(tq.getSingleResult());
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    public List<Token> findTokenByAccountId(Long accountId) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findTokenByAccountId", Token.class);
        tq.setParameter("accountId", accountId);
        return tq.getResultList();
    }

    public List<Token> findByTokenType(TokenType tokenType) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByTokenType", Token.class);
        tq.setParameter("tokenType", tokenType);
        return tq.getResultList();
    }

    public List<Token> findByExpiresAt(LocalDateTime expiresAt) {
        return this.findByExpiresAtIf(expiresAt, null);
    }

    public List<Token> findByExpiresAtAfter(LocalDateTime expiresAt) {
        return this.findByExpiresAtIf(expiresAt, false);
    }

    public List<Token> findByExpiresAtBefore(LocalDateTime expiresAt) {
        return this.findByExpiresAtIf(expiresAt, true);
    }

    public List<Token> findByAccountIdAndTokenType(Long accountId, TokenType tokenType) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByAccountIdAndTokenType", Token.class);
        tq.setParameter("accountId", accountId);
        tq.setParameter("tokenType", tokenType);
        return tq.getResultList();
    }

    public List<Token> findByTokenTypeAndExpiresAtAfter(TokenType tokenType, LocalDateTime expiresAt) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByTokenTypeAndExpiresAtAfter", Token.class);
        tq.setParameter("tokenType", tokenType);
        tq.setParameter("expiresAt", expiresAt);
        return tq.getResultList();
    }

    public List<Token> findByTokenTypeAndExpiresAtBefore(TokenType tokenType, LocalDateTime expiresAt) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByTokenTypeAndExpiresAtBefore", Token.class);
        tq.setParameter("tokenType", tokenType);
        tq.setParameter("expiresAt", expiresAt);
        return tq.getResultList();
    }

    public List<Token> findByNotTokenTypeAndExpiresAtBefore(TokenType tokenType, LocalDateTime expiresAt) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByNotTokenTypeAndExpiresAtBefore", Token.class);
        tq.setParameter("tokenType", tokenType);
        tq.setParameter("expiresAt", expiresAt);
        return tq.getResultList();
    }

    public List<Token> findByAccountIdAndExpiresAtAfter(Long accountId, LocalDateTime expiresAt) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByAccountIdAndExpiresAtAfter", Token.class);
        tq.setParameter("accountId", accountId);
        tq.setParameter("expiresAt", expiresAt);
        return tq.getResultList();
    }

    public List<Token> findByAccountIdAndExpiresAtBefore(Long accountId, LocalDateTime expiresAt) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByAccountIdAndExpiresAtBefore", Token.class);
        tq.setParameter("accountId", accountId);
        tq.setParameter("expiresAt", expiresAt);
        return tq.getResultList();
    }

    public List<Token> findByAccountLoginAndTokenType(String login, TokenType tokenType) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByAccountLoginAndTokenType", Token.class);
        tq.setParameter("login", login);
        tq.setParameter("tokenType", tokenType);
        return tq.getResultList();
    }

    private List<Token> findByExpiresAtIf(LocalDateTime expiresAt, Boolean isBefore) {
        TypedQuery<Token> tq;
        if (isBefore == null) {
            tq = em.createNamedQuery("Token.findByExpiresAt", Token.class);
        } else if (!isBefore) {
            tq = em.createNamedQuery("Token.findByExpiresAtAfter", Token.class);
        } else {
            tq = em.createNamedQuery("Token.findByExpiresAtBefore", Token.class);
        }
        tq.setParameter("expiresAt", expiresAt);
        return tq.getResultList();
    }
}
