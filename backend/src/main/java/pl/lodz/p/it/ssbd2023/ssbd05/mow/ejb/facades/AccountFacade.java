package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.util.Optional;

@Stateless
@DenyAll
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class
})
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

    @RolesAllowed(MANAGER)
    public Optional<Account> findByOwnerId(Long ownerId) {
        try {
            TypedQuery<Account> tq = em.createNamedQuery("Account.findByOwnerId", Account.class);
            tq.setParameter("ownerId", ownerId);
            return Optional.of(tq.getSingleResult());
        } catch (PersistenceException e) {
            return Optional.empty();
        }
    }
}
