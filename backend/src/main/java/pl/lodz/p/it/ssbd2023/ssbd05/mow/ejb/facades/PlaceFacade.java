package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
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
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.PlaceFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Stateless
@DenyAll
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    PlaceFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class
})
public class PlaceFacade extends AbstractFacade<Place> {

    @PersistenceContext(unitName = "ssbd05mowPU")
    private EntityManager em;

    public PlaceFacade() {
        super(Place.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @PermitAll
    public void edit(Place entity) throws AppBaseException {
        super.edit(entity);
    }

    @Override
    @PermitAll
    public Optional<Place> find(Long id) {
        return super.find(id);
    }

    @Override
    @PermitAll
    public List<Place> findAll() {
        return super.findAll();
    }

    @RolesAllowed({OWNER})
    public Optional<Place> findByIdAndOwnerLogin(Long id, String login) {
        TypedQuery<Place> tq = em.createNamedQuery("Place.findByIdAndOwnerLogin", Place.class);
        tq.setParameter("id", id);
        tq.setParameter("login", login);
        try {
            return Optional.of(tq.getSingleResult());
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    @RolesAllowed(OWNER)
    public List<Place> findByLogin(String login) {
        TypedQuery<Place> tq = em.createNamedQuery("Place.findByOwnerLogin", Place.class);
        tq.setParameter("login", login);
        return tq.getResultList();
    }

    @RolesAllowed(OWNER)
    public List<Place> findActiveByLogin(String login) {
        TypedQuery<Place> tq = em.createNamedQuery("Place.findActiveByOwnerLogin", Place.class);
        tq.setParameter("login", login);
        return tq.getResultList();
    }

    @PermitAll
    public List<Place> findByActive(boolean active) {
        TypedQuery<Place> tq;
        if (active) {
            tq = em.createNamedQuery("Place.findAllActive", Place.class);
        } else {
            tq = em.createNamedQuery("Place.findAllInactive", Place.class);
        }
        return tq.getResultList();
    }

    @RolesAllowed({MANAGER})
    public List<Rate> findCurrentRateByPlaceId(Long id) {
        TypedQuery<Rate> tq = em.createNamedQuery("Place.findCurrentRateByPlaceId", Rate.class);
        tq.setParameter("placeId", id);
        return tq.getResultList();
    }

    @RolesAllowed(MANAGER)
    public List<Rate> findCurrentRateByPlaceIdNotMatch(Long id) {
        TypedQuery<Rate> tq = em.createNamedQuery("Place.findCurrentRateByPlaceIdNotMatch", Rate.class);
        tq.setParameter("placeId", id);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER})
    public List<Rate> findCurrentRateByOwnPlaceId(Long id, String login) {
        TypedQuery<Rate> tq = em.createNamedQuery("Place.findCurrentRateByOwnPlaceId", Rate.class);
        tq.setParameter("placeId", id);
        tq.setParameter("login", login);
        tq.setParameter("now", LocalDate.now());
        return tq.getResultList();
    }

    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public List<Place> findByBuildingId(Long id) {
        return em.createNamedQuery("Place.findByBuildingId", Place.class)
            .setParameter("buildingId", id)
            .getResultList();
    }

    @RolesAllowed(MANAGER)
    public List<OwnerData> findByNotInLogins(Long id) {
        TypedQuery<OwnerData> tq = em.createNamedQuery("Place.findOwnerDataByNotOwnersOfPlaceId", OwnerData.class);
        tq.setParameter("placeId", id);
        return tq.getResultList();
    }

    @RolesAllowed(MANAGER)
    @Override
    public void create(Place entity) throws AppBaseException {
        super.create(entity);
    }
}
