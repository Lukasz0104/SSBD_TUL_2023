package pl.lodz.p.it.ssbd2023.mow.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.entities.mow.Category;
import pl.lodz.p.it.ssbd2023.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2023.shared.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
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

    public Category findByName(String name) throws DatabaseException {
        try {
            TypedQuery<Category> tq = em.createNamedQuery("Category.findByName", Category.class);
            tq.setParameter("name", name);
            return tq.getSingleResult();
        } catch (PersistenceException e) {
            throw new DatabaseException("Category.findByName", e);
        }
    }
}
