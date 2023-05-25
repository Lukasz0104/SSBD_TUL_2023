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
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Cost;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.time.Month;
import java.time.Year;
import java.util.List;

@Stateless
@DenyAll
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class CostFacade extends AbstractFacade<Cost> {

    @PersistenceContext(unitName = "ssbd05mowPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CostFacade() {
        super(Cost.class);
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Cost> findByYear(Year year) throws AppDatabaseException {
        try {
            TypedQuery<Cost> tq = em.createNamedQuery("Cost.findByYear", Cost.class);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Cost.findByYear, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Cost> findByMonth(Month month) throws AppDatabaseException {
        try {
            TypedQuery<Cost> tq = em.createNamedQuery("Cost.findByMonth", Cost.class);
            tq.setParameter("month", month);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Cost.findByMonth, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Cost> findByCategoryId(Long categoryId) throws AppDatabaseException {
        try {
            TypedQuery<Cost> tq = em.createNamedQuery("Cost.findByCategoryId", Cost.class);
            tq.setParameter("categoryId", categoryId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Cost.findByCategoryId, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Cost> findByCategoryName(String categoryName) throws AppDatabaseException {
        try {
            TypedQuery<Cost> tq = em.createNamedQuery("Cost.findByCategoryName", Cost.class);
            tq.setParameter("categoryName", categoryName);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Cost.findByCategoryName, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Cost> findByYearAndMonth(Year year, Month month) throws AppDatabaseException {
        try {
            TypedQuery<Cost> tq = em.createNamedQuery("Cost.findByYearAndMonth", Cost.class);
            tq.setParameter("year", year);
            tq.setParameter("month", month);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Cost.findByYearAndMonth, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Cost> findByYearAndCategoryId(Year year, Long categoryId) throws AppDatabaseException {
        try {
            TypedQuery<Cost> tq = em.createNamedQuery("Cost.findByYearAndCategoryId", Cost.class);
            tq.setParameter("year", year);
            tq.setParameter("categoryId", categoryId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Cost.findByYearAndCategoryId, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Cost> findByYearAndCategoryName(Year year, String categoryName) throws AppDatabaseException {
        try {
            TypedQuery<Cost> tq = em.createNamedQuery("Cost.findByYearAndCategoryName", Cost.class);
            tq.setParameter("year", year);
            tq.setParameter("categoryName", categoryName);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Cost.findByYearAndCategoryName, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Cost> findByMonthAndCategoryId(Month month, Long categoryId) throws AppDatabaseException {
        try {
            TypedQuery<Cost> tq = em.createNamedQuery("Cost.findByMonthAndCategoryId", Cost.class);
            tq.setParameter("month", month);
            tq.setParameter("categoryId", categoryId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Cost.findByMonthAndCategoryId, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Cost> findByMonthAndCategoryName(Month month, String categoryName) throws AppDatabaseException {
        try {
            TypedQuery<Cost> tq = em.createNamedQuery("Cost.findByMonthAndCategoryName", Cost.class);
            tq.setParameter("month", month);
            tq.setParameter("categoryName", categoryName);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Cost.findByMonthAndCategoryName, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Cost> findByYearAndMonthAndCategoryId(Year year, Month month, Long categoryId)
        throws AppDatabaseException {
        try {
            TypedQuery<Cost> tq = em.createNamedQuery("Cost.findByYearAndMonthAndCategoryId", Cost.class);
            tq.setParameter("year", year);
            tq.setParameter("month", month);
            tq.setParameter("categoryId", categoryId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Cost.findByYearAndMonthAndCategoryId, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Cost> findByYearAndMonthAndCategoryName(Year year, Month month, String categoryName)
        throws AppDatabaseException {
        try {
            TypedQuery<Cost> tq = em.createNamedQuery("Cost.findByYearAndMonthAndCategoryName", Cost.class);
            tq.setParameter("year", year);
            tq.setParameter("month", month);
            tq.setParameter("categoryName", categoryName);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Cost.findByYearAndMonthAndCategoryName, Database Exception", e);
        }
    }
}
