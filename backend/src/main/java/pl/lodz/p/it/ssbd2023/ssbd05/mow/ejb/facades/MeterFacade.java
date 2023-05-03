package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Meter;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
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

    public List<Meter> findByCategoryId(Long categoryId) throws AppDatabaseException {
        try {
            TypedQuery<Meter> tq = em.createNamedQuery("Meter.findByCategoryId", Meter.class);
            tq.setParameter("categoryId", categoryId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Meter.findByCategoryId , Database Exception", e);
        }
    }

    public List<Meter> findByCategoryName(String categoryName) throws AppDatabaseException {
        try {
            TypedQuery<Meter> tq = em.createNamedQuery("Meter.findByCategoryName", Meter.class);
            tq.setParameter("categoryName", categoryName);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Meter.findByCategoryName , Database Exception", e);
        }
    }

    public List<Meter> findByPlaceId(Long placeId) throws AppDatabaseException {
        try {
            TypedQuery<Meter> tq = em.createNamedQuery("Meter.findByPlaceId", Meter.class);
            tq.setParameter("placeId", placeId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Meter.findByPlaceId , Database Exception", e);
        }
    }

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
