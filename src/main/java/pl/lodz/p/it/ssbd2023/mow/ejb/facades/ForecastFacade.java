package pl.lodz.p.it.ssbd2023.mow.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.entities.mow.Category;
import pl.lodz.p.it.ssbd2023.entities.mow.Forecast;
import pl.lodz.p.it.ssbd2023.shared.AbstractFacade;

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

    //Date

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

    //PlaceId

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


    public List<Forecast> findByPlaceIdAndCategory(Long placeId, Category category) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByPlaceIdAndCategory", Forecast.class);
        tq.setParameter("place", placeId);
        tq.setParameter("category", category);
        return tq.getResultList();
    }

    //Category

    public List<Forecast> findByCategory(Category category) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByCategory", Forecast.class);
        tq.setParameter("category", category);
        return tq.getResultList();
    }

    public List<Forecast> findByYearAndCategory(Year year, Category category) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByYearAndCategory", Forecast.class);
        tq.setParameter("year", year);
        tq.setParameter("category", category);
        return tq.getResultList();
    }

    public List<Forecast> findByMonthAndCategory(Month month, Category category) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonthAndCategory", Forecast.class);
        tq.setParameter("month", month);
        tq.setParameter("category", category);
        return tq.getResultList();
    }

    public List<Forecast> findByMonthAndYearAndCategory(Month month, Year year, Category category) {
        TypedQuery<Forecast> tq = em.createNamedQuery("Forecast.findByMonthAndYearAndCategory", Forecast.class);
        tq.setParameter("month", month);
        tq.setParameter("year", year);
        tq.setParameter("category", category);
        return tq.getResultList();
    }

    //Rate

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
