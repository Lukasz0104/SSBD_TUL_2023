package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
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
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.util.List;
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


    @RolesAllowed({OWNER, MANAGER})
    public List<Meter> findByCategoryId(Long categoryId) throws AppDatabaseException {
        try {
            TypedQuery<Meter> tq = em.createNamedQuery("Meter.findByCategoryId", Meter.class);
            tq.setParameter("categoryId", categoryId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Meter.findByCategoryId , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Meter> findByCategoryName(String categoryName) throws AppDatabaseException {
        try {
            TypedQuery<Meter> tq = em.createNamedQuery("Meter.findByCategoryName", Meter.class);
            tq.setParameter("categoryName", categoryName);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Meter.findByCategoryName , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Meter> findByPlaceNumberAndBuildingId(Integer placeNumber, Long buildingId) throws
        AppDatabaseException {
        try {
            TypedQuery<Meter> tq = em.createNamedQuery("Meter.findByPlaceNumberAndBuildingId", Meter.class);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Meter.findByPlaceNumberAndBuildingId , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public Meter findByCategoryIdAndPlaceId(Long categoryId, Long placeId) throws AppDatabaseException {
        try {
            TypedQuery<Meter> tq = em.createNamedQuery("Meter.findByCategoryIdAndPlaceId", Meter.class);
            tq.setParameter("categoryId", categoryId);
            tq.setParameter("placeId", placeId);
            return tq.getSingleResult();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Meter.findByCategoryIdAndPlaceId , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public Meter findByCategoryIdAndPlaceNumberAndBuildingId(Long categoryId, Integer placeNumber, Long buildingId)
        throws AppDatabaseException {
        try {
            TypedQuery<Meter> tq =
                em.createNamedQuery("Meter.findByCategoryIdAndPlaceNumberAndBuildingId", Meter.class);
            tq.setParameter("categoryId", categoryId);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            return tq.getSingleResult();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Meter.findByCategoryIdAndPlaceNumberAndBuildingId , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public Meter findByCategoryNameAndPlaceId(String categoryName, Long placeId) throws AppDatabaseException {
        try {
            TypedQuery<Meter> tq = em.createNamedQuery("Meter.findByCategoryNameAndPlaceId", Meter.class);
            tq.setParameter("categoryName", categoryName);
            tq.setParameter("placeId", placeId);
            return tq.getSingleResult();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Meter.findByCategoryNameAndPlaceId , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public Meter findByCategoryNameAndPlaceNumberAndBuildingId(String categoryName, Integer placeNumber,
                                                               Long buildingId) throws AppDatabaseException {
        try {
            TypedQuery<Meter> tq =
                em.createNamedQuery("Meter.findByCategoryNameAndPlaceNumberAndBuildingId", Meter.class);
            tq.setParameter("categoryName", categoryName);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            return tq.getSingleResult();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Meter.findByCategoryNameAndPlaceNumberAndBuildingId , Database Exception",
                e);
        }
    }
}
