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
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.time.LocalDateTime;
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
    @PermitAll
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
        tq = em.createNamedQuery("Account.findAllAccountsByVerified", Account.class);
        tq.setParameter("verified", verified);
        return tq.getResultList();
    }

    @RolesAllowed({"ADMIN", "MANAGER"})
    public Page<Account> findByActive(boolean active, int page, int pageSize, boolean asc, String phrase,
                                      String login) {
        TypedQuery<Account> tq;
        if (asc) {
            tq = em.createNamedQuery("Account.findAllAccountsByActiveAsc", Account.class);
        } else {
            tq = em.createNamedQuery("Account.findAllAccountsByActiveDesc", Account.class);
        }
        tq.setParameter("active", active);
        tq.setParameter("phrase", phrase);
        tq.setParameter("login", login);

        tq.setFirstResult(page * pageSize);
        tq.setMaxResults(pageSize);

        Long count = countByActive(active, phrase, login);

        return new Page<>(tq.getResultList(), count, pageSize, page);
    }

    @RolesAllowed({"ADMIN", "MANAGER"})
    public Long countByActive(boolean active, String phrase, String login) {
        TypedQuery<Long> tq;
        tq = em.createNamedQuery("Account.countAllAccountsByActive", Long.class);
        tq.setParameter("active", active);
        tq.setParameter("phrase", phrase);
        tq.setParameter("login", login);
        return tq.getSingleResult();
    }

    @RolesAllowed({"ADMIN", "MANAGER"})
    public Page<Account> findByActiveAccessLevel(AccessType accessType, boolean active, int page, int pageSize,
                                                 boolean asc, String phrase, String login) {
        TypedQuery<Account> tq;
        if (asc) {
            tq = em.createNamedQuery("Account.findAllAccountsByActiveAndAccessLevelAsc", Account.class);
        } else {
            tq = em.createNamedQuery("Account.findAllAccountsByActiveAndAccessLevelDesc", Account.class);
        }
        tq.setParameter("active", active);
        tq.setParameter("phrase", phrase);
        tq.setParameter("level", accessType);
        tq.setParameter("login", login);

        tq.setFirstResult(page * pageSize);
        tq.setMaxResults(pageSize);

        Long count = countByActiveAccessLevel(active, accessType, phrase, login);

        return new Page<>(tq.getResultList(), count, pageSize, page);
    }

    @RolesAllowed({"ADMIN", "MANAGER"})
    public Long countByActiveAccessLevel(boolean active, AccessType accessType, String phrase, String login) {
        TypedQuery<Long> tq;
        tq = em.createNamedQuery("Account.countAllAccountsByActiveAndAccessLevel", Long.class);
        tq.setParameter("active", active);
        tq.setParameter("phrase", phrase);
        tq.setParameter("level", accessType);
        tq.setParameter("login", login);
        return tq.getSingleResult();
    }

    @RolesAllowed({"ADMIN", "MANAGER"})
    public Page<Account> findAccountsThatNeedApprovalByAccessLevel(AccessType accessType, int page, int pageSize,
                                                                   boolean asc, String phrase, String login) {
        TypedQuery<Account> tq;
        if (asc) {
            tq = em.createNamedQuery("Account.findAccountsThatNeedApprovalByAccessLevelAsc", Account.class);
        } else {
            tq = em.createNamedQuery("Account.findAccountsThatNeedApprovalByAccessLevelDesc", Account.class);
        }
        tq.setParameter("level", accessType);
        tq.setParameter("phrase", phrase);
        tq.setParameter("login", login);

        tq.setFirstResult(page * pageSize);
        tq.setMaxResults(pageSize);

        Long count = countAccountsThatNeedApprovalByAccessLevel(accessType, phrase, login);

        return new Page<>(tq.getResultList(), count, pageSize, page);
    }

    @RolesAllowed({"ADMIN", "MANAGER"})
    public Long countAccountsThatNeedApprovalByAccessLevel(AccessType accessType, String phrase, String login) {
        TypedQuery<Long> tq =
            em.createNamedQuery("Account.countAccountsThatNeedApprovalByAccessLevel", Long.class);
        tq.setParameter("level", accessType);
        tq.setParameter("phrase", phrase);
        tq.setParameter("login", login);
        return tq.getSingleResult();
    }

    @RolesAllowed({"ADMIN", "MANAGER"})
    public List<String> findAccountsLoginsByLoginLike(String login) {
        TypedQuery<String> tq =
            em.createNamedQuery("Account.findAccountsLoginsByLoginLike", String.class);
        tq.setParameter("login", login);
        tq.setMaxResults(10);
        return tq.getResultList();
    }

    @PermitAll
    public List<Account> findAccountsWithoutRecentActivity(LocalDateTime lastSuccessfulLoginBefore) {
        return em.createNamedQuery("Account.findAccountsWithoutRecentActivity", Account.class)
            .setParameter("lastSuccessfulLogin", lastSuccessfulLoginBefore)
            .getResultList();
    }
}
