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
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Forecast;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
@DenyAll
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ForecastFacade extends AbstractFacade<Forecast> {
    @PersistenceContext(unitName = "ssbd05mowPU")
    private EntityManager em;

    public ForecastFacade() {
        super(Forecast.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    @RolesAllowed({OWNER, MANAGER})
    public void edit(Forecast forecast) throws AppBaseException {
        super.edit(forecast);
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByYear(Year year) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByYear", Forecast.class);
        tq.setParameter("year", year);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByMonth(Month month) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonth", Forecast.class);
        tq.setParameter("month", month);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByMonthAndYear(Month month, Year year) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonthAndYear", Forecast.class);
        tq.setParameter("month", month);
        tq.setParameter("year", year);
        return tq.getResultList();
    }

    // PlaceId

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByPlaceId(Long placeId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByPlaceId", Forecast.class);
        tq.setParameter("place", placeId);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByPlaceIdAndMonth(Long placeId, Month month) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByPlaceIdAndMonth", Forecast.class);
        tq.setParameter("place", placeId);
        tq.setParameter("month", month);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByPlaceIdAndYear(Long placeId, Year year) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByPlaceIdAndYear", Forecast.class);
        tq.setParameter("place", placeId);
        tq.setParameter("year", year);
        return tq.getResultList();
    }


    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByPlaceIdAndYearAndMonth(Long placeId, Year year, Month month) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByPlaceIdAndYearAndMonth", Forecast.class);
        tq.setParameter("place", placeId);
        tq.setParameter("year", year);
        tq.setParameter("month", month);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByPlaceIdAndCategory(Long placeId, Long categoryId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByPlaceIdAndCategoryId", Forecast.class);
        tq.setParameter("place", placeId);
        tq.setParameter("categoryId", categoryId);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findFutureByPlaceIdAndCategoryAndYear(Long placeId, Long categoryId, Year year, Month month) {
        TypedQuery<Forecast> tq =
            em.createNamedQuery("Forecast.findByPlaceIdAndCategoryIdAndYearAndAfterMonth", Forecast.class);
        tq.setParameter("place", placeId);
        tq.setParameter("categoryId", categoryId);
        tq.setParameter("year", year);
        tq.setParameter("month", month);
        return tq.getResultList();
    }

    // Category

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByCategory(Long categoryId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByCategoryId", Forecast.class);
        tq.setParameter("categoryId", categoryId);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByYearAndCategory(Year year, Long categoryId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByYearAndCategoryId", Forecast.class);
        tq.setParameter("year", year);
        tq.setParameter("categoryId", categoryId);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByMonthAndCategory(Month month, Long categoryId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonthAndCategoryId", Forecast.class);
        tq.setParameter("month", month);
        tq.setParameter("categoryId", categoryId);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByMonthAndYearAndCategory(Month month, Year year, Long categoryId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonthAndYearAndCategoryId", Forecast.class);
        tq.setParameter("month", month);
        tq.setParameter("year", year);
        tq.setParameter("categoryId", categoryId);
        return tq.getResultList();
    }

    // Rate

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByRateId(Long rateId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByRateId", Forecast.class);
        tq.setParameter("rate", rateId);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByYearAndRateId(Year year, Long rateId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByYearAndRateId", Forecast.class);
        tq.setParameter("year", year);
        tq.setParameter("rate", rateId);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByMonthAndRateId(Month month, Long rateId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonthAndRateId", Forecast.class);
        tq.setParameter("month", month);
        tq.setParameter("rate", rateId);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByMonthAndYearAndRateId(Month month, Year year, Long rateId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonthAndYearAndRateId", Forecast.class);
        tq.setParameter("month", month);
        tq.setParameter("year", year);
        tq.setParameter("rate", rateId);
        return tq.getResultList();
    }


    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public List<Forecast> findByBuildingIdAndYear(Long id, Year year) throws AppBaseException {
        try {
            TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByBuildingIdAndYear", Forecast.class);
            tq.setParameter("buildingId", id);
            tq.setParameter("year", year);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Forecast.findByBuildingIdAndYear, Database Exception", e);
        }
    }

    @RolesAllowed({OWNER, MANAGER, ADMIN})
    public Map<Integer, Integer> findYearsAndMonthsByBuildingId(Long id) {
        TypedQuery<Object[]> tq = em.createNamedQuery("Forecast.findYearsAndMonthsByBuildingId", Object[].class);
        tq.setParameter("id", id);
        return tq.getResultStream().collect(Collectors.toMap(
            obj -> ((Year)obj[0]).getValue(),
            obj -> ((Long)obj[1]).intValue()
        ));
    }

    @RolesAllowed({OWNER, MANAGER, ADMIN})
    public List<Forecast> findByBuildingIdAndYearAndMonth(Long id, Year year, Month month) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByBuildingIdAndYearAndMonth", Forecast.class);
        tq.setParameter("id", id);
        tq.setParameter("year", year);
        tq.setParameter("month", month);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Year> findForecastYearByPlaceId(Long placeId) {
        TypedQuery<Year> tq = em.createNamedQuery("Forecast.findForecastYearsByPlaceId", Year.class);
        tq.setParameter("placeId", placeId);
        return tq.getResultList();
    }
}
