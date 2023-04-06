package pl.lodz.p.it.ssbd2023.mow.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.entities.mow.Report;
import pl.lodz.p.it.ssbd2023.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2023.shared.AbstractFacade;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ReportFacade extends AbstractFacade<Report> {

    @PersistenceContext(unitName = "ssbd05mowPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReportFacade() {
        super(Report.class);
    }

    public List<Report> findByYear(Year year) throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByYear", Report.class);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByYear, Database Exception", e);
        }
    }

    public List<Report> findByTotalCost(BigDecimal totalCost) throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByTotalCost", Report.class);
            tq.setParameter("totalCost", totalCost);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByTotalCost, Database Exception", e);
        }
    }

    public List<Report> findByTotalConsumption(BigDecimal totalConsumption) throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByTotalConsumption", Report.class);
            tq.setParameter("totalConsumption", totalConsumption);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByTotalConsumption, Database Exception", e);
        }
    }

    public List<Report> findByPlaceId(Long placeId) throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceId", Report.class);
            tq.setParameter("placeId", placeId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByPlaceId, Database Exception", e);
        }
    }

    public List<Report> findByPlaceNumberAndBuildingId(Integer placeNumber, Long buildingId) throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceNumberAndBuildingId", Report.class);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByPlaceNumberAndBuildingId, Database Exception", e);
        }
    }

    public List<Report> findByCategoryId(Long categoryId) throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByCategoryId", Report.class);
            tq.setParameter("categoryId", categoryId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByCategoryId, Database Exception", e);
        }
    }

    public List<Report> findByCategoryName(String categoryName) throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByCategoryName", Report.class);
            tq.setParameter("categoryName", categoryName);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByCategoryName, Database Exception", e);
        }
    }

    public List<Report> findByPlaceIdAndCategoryId(Long placeId, Long categoryId) throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceIdAndCategoryId", Report.class);
            tq.setParameter("placeId", placeId);
            tq.setParameter("categoryId", categoryId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByPlaceIdAndCategoryId, Database Exception", e);
        }
    }

    public List<Report> findByPlaceIdAndCategoryName(Long placeId, String name) throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceIdAndCategoryName", Report.class);
            tq.setParameter("placeId", placeId);
            tq.setParameter("name", name);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByPlaceIdAndCategoryName, Database Exception", e);
        }
    }

    public List<Report> findByPlaceNumberAndBuildingIdAndCategoryId(Integer placeNumber, Long buildingId,
                                                                    Long categoryId) throws DatabaseException {
        try {
            TypedQuery<Report> tq =
                em.createNamedQuery("Report.findByPlaceNumberAndBuildingIdAndCategoryId", Report.class);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            tq.setParameter("categoryId", categoryId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByPlaceNumberAndBuildingIdAndCategoryId, Database Exception", e);
        }
    }

    public List<Report> findByPlaceNumberAndCategoryName(Integer placeNumber, Long buildingId, String name)
        throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceNumberAndCategoryName", Report.class);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            tq.setParameter("name", name);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByPlaceNumberAndCategoryName, Database Exception", e);
        }
    }

    public List<Report> findByPlaceIdAndYear(Long placeId, Year year) throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceIdAndYear", Report.class);
            tq.setParameter("placeId", placeId);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByPlaceIdAndYear, Database Exception", e);
        }
    }

    public List<Report> findByPlaceNumberAndBuildingIdAndYear(Integer placeNumber, Long buildingId, Year year)
        throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceNumberAndBuildingIdAndYear", Report.class);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByPlaceNumberAndBuildingIdAndYear, Database Exception", e);
        }
    }

    public List<Report> findByCategoryIdAndYear(Long categoryId, Year year) throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByCategoryIdAndYear", Report.class);
            tq.setParameter("categoryId", categoryId);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByCategoryIdAndYear, Database Exception", e);
        }
    }

    public List<Report> findByCategoryNameAndYear(String categoryName, Year year) throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByCategoryNameAndYear", Report.class);
            tq.setParameter("categoryName", categoryName);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByCategoryNameAndYear, Database Exception", e);
        }
    }

    public List<Report> findByPlaceIdAndCategoryIdAndYear(Long placeId, Long categoryId, Year year)
        throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceIdAndCategoryIdAndYear", Report.class);
            tq.setParameter("placeId", placeId);
            tq.setParameter("categoryId", categoryId);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByPlaceIdAndCategoryIdAndYear, Database Exception", e);
        }
    }

    public List<Report> findByPlaceIdAndCategoryNameAndYear(Long placeId, String categoryName, Year year)
        throws DatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceIdAndCategoryNameAndYear", Report.class);
            tq.setParameter("placeId", placeId);
            tq.setParameter("categoryName", categoryName);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByPlaceIdAndCategoryNameAndYear, Database Exception", e);
        }
    }

    public List<Report> findByPlaceNumberAndBuildingIdAndCategoryIdAndYear(Integer placeNumber, Long buildingId,
                                                                           Long categoryId, Year year)
        throws DatabaseException {
        try {
            TypedQuery<Report> tq =
                em.createNamedQuery("Report.findByPlaceNumberAndBuildingIdAndCategoryIdAndYear", Report.class);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            tq.setParameter("categoryId", categoryId);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException("Report.findByPlaceNumberAndBuildingIdAndCategoryIdAndYear, Database Exception",
                e);
        }
    }

    public List<Report> findByPlaceNumberAndBuildingIdAndCategoryNameAndYear(Integer placeNumber, Long buildingId,
                                                                             String categoryName, Year year)
        throws DatabaseException {
        try {
            TypedQuery<Report> tq =
                em.createNamedQuery("Report.findByPlaceNumberAndBuildingIdAndCategoryNameAndYear", Report.class);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            tq.setParameter("categoryName", categoryName);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new DatabaseException(
                "Report.findByPlaceNumberAndBuildingIdAndCategoryNameAndYear, Database Exception", e);
        }
    }
}
