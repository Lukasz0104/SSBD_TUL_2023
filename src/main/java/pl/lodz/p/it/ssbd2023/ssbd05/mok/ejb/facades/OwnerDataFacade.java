package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class OwnerDataFacade extends AbstractFacade<OwnerData> {

    @PersistenceContext(unitName = "ssbd05mokPU")
    private EntityManager em;

    public OwnerDataFacade() {
        super(OwnerData.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
