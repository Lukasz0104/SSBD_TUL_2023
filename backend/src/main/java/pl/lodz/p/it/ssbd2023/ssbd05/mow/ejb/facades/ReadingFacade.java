package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

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
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Reading;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.util.List;

@Stateless
@DenyAll
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class
})
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

    @PermitAll
    public List<Reading> findReliableReadingsFromLastDayOfYear(Long placeId, Long categoryId, Integer year) {
        return em.createNamedQuery("Reading.findReliableReadingsFromLastDayOfYear", Reading.class)
            .setParameter("placeId", placeId)
            .setParameter("categoryId", categoryId)
            .setParameter("year", year)
            .setMaxResults(2)
            .getResultList();
    }
}
