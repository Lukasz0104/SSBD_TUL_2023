package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.AccountFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    AccountFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class AccessLevelFacade extends AbstractFacade<AccessLevel> {

    @PersistenceContext(unitName = "ssbd05mokPU")
    private EntityManager em;

    public AccessLevelFacade() {
        super(AccessLevel.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed({ADMIN, MANAGER})
    public void create(AccessLevel entity) throws AppBaseException {
        super.create(entity);
    }

    @Override
    @RolesAllowed({ADMIN, MANAGER})
    public void edit(AccessLevel entity) throws AppBaseException {
        super.edit(entity);
    }
}
