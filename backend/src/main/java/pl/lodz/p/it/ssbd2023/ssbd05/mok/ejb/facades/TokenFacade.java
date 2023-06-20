package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades;


import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
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

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
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
    @PermitAll
    public void create(Token entity) throws AppBaseException {
        super.create(entity);
    }

    @Override
    @PermitAll
    public void remove(Token entity) throws AppBaseException {
        super.remove(entity);
    }

    @PermitAll
    public Optional<Token> findByToken(String token) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByToken", Token.class);
        tq.setParameter("token", token);
        try {
            return Optional.of(tq.getSingleResult());
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    @PermitAll
    public Optional<Token> findByTokenAndTokenType(String token, TokenType tokenType) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByTokenAndTokenType", Token.class);
        tq.setParameter("token", token);
        tq.setParameter("tokenType", tokenType);
        try {
            return Optional.of(tq.getSingleResult());
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    @PermitAll
    public void removeTokensByAccountIdAndTokenType(Long accountId, TokenType tokenType) {
        TypedQuery tq = em.createNamedQuery("Token.removeTokensByAccountIdAndTokenType", Token.class);
        tq.setParameter("accountId", accountId);
        tq.setParameter("tokenType", tokenType);
        tq.executeUpdate();
        em.flush();
    }

    @PermitAll
    public List<Token> findByTokenTypeAndExpiresAtAfter(TokenType tokenType, LocalDateTime expiresAt) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByTokenTypeAndExpiresAtAfter", Token.class);
        tq.setParameter("tokenType", tokenType);
        tq.setParameter("expiresAt", expiresAt);
        return tq.getResultList();
    }

    @PermitAll
    public List<Token> findByTokenTypeAndExpiresAtBefore(TokenType tokenType, LocalDateTime expiresAt) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByTokenTypeAndExpiresAtBefore", Token.class);
        tq.setParameter("tokenType", tokenType);
        tq.setParameter("expiresAt", expiresAt);
        return tq.getResultList();
    }

    @PermitAll
    public List<Token> findByNotTokenTypeAndExpiresAtBefore(TokenType tokenType, LocalDateTime expiresAt) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByNotTokenTypeAndExpiresAtBefore", Token.class);
        tq.setParameter("tokenType", tokenType);
        tq.setParameter("expiresAt", expiresAt);
        return tq.getResultList();
    }

    @PermitAll
    public List<Token> findByTokenTypeAndAccountId(TokenType tokenType, Long accountId) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByTokenTypeAndAccountId", Token.class);
        tq.setParameter("tokenType", tokenType);
        tq.setParameter("accountId", accountId);
        return tq.getResultList();
    }
}
