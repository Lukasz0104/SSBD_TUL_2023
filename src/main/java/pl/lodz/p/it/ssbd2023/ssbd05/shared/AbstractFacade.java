package pl.lodz.p.it.ssbd2023.ssbd05.shared;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;

import java.util.List;
import java.util.Optional;

public abstract class AbstractFacade<T> {

    private final Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    protected void create(T entity) throws AppBaseException {
        getEntityManager().persist(entity);
        getEntityManager().flush();
    }

    protected void edit(T entity) throws AppBaseException {
        getEntityManager().merge(entity);
        getEntityManager().flush();
    }

    protected void remove(T entity) throws AppBaseException {
        getEntityManager().remove(getEntityManager().merge(entity));
        getEntityManager().flush();
    }

    public Optional<T> find(Object id) {
        return Optional.ofNullable(getEntityManager().find(entityClass, id));
    }

    public List<T> findAll() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
}
