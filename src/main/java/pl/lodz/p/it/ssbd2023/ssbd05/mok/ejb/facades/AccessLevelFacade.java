package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccessLevelFacade extends AbstractFacade<AccessLevel> {

    @PersistenceContext(unitName = "ssbd05mokPU")
    private EntityManager em;

    public AccessLevelFacade() {
        super(AccessLevel.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<AccessLevel> findByAccountId(Long accountId) {
        TypedQuery<AccessLevel> tq = em.createNamedQuery("AccessLevel.findByAccountId", AccessLevel.class);
        tq.setParameter("accountId", accountId);
        return tq.getResultList();
    }

    public List<AccessLevel> findActiveByAccountId(Long accountId) {
        TypedQuery<AccessLevel> tq = em.createNamedQuery("AccessLevel.findActiveByAccountId", AccessLevel.class);
        tq.setParameter("accountId", accountId);
        return tq.getResultList();
    }
}
