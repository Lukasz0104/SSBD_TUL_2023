package pl.lodz.p.it.ssbd2023.mow.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.entities.mow.AccountingRule;
import pl.lodz.p.it.ssbd2023.entities.mow.Category;
import pl.lodz.p.it.ssbd2023.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.shared.AbstractFacade;

import java.time.LocalDate;
import java.util.List;

@Stateless
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

    //CurrentRates

    public List<Rate> findCurrentRates() {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findCurrentRates", Rate.class);
        return tq.getResultList();
    }

    //AccountingRules

    public List<Rate> findByAccountingRule(AccountingRule accountingRule) {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findByAccountingRule", Rate.class);
        tq.setParameter("accounting_rule", accountingRule);
        return tq.getResultList();
    }

    public List<Rate> findByEffectiveDateAndAccountingRule(LocalDate effectiveDate, AccountingRule accountingRule) {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findByEffectiveDateAndAccountingRule", Rate.class);
        tq.setParameter("effectiveDate", effectiveDate);
        tq.setParameter("ar", accountingRule);
        return tq.getResultList();
    }

    public List<Rate> findByEffectiveDateBeforeAndAccountingRule(LocalDate effectiveDate,
                                                                 AccountingRule accountingRule) {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findByEffectiveDateBeforeAndAccountingRule", Rate.class);
        tq.setParameter("effectiveDate", effectiveDate);
        tq.setParameter("ar", accountingRule);
        return tq.getResultList();
    }

    public List<Rate> findByEffectiveDateAfterAndAccountingRule(LocalDate effectiveDate,
                                                                AccountingRule accountingRule) {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findByEffectiveDateAfterAndAccountingRule", Rate.class);
        tq.setParameter("effectiveDate", effectiveDate);
        tq.setParameter("ar", accountingRule);
        return tq.getResultList();
    }


    // Category
    public List<Rate> findByCategory(Category category) {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findByCategory", Rate.class);
        tq.setParameter("category", category);
        return tq.getResultList();
    }

    public List<Rate> findByEffectiveDateAndCategory(LocalDate effectiveDate, Category category) {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findByEffectiveDateAndCategory", Rate.class);
        tq.setParameter("effectiveDate", effectiveDate);
        tq.setParameter("category", category);
        return tq.getResultList();
    }

    public List<Rate> findByEffectiveDateBeforeAndCategory(LocalDate effectiveDate, Category category) {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findByEffectiveDateBeforeAndCategory", Rate.class);
        tq.setParameter("effectiveDate", effectiveDate);
        tq.setParameter("category", category);
        return tq.getResultList();
    }

    public List<Rate> findByEffectiveDateAfterAndCategory(LocalDate effectiveDate, Category category) {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findByEffectiveDateAfterAndCategory", Rate.class);
        tq.setParameter("effectiveDate", effectiveDate);
        tq.setParameter("category", category);
        return tq.getResultList();
    }


    public List<Rate> findByCategoryAndAccountingRule(Category category, AccountingRule accountingRule) {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findByCategoryAndAccountingRule", Rate.class);
        tq.setParameter("category", category);
        tq.setParameter("ar", accountingRule);
        return tq.getResultList();
    }

    public List<Rate> findByEffectiveDateAndCategoryAndAccountingRule(LocalDate effectiveDate, Category category,
                                                                      AccountingRule accountingRule) {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findByEffectiveDateAndCategoryAndAccountingRule", Rate.class);
        tq.setParameter("effectiveDate", effectiveDate);
        tq.setParameter("category", category);
        tq.setParameter("accounting_rule", accountingRule);
        return tq.getResultList();
    }

    public List<Rate> findByEffectiveDateBeforeAndCategoryAndAccountingRule(LocalDate effectiveDate, Category category,
                                                                            AccountingRule accountingRule) {
        TypedQuery<Rate> tq =
                em.createNamedQuery("Rate.findByEffectiveDateBeforeAndCategoryAndAccountingRule", Rate.class);
        tq.setParameter("effectiveDate", effectiveDate);
        tq.setParameter("category", category);
        tq.setParameter("accounting_rule", accountingRule);
        return tq.getResultList();
    }

    public List<Rate> findByEffectiveDateAfterAndCategoryAndAccountingRule(LocalDate effectiveDate, Category category,
                                                                           AccountingRule accountingRule) {
        TypedQuery<Rate> tq =
                em.createNamedQuery("Rate.findByEffectiveDateAfterAndCategoryAndAccountingRule", Rate.class);
        tq.setParameter("effectiveDate", effectiveDate);
        tq.setParameter("category", category);
        tq.setParameter("accounting_rule", accountingRule);
        return tq.getResultList();
    }

    public List<Rate> findByEffectiveDate(LocalDate effectiveDate) {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findByEffectiveDate", Rate.class);
        tq.setParameter("effectiveDate", effectiveDate);
        return tq.getResultList();
    }

    public List<Rate> findByEffectiveDateBefore(LocalDate effectiveDate) {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findByEffectiveDateBefore", Rate.class);
        tq.setParameter("effectiveDate", effectiveDate);
        return tq.getResultList();
    }

    public List<Rate> findByEffectiveDateAfter(LocalDate effectiveDate) {
        TypedQuery<Rate> tq = em.createNamedQuery("Rate.findByEffectiveDateAfter", Rate.class);
        tq.setParameter("effectiveDate", effectiveDate);
        return tq.getResultList();
    }

}
