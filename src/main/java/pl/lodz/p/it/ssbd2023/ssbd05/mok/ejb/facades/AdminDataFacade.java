package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AdminData;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AdminDataFacade extends AbstractFacade<AdminData> {

    @PersistenceContext(unitName = "ssbd05mokPU")
    private EntityManager em;

    public AdminDataFacade() {
        super(AdminData.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
