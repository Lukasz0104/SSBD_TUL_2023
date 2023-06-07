package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
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
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Report;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

@Stateless
@DenyAll
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

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByYear(Year year) throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByYear", Report.class);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByYear, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByTotalCost(BigDecimal totalCost) throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByTotalCost", Report.class);
            tq.setParameter("totalCost", totalCost);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByTotalCost, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByTotalConsumption(BigDecimal totalConsumption) throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByTotalConsumption", Report.class);
            tq.setParameter("totalConsumption", totalConsumption);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByTotalConsumption, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByPlaceId(Long placeId) throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceId", Report.class);
            tq.setParameter("placeId", placeId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByPlaceId, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByPlaceNumberAndBuildingId(Integer placeNumber, Long buildingId) throws
        AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceNumberAndBuildingId", Report.class);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByPlaceNumberAndBuildingId, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByCategoryId(Long categoryId) throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByCategoryId", Report.class);
            tq.setParameter("categoryId", categoryId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByCategoryId, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByCategoryName(String categoryName) throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByCategoryName", Report.class);
            tq.setParameter("categoryName", categoryName);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByCategoryName, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByPlaceIdAndCategoryId(Long placeId, Long categoryId) throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceIdAndCategoryId", Report.class);
            tq.setParameter("placeId", placeId);
            tq.setParameter("categoryId", categoryId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByPlaceIdAndCategoryId, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByPlaceIdAndCategoryName(Long placeId, String name) throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceIdAndCategoryName", Report.class);
            tq.setParameter("placeId", placeId);
            tq.setParameter("name", name);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByPlaceIdAndCategoryName, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByPlaceNumberAndBuildingIdAndCategoryId(Integer placeNumber, Long buildingId,
                                                                    Long categoryId) throws AppDatabaseException {
        try {
            TypedQuery<Report> tq =
                em.createNamedQuery("Report.findByPlaceNumberAndBuildingIdAndCategoryId", Report.class);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            tq.setParameter("categoryId", categoryId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByPlaceNumberAndBuildingIdAndCategoryId, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByPlaceNumberAndCategoryName(Integer placeNumber, Long buildingId, String name)
        throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceNumberAndCategoryName", Report.class);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            tq.setParameter("name", name);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByPlaceNumberAndCategoryName, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByPlaceIdAndYear(Long placeId, Year year) throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceIdAndYear", Report.class);
            tq.setParameter("placeId", placeId);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByPlaceIdAndYear, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByPlaceNumberAndBuildingIdAndYear(Integer placeNumber, Long buildingId, Year year)
        throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceNumberAndBuildingIdAndYear", Report.class);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByPlaceNumberAndBuildingIdAndYear, Database Exception", e);
        }
    }

    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public List<Report> findByBuildingIdAndYear(Long buildingId, Year year) throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByBuildingIdAndYear", Report.class);
            tq.setParameter("buildingId", buildingId);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByBuildingIdAndYear, Database Exception", e);
        }
    }


    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByCategoryIdAndYear(Long categoryId, Year year) throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByCategoryIdAndYear", Report.class);
            tq.setParameter("categoryId", categoryId);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByCategoryIdAndYear, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByCategoryNameAndYear(String categoryName, Year year) throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByCategoryNameAndYear", Report.class);
            tq.setParameter("categoryName", categoryName);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByCategoryNameAndYear, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByPlaceIdAndCategoryIdAndYear(Long placeId, Long categoryId, Year year)
        throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceIdAndCategoryIdAndYear", Report.class);
            tq.setParameter("placeId", placeId);
            tq.setParameter("categoryId", categoryId);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByPlaceIdAndCategoryIdAndYear, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByPlaceIdAndCategoryNameAndYear(Long placeId, String categoryName, Year year)
        throws AppDatabaseException {
        try {
            TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceIdAndCategoryNameAndYear", Report.class);
            tq.setParameter("placeId", placeId);
            tq.setParameter("categoryName", categoryName);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Report.findByPlaceIdAndCategoryNameAndYear, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByPlaceNumberAndBuildingIdAndCategoryIdAndYear(Integer placeNumber, Long buildingId,
                                                                           Long categoryId, Year year)
        throws AppDatabaseException {
        try {
            TypedQuery<Report> tq =
                em.createNamedQuery("Report.findByPlaceNumberAndBuildingIdAndCategoryIdAndYear", Report.class);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            tq.setParameter("categoryId", categoryId);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException(
                "Report.findByPlaceNumberAndBuildingIdAndCategoryIdAndYear, Database Exception",
                e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByPlaceNumberAndBuildingIdAndCategoryNameAndYear(Integer placeNumber, Long buildingId,
                                                                             String categoryName, Year year)
        throws AppDatabaseException {
        try {
            TypedQuery<Report> tq =
                em.createNamedQuery("Report.findByPlaceNumberAndBuildingIdAndCategoryNameAndYear", Report.class);
            tq.setParameter("placeNumber", placeNumber);
            tq.setParameter("buildingId", buildingId);
            tq.setParameter("categoryName", categoryName);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException(
                "Report.findByPlaceNumberAndBuildingIdAndCategoryNameAndYear, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Year> findReportYearsByPlaceId(Long placeId) {
        TypedQuery<Year> tq = em.createNamedQuery("Report.findYearsByPlaceId", Year.class);
        tq.setParameter("placeId", placeId);
        return tq.getResultList();
    }
}
