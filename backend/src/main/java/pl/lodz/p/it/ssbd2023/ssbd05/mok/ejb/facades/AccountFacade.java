package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.AccountFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.util.List;
import java.util.Optional;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    AccountFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
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

    @Override
    @PermitAll
    public void create(Account entity) throws AppBaseException {
        super.create(entity);
    }

    @Override
    @PermitAll
    public void edit(Account entity) throws AppBaseException {
        super.edit(entity);
    }

    public void lockAndEdit(Account account) throws AppBaseException {
        em.lock(account, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        em.merge(account);
        em.flush();
    }

    @Override
    public void remove(Account entity) throws AppBaseException {
        super.remove(entity);
    }

    @Override
    @RolesAllowed({"MANAGER", "ADMIN"})
    public Optional<Account> find(Long id) {
        return super.find(id);
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

    @PermitAll
    public Optional<Account> findByEmail(String email) {
        try {
            TypedQuery<Account> tq = em.createNamedQuery("Account.findByEmail", Account.class);
            tq.setParameter("email", email);
            return Optional.of(tq.getSingleResult());
        } catch (PersistenceException e) {
            return Optional.empty();
        }
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

    @RolesAllowed({"ADMIN", "MANAGER"})
    public List<Account> findByActive(boolean active) {
        TypedQuery<Account> tq;
        if (active) {
            tq = em.createNamedQuery("Account.findAllActiveAccounts", Account.class);
        } else {
            tq = em.createNamedQuery("Account.findAllNotActiveAccounts", Account.class);
        }
        return tq.getResultList();
    }

    @RolesAllowed({"ADMIN", "MANAGER"})
    public List<Account> findByActiveAccessLevel(AccessType accessType, boolean active) {
        TypedQuery<Account> tq;
        if (active) {
            tq = em.createNamedQuery("Account.findAllActiveAccountsByAccessLevel", Account.class);
        } else {
            tq = em.createNamedQuery("Account.findAllInactiveAccountsByAccessLevel", Account.class);
        }
        tq.setParameter("level", accessType);
        return tq.getResultList();
    }

    @RolesAllowed({"ADMIN", "MANAGER"})
    public List<Account> findByActiveAndVerifiedAccessLevel(AccessType accessType) {
        TypedQuery<Account> tq =
            em.createNamedQuery("Account.findAccountsThatNeedApprovalByAccessLevel", Account.class);
        tq.setParameter("level", accessType);
        return tq.getResultList();
    }
}
