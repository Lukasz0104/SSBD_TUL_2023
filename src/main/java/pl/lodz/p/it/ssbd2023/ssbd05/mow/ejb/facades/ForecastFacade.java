package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Forecast;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.time.Month;
import java.time.Year;
import java.util.List;

@Stateless
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

    // Date

    public List<Forecast> findByYear(Year year) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByYear", Forecast.class);
        tq.setParameter("year", year);
        return tq.getResultList();
    }

    public List<Forecast> findByMonth(Month month) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonth", Forecast.class);
        tq.setParameter("month", month);
        return tq.getResultList();
    }

    public List<Forecast> findByMonthAndYear(Month month, Year year) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonthAndYear", Forecast.class);
        tq.setParameter("month", month);
        tq.setParameter("year", year);
        return tq.getResultList();
    }

    // PlaceId

    public List<Forecast> findByPlaceId(Long placeId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByPlaceId", Forecast.class);
        tq.setParameter("place", placeId);
        return tq.getResultList();
    }

    public List<Forecast> findByPlaceIdAndMonth(Long placeId, Month month) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByPlaceIdAndMonth", Forecast.class);
        tq.setParameter("place", placeId);
        tq.setParameter("month", month);
        return tq.getResultList();
    }

    public List<Forecast> findByPlaceIdAndYear(Long placeId, Year year) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByPlaceIdAndYear", Forecast.class);
        tq.setParameter("place", placeId);
        tq.setParameter("year", year);
        return tq.getResultList();
    }

    public List<Forecast> findByPlaceIdAndYearAndMonth(Long placeId, Year year, Month month) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByPlaceIdAndYearAndMonth", Forecast.class);
        tq.setParameter("place", placeId);
        tq.setParameter("year", year);
        tq.setParameter("month", month);
        return tq.getResultList();
    }


    public List<Forecast> findByPlaceIdAndCategory(Long placeId, Long categoryId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByPlaceIdAndCategoryId", Forecast.class);
        tq.setParameter("place", placeId);
        tq.setParameter("categoryId", categoryId);
        return tq.getResultList();
    }

    // Category

    public List<Forecast> findByCategory(Long categoryId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByCategoryId", Forecast.class);
        tq.setParameter("categoryId", categoryId);
        return tq.getResultList();
    }

    public List<Forecast> findByYearAndCategory(Year year, Long categoryId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByYearAndCategoryId", Forecast.class);
        tq.setParameter("year", year);
        tq.setParameter("categoryId", categoryId);
        return tq.getResultList();
    }

    public List<Forecast> findByMonthAndCategory(Month month, Long categoryId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonthAndCategoryId", Forecast.class);
        tq.setParameter("month", month);
        tq.setParameter("categoryId", categoryId);
        return tq.getResultList();
    }

    public List<Forecast> findByMonthAndYearAndCategory(Month month, Year year, Long categoryId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonthAndYearAndCategoryId", Forecast.class);
        tq.setParameter("month", month);
        tq.setParameter("year", year);
        tq.setParameter("categoryId", categoryId);
        return tq.getResultList();
    }

    // Rate

    public List<Forecast> findByRateId(Long rateId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByRateId", Forecast.class);
        tq.setParameter("rate", rateId);
        return tq.getResultList();
    }

    public List<Forecast> findByYearAndRateId(Year year, Long rateId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByYearAndRateId", Forecast.class);
        tq.setParameter("year", year);
        tq.setParameter("rate", rateId);
        return tq.getResultList();
    }

    public List<Forecast> findByMonthAndRateId(Month month, Long rateId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonthAndRateId", Forecast.class);
        tq.setParameter("month", month);
        tq.setParameter("rate", rateId);
        return tq.getResultList();
    }

    public List<Forecast> findByMonthAndYearAndRateId(Month month, Year year, Long rateId) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonthAndYearAndRateId", Forecast.class);
        tq.setParameter("month", month);
        tq.setParameter("year", year);
        tq.setParameter("rate", rateId);
        return tq.getResultList();
    }

}
