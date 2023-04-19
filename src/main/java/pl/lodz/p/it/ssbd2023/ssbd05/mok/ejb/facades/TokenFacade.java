package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Token;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
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

    public Token findByToken(UUID token) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findById", Token.class);
        tq.setParameter("token", token);
        return tq.getSingleResult();
    }

    public List<Token> findTokenByAccountId(Long accountId) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findTokenByAccountId", Token.class);
        tq.setParameter("accountId", accountId);
        return tq.getResultList();
    }

    public List<Token> findByTokenType(TokenType tokenTypes) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByTokenType", Token.class);
        tq.setParameter("tokenType", tokenTypes);
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

    public Token findByAccountIdAndTokenType(Long accountId, TokenType tokenType) {
        TypedQuery<Token> tq = em.createNamedQuery("Token.findByAccountIdAndTokenType", Token.class);
        tq.setParameter("accountId", accountId);
        tq.setParameter("tokenType", tokenType);
        return tq.getSingleResult();
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
