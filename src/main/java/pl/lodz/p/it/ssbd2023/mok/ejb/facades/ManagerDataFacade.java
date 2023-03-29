package pl.lodz.p.it.ssbd2023.mok.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.entities.ManagerData;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ManagerDataFacade extends AbstractFacade<ManagerData> {

    @PersistenceContext(unitName = "ssbd05mokPU")
    private EntityManager em;

    public ManagerDataFacade() {
        super(ManagerData.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ManagerData findByLicenseNumber(String licenseNumber) {
        TypedQuery<ManagerData> tq =
            em.createNamedQuery("ManagerData.findManagerDataByLicenseNumber", ManagerData.class);
        tq.setParameter("licenseNumber", licenseNumber);
        return tq.getSingleResult();
    }
}
