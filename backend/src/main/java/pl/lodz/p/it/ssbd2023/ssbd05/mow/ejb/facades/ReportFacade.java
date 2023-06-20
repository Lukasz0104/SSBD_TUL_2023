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
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Report;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.time.Year;
import java.util.List;

@Stateless
@DenyAll
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class
})
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


    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public List<Report> findByYear(Year year) {
        TypedQuery<Report> tq = em.createNamedQuery("Report.findByYear", Report.class);
        tq.setParameter("year", year);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Report> findByPlaceIdAndYear(Long placeId, Year year) {
        TypedQuery<Report> tq = em.createNamedQuery("Report.findByPlaceIdAndYear", Report.class);
        tq.setParameter("placeId", placeId);
        tq.setParameter("year", year);
        return tq.getResultList();
    }

    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public List<Report> findByBuildingIdAndYear(Long buildingId, Year year) {
        TypedQuery<Report> tq = em.createNamedQuery("Report.findByBuildingIdAndYear", Report.class);
        tq.setParameter("buildingId", buildingId);
        tq.setParameter("year", year);
        return tq.getResultList();
    }

    @RolesAllowed({OWNER, MANAGER})
    public List<Year> findReportYearsByPlaceId(Long placeId) {
        TypedQuery<Year> tq = em.createNamedQuery("Report.findYearsByPlaceId", Year.class);
        tq.setParameter("placeId", placeId);
        return tq.getResultList();
    }

    @Override
    @PermitAll
    public void create(Report entity) throws AppBaseException {
        super.create(entity);
    }
}
