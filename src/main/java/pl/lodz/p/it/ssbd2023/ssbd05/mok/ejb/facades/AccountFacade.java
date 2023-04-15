package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AccountFacade extends AbstractFacade<Account> {

    @PersistenceContext(unitName = "ssbd05mokPU")
    private EntityManager em;

    public AccountFacade() {
        super(Account.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Account findByLogin(String login) {
        TypedQuery<Account> tq = em.createNamedQuery("Account.findByLogin", Account.class);
        tq.setParameter("login", login);
        return tq.getSingleResult();
    }

    public Account findByEmail(String email) {
        TypedQuery<Account> tq = em.createNamedQuery("Account.findByEmail", Account.class);
        tq.setParameter("email", email);
        return tq.getSingleResult();
    }

    public List<Account> findByVerified(boolean verified) {
        TypedQuery<Account> tq;
        if (verified) {
            tq = em.createNamedQuery("Account.findAllVerifiedAccounts", Account.class);
        } else {
            tq = em.createNamedQuery("Account.findAllNotVerifiedAccounts", Account.class);
        }
        return tq.getResultList();
    }

    public List<Account> findByActive(boolean active) {
        TypedQuery<Account> tq;
        if (active) {
            tq = em.createNamedQuery("Account.findAllActiveAccounts", Account.class);
        } else {
            tq = em.createNamedQuery("Account.findAllNotActiveAccounts", Account.class);
        }
        return tq.getResultList();
    }
}
