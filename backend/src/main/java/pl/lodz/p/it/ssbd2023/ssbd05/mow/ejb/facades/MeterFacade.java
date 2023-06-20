package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Meter;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
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
public class MeterFacade extends AbstractFacade<Meter> {

    @PersistenceContext(unitName = "ssbd05mowPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MeterFacade() {
        super(Meter.class);
    }

    @RolesAllowed({MANAGER})
    public Optional<Meter> find(Long id) {
        return super.find(id);
    }

    @Override
    @RolesAllowed(MANAGER)
    public void create(Meter entity) throws AppBaseException {
        super.create(entity);
    }

    @Override
    @RolesAllowed({MANAGER, OWNER})
    public void edit(Meter entity) throws AppBaseException {
        super.edit(entity);
    }

    @RolesAllowed({OWNER})
    public boolean existsByIdAndOwnerLogin(Long id, String login) {
        TypedQuery<Meter> tq = em.createNamedQuery("Meter.findByIdAndOwnerLogin", Meter.class);
        tq.setParameter("id", id);
        tq.setParameter("login", login);

        try {
            tq.getSingleResult();
            return true;
        } catch (NoResultException nre) {
            return false;
        }
    }

    @RolesAllowed({MANAGER})
    public boolean existsById(Long id) {
        return find(id).isPresent();
    }

    @RolesAllowed({OWNER})
    public Optional<Meter> findByIdAndOwnerLogin(Long id, String login) {
        TypedQuery<Meter> tq = em.createNamedQuery("Meter.findByIdAndOwnerLogin", Meter.class);
        tq.setParameter("id", id);
        tq.setParameter("login", login);

        try {
            return Optional.of(tq.getSingleResult());
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    @PermitAll
    public Optional<Meter> findByCategoryIdAndPlaceId(Long categoryId, Long placeId) throws AppDatabaseException {
        try {
            TypedQuery<Meter> tq = em.createNamedQuery("Meter.findByCategoryIdAndPlaceId", Meter.class);
            tq.setParameter("categoryId", categoryId);
            tq.setParameter("placeId", placeId);
            return Optional.of(tq.getSingleResult());
        } catch (PersistenceException e) {
            return Optional.empty();
        }
    }
}
