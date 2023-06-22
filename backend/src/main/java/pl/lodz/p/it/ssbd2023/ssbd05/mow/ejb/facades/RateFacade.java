package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.RateFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Stateless
@DenyAll
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    RateFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class RateFacade extends AbstractFacade<Rate> {
    @PersistenceContext(unitName = "ssbd05mowPU")
    private EntityManager em;

    public RateFacade() {
        super(Rate.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed(MANAGER)
    public void create(Rate entity) throws AppBaseException {
        super.create(entity);
    }

    @Override
    @RolesAllowed(MANAGER)
    public void edit(Rate entity) throws AppBaseException {
        super.edit(entity);
    }

    // CurrentRates

    @PermitAll
    public List<Rate> findCurrentRates() {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findCurrentRates", Rate.class);
        return tq.getResultList();
    }

    // Category

    @RolesAllowed({MANAGER})
    public Page<Rate> findByCategoryId(Long categoryId, int page, int pageSize) {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findByCategoryId", Rate.class);
        tq.setParameter("categoryId", categoryId);

        tq.setFirstResult(page * pageSize);
        tq.setMaxResults(pageSize);

        Long count = countByCategoryId(categoryId);
        return new Page<>(tq.getResultList(), count, pageSize, page);
    }

    @RolesAllowed({MANAGER})
    public Long countByCategoryId(Long categoryId) {
        TypedQuery<Long> tq = em.createNamedQuery("Rate.countByCategoryId", Long.class);
        tq.setParameter("categoryId", categoryId);
        return tq.getSingleResult();
    }

    @PermitAll
    public Optional<Rate> findCurrentRateByCategoryId(Long categoryId) {
        try {
            TypedQuery<Rate> tq = em.createNamedQuery("Rate.findCurrentRateByCategoryId", Rate.class);
            tq.setParameter("categoryId", categoryId);
            return Optional.of(tq.getSingleResult());
        } catch (PersistenceException pe) {
            return Optional.empty();
        }
    }

    @Override
    @PermitAll
    public Optional<Rate> find(Long id) {
        return super.find(id);
    }

    @Override
    @RolesAllowed(MANAGER)
    public void remove(Rate entity) throws AppBaseException {
        super.remove(entity);
    }

    @PermitAll
    public Rate findFirstInYear(LocalDate maxDate, Long categoryId) {
        return em.createNamedQuery("Rate.findByEffectiveDateBeforeOrderedByDate", Rate.class)
            .setParameter("effectiveDate", maxDate)
            .setParameter("categoryId", categoryId)
            .setMaxResults(1)
            .getSingleResult();
    }

    @PermitAll
    public List<Rate> findByYearAndCategoryId(Year year, Long categoryId) {
        return em.createNamedQuery("Rate.findByYearAndCategoryId", Rate.class)
            .setParameter("categoryId", categoryId)
            .setParameter("year", year.getValue())
            .getResultList();
    }
}
