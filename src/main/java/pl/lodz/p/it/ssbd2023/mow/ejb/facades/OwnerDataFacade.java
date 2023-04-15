package pl.lodz.p.it.ssbd2023.mow.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.shared.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class OwnerDataFacade extends AbstractFacade<OwnerData> {

    @PersistenceContext(unitName = "ssbd05mowPU")
    private EntityManager em;

    public OwnerDataFacade() {
        super(OwnerData.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
