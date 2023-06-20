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
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Forecast;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.ForecastFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.time.Month;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
@DenyAll
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    ForecastFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class
})
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


    @PermitAll
    public void edit(Forecast forecast) throws AppBaseException {
        super.edit(forecast);
    }

    @Override
    @PermitAll
    public void create(Forecast entity) throws AppBaseException {
        super.create(entity);
    }

    // PlaceId

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

    @RolesAllowed({MANAGER})
    public Optional<Forecast> findByPlaceIdAndCategoryIdAndYearAndMonth(Long placeId, Long categoryId, Year year,
                                                                        Month month) {
        try {
            TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByPlaceIdAndCategoryIdAndYearAndMonth",
                Forecast.class);
            tq.setParameter("place", placeId);
            tq.setParameter("categoryId", categoryId);
            tq.setParameter("year", year);
            tq.setParameter("month", month);
            return Optional.of(tq.getSingleResult());
        } catch (PersistenceException pe) {
            return Optional.empty();
        }
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByPlaceIdAndYearAndBeforeMonth(Long placeId, Year year, Month month) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByPlaceIdAndYearAndBeforeMonth", Forecast.class);
        tq.setParameter("placeId", placeId);
        tq.setParameter("year", year);
        tq.setParameter("month", month);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public Optional<Month> findMinMonthByPlaceIdAndYear(Long placeId, Year year) {
        TypedQuery<Month> tq = em.createNamedQuery("Forecast.findMinMonthByPlaceIdAndYear", Month.class);
        tq.setParameter("id", placeId);
        tq.setParameter("year", year);
        try {
            return Optional.of(tq.getSingleResult());
        } catch (NoResultException | NullPointerException nre) {
            return Optional.empty();
        }
    }

    @PermitAll
    public List<Forecast> findFutureByPlaceIdAndCategoryAndYear(Long placeId, Long categoryId, Year year, Month month) {
        TypedQuery<Forecast> tq =
            em.createNamedQuery("Forecast.findByPlaceIdAndCategoryIdAndYearAndAfterMonth", Forecast.class);
        tq.setParameter("place", placeId);
        tq.setParameter("categoryId", categoryId);
        tq.setParameter("year", year);
        tq.setParameter("month", month);
        return tq.getResultList();
    }

    @PermitAll
    public List<Forecast> findFutureAndCurrentByPlaceIdAndCategoryAndYear(Long placeId, Long categoryId, Year year,
                                                                          Month month) {
        TypedQuery<Forecast> tq =
            em.createNamedQuery("Forecast.findByPlaceIdAndCategoryIdAndYearAndAfterOrEqualMonth", Forecast.class);
        tq.setParameter("place", placeId);
        tq.setParameter("categoryId", categoryId);
        tq.setParameter("year", year);
        tq.setParameter("month", month);
        return tq.getResultList();
    }

    // Category

    @RolesAllowed({OWNER, MANAGER})
    public List<Forecast> findByMonthAndYearAndCategory(Month month, Year year, Long categoryId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonthAndYearAndCategoryId", Forecast.class);
        tq.setParameter("month", month);
        tq.setParameter("year", year);
        tq.setParameter("categoryId", categoryId);
        return tq.getResultList();
    }

    // Rate


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
            obj -> ((Year) obj[0]).getValue(),
            obj -> ((Long) obj[1]).intValue()
        ));
    }

    @RolesAllowed(MANAGER)
    public Map<Integer, List<Integer>> findYearsAndMonths() {
        TypedQuery<Object[]> tq = em.createNamedQuery("Forecast.findYearsAndMonths", Object[].class);
        Map<Integer, List<Integer>> ret = new HashMap<>();

        tq.getResultStream().forEach((obj) -> ret.putIfAbsent(((Year) obj[0]).getValue(),
                tq.getResultStream().filter(o -> ((Year) o[0]).getValue() == ((Year) obj[0]).getValue())
                    .map(x -> ((Month) x[1]).getValue()).collect(
                        Collectors.toList())
            )
        );
        return ret;
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

    @RolesAllowed(MANAGER)
    public void deleteFutureForecastsByCategoryIdAndPlaceId(Long categoryId, Long placeId, Year year, Month month) {
        TypedQuery<Forecast> tq =
            em.createNamedQuery("Forecast.deleteFutureForecastsByCategoryIdAndPlaceId", Forecast.class);
        tq.setParameter("categoryId", categoryId);
        tq.setParameter("placeId", placeId);
        tq.setParameter("year", year);
        tq.setParameter("month", month);
        tq.executeUpdate();
        em.flush();
    }

    @RolesAllowed(MANAGER)
    public List<Forecast> findByYearAndCategoryName(Year year, String categoryName) {
        return em.createNamedQuery("Forecast.findByYearAndCategoryName", Forecast.class)
            .setParameter("year", year)
            .setParameter("categoryName", categoryName)
            .getResultList();
    }

    @RolesAllowed(MANAGER)
    public List<Forecast> findByYearAndCategoryNameAndMonthBefore(Year year, String categoryName, Month month) {
        return em.createNamedQuery("Forecast.findByYearAndCategoryNameAndMonthBefore", Forecast.class)
            .setParameter("year", year)
            .setParameter("categoryName", categoryName)
            .setParameter("month", month)
            .getResultList();
    }
}
