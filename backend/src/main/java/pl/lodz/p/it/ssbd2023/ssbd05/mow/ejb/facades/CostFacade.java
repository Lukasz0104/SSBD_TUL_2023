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
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Cost;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.CostFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Stateless
@DenyAll
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    CostFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class
})
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
    public List<Cost> findByCategoryId(Long categoryId) throws AppDatabaseException {
        try {
            TypedQuery<Cost> tq = em.createNamedQuery("Cost.findByCategoryId", Cost.class);
            tq.setParameter("categoryId", categoryId);
            return tq.getResultList();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Cost.findByCategoryId, Database Exception", e);
        }
    }

    @PermitAll
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

    @RolesAllowed({MANAGER})
    public Page<Cost> findByYearAndMonthAndCategoryName(int page, int pageSize, boolean order, Year year,
                                                        String categoryName) {
        TypedQuery<Cost> tq;

        if (order) {
            tq = em.createNamedQuery("Cost.findByYearAndCategoryNameAsc", Cost.class);
        } else {
            tq = em.createNamedQuery("Cost.findByYearAndCategoryNameDesc", Cost.class);
        }

        tq.setParameter("year", year);
        tq.setParameter("categoryName", categoryName);
        tq.setFirstResult(page * pageSize);
        tq.setMaxResults(pageSize);
        List<Cost> list = tq.getResultList();

        Long count = countCostsByYearAndMonthAndCategoryName(year, categoryName);
        return new Page<>(list, count, pageSize, page);

    }

    @RolesAllowed({MANAGER})
    public Long countCostsByYearAndMonthAndCategoryName(Year year,
                                                        String categoryName) {
        TypedQuery<Long> tq = em.createNamedQuery("Cost.countByYearAndCategoryName", Long.class);
        tq.setParameter("year", year);
        tq.setParameter("categoryName", categoryName);
        return tq.getSingleResult();
    }

    @Override
    @RolesAllowed({MANAGER})
    public List<Cost> findAll() {
        return super.findAll();
    }

    @Override
    @RolesAllowed({MANAGER})
    public Optional<Cost> find(Long id) {
        return super.find(id);
    }

    @Override
    @RolesAllowed({MANAGER})
    public void create(Cost entity) throws AppBaseException {
        super.create(entity);
    }

    @Override
    @RolesAllowed({MANAGER})
    public void remove(Cost entity) throws AppBaseException {
        super.remove(entity);
    }

    @RolesAllowed({MANAGER})
    public List<Year> findDistinctYears() {
        TypedQuery<Year> tq = em.createNamedQuery("Cost.findDistinctYears", Year.class);
        return tq.getResultList();
    }

    @RolesAllowed({MANAGER})
    public List<String> findDistinctCategoryNamesFromCosts() {
        TypedQuery<String> tq = em.createNamedQuery("Cost.findDistinctCategoryNames", String.class);
        return tq.getResultList();
    }

    @RolesAllowed(MANAGER)
    public BigDecimal sumConsumptionForCategoryAndMonthBefore(Year year, Long categoryId, Month month) {
        try {
            return em.createNamedQuery("Cost.sumConsumptionForCategoryAndYearAndMonthBefore", BigDecimal.class)
                .setParameter("year", year)
                .setParameter("categoryId", categoryId)
                .setParameter("month", month)
                .getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    @RolesAllowed(MANAGER)
    public BigDecimal sumConsumptionForCategoryAndMonth(Year year, Long categoryId, Month month) {
        try {
            return em.createNamedQuery("Cost.sumConsumptionForCategoryAndYearAndMonth", BigDecimal.class)
                .setParameter("year", year)
                .setParameter("categoryId", categoryId)
                .setParameter("month", month)
                .getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }
}
