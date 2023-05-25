package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Category;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@DenyAll
public class CategoryFacade extends AbstractFacade<Category> {

    @PersistenceContext(unitName = "ssbd05mowPU")
    private EntityManager em;

    public CategoryFacade() {
        super(Category.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public Category findByName(String name) throws AppDatabaseException {
        try {
            TypedQuery<Category> tq = em.createNamedQuery("Category.findByName", Category.class);
            tq.setParameter("name", name);
            return tq.getSingleResult();
        } catch (PersistenceException e) {
            throw new AppDatabaseException("Category.findByName", e);
        }
    }
}
