package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Reading;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
@DenyAll
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ReadingFacade extends AbstractFacade<Reading> {

    @PersistenceContext(unitName = "ssbd05mowPU")
    private EntityManager em;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReadingFacade() {
        super(Reading.class);
    }

    @RolesAllowed({OWNER, MANAGER})
    public void create(Reading reading) throws AppBaseException {
        super.create(reading);
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Reading> findByValue(BigDecimal value) throws AppDatabaseException {
        try {
            TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByValue", Reading.class);
            tq.setParameter("value", value);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Reading.findByValue , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Reading> findByDate(LocalDateTime date) throws AppDatabaseException {
        try {
            TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByDate", Reading.class);
            tq.setParameter("date", date);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Reading.findByDate , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Reading> findByDateAfter(LocalDateTime date) throws AppDatabaseException {
        try {
            TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByDateAfter", Reading.class);
            tq.setParameter("date", date);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Reading.findByDateAfter , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Reading> findByDateBefore(LocalDateTime date) throws AppDatabaseException {
        try {
            TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByDateBefore", Reading.class);
            tq.setParameter("date", date);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Reading.findByDateBefore , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Reading> findByDateBetween(LocalDateTime beginDate, LocalDateTime endDate) throws AppDatabaseException {
        try {
            TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByDateBetween", Reading.class);
            tq.setParameter("beginDate", beginDate);
            tq.setParameter("endDate", endDate);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Reading.findByDateBetween , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public Page<Reading> findByMeterId(Long meterId, int page, int pageSize) {
        TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByMeterId", Reading.class);
        tq.setParameter("meterId", meterId);

        tq.setFirstResult(page * pageSize);
        tq.setMaxResults(pageSize);

        Long count = countByMeterId(meterId);

        return new Page<>(tq.getResultList(), count, pageSize, page);
    }

    @RolesAllowed({OWNER, MANAGER})
    public Long countByMeterId(Long meterId) {
        TypedQuery<Long> tq = em.createNamedQuery("Reading.countByMeterId", Long.class);
        tq.setParameter("meterId", meterId);

        return tq.getSingleResult();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Reading> findByMeterIdAndDate(Long meterId, LocalDateTime date) throws AppDatabaseException {
        try {
            TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByMeterIdAndDate", Reading.class);
            tq.setParameter("meterId", meterId);
            tq.setParameter("date", date);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Reading.findByMeterIdAndDate , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Reading> findByMeterIdAndDateAfter(Long meterId, LocalDateTime date) throws AppDatabaseException {
        try {
            TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByMeterIdAndDateAfter", Reading.class);
            tq.setParameter("meterId", meterId);
            tq.setParameter("date", date);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Reading.findByMeterIdAndDateAfter , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Reading> findByMeterIdAndDateBefore(Long meterId, LocalDateTime date) throws AppDatabaseException {
        try {
            TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByMeterIdAndDateBefore", Reading.class);
            tq.setParameter("meterId", meterId);
            tq.setParameter("date", date);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Reading.findByMeterIdAndDateBefore , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Reading> findByMeterIdAndDateBetween(Long meterId, LocalDateTime beginDate, LocalDateTime endDate)
        throws AppDatabaseException {
        try {
            TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByMeterIdAndDateBetween", Reading.class);
            tq.setParameter("meterId", meterId);
            tq.setParameter("beginDate", beginDate);
            tq.setParameter("endDate", endDate);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Reading.findByMeterIdAndDateBetween , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Reading> findByPlaceId() throws AppDatabaseException {
        try {
            TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByPlaceId", Reading.class);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Reading.findByPlaceId , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Reading> findByPlaceIdAndDate(LocalDateTime date) throws AppDatabaseException {
        try {
            TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByPlaceIdAndDate", Reading.class);
            tq.setParameter("date", date);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Reading.findByPlaceIdAndDate , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Reading> findByPlaceIdAndDateBetween(LocalDateTime beginDate, LocalDateTime endDate)
        throws AppDatabaseException {
        try {
            TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByPlaceIdAndDateBetween", Reading.class);
            tq.setParameter("beginDate", beginDate);
            tq.setParameter("endDate", endDate);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Reading.findByPlaceIdAndDateBetween , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Reading> findByPlaceIdAndDateAfter(LocalDateTime date) throws AppDatabaseException {
        try {
            TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByPlaceIdAndDateAfter", Reading.class);
            tq.setParameter("date", date);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Reading.findByPlaceIdAndDateAfter , Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Reading> findByPlaceIdAndDateBefore(LocalDateTime date) throws AppDatabaseException {
        try {
            TypedQuery<Reading> tq = em.createNamedQuery("Reading.findByPlaceIdAndDateBefore", Reading.class);
            tq.setParameter("date", date);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Reading.findByPlaceIdAndDateBefore , Database Exception", e);
        }
    }
}
