package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.util.List;
import java.util.Optional;

@Stateless
@DenyAll
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccountFacade extends AbstractFacade<Account> {

    @PersistenceContext(unitName = "ssbd05mowPU")
    private EntityManager em;

    public AccountFacade() {
        super(Account.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    @PermitAll
    public Optional<Account> findByLogin(String login) {
        try {
            TypedQuery<Account> tq = em.createNamedQuery("Account.findByLogin", Account.class);
            tq.setParameter("login", login);
            return Optional.of(tq.getSingleResult());
        } catch (PersistenceException pe) {
            return Optional.empty();
        }
    }

    @RolesAllowed({MANAGER, ADMIN})
    public Optional<Account> findByEmail(String email) {
        try {
            TypedQuery<Account> tq = em.createNamedQuery("Account.findByEmail", Account.class);
            tq.setParameter("email", email);
            return Optional.of(tq.getSingleResult());
        } catch (PersistenceException e) {
            return Optional.empty();
        }
    }

    @RolesAllowed({MANAGER, ADMIN})
    public List<Account> findByVerified(boolean verified) {
        TypedQuery<Account> tq;
        if (verified) {
            tq = em.createNamedQuery("Account.findAllVerifiedAccounts", Account.class);
        } else {
            tq = em.createNamedQuery("Account.findAllNotVerifiedAccounts", Account.class);
        }
        return tq.getResultList();
    }

    @RolesAllowed({MANAGER, ADMIN})
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
