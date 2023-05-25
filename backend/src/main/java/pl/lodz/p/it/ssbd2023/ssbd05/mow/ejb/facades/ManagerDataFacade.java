package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.ManagerData;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

@Stateless
@DenyAll
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ManagerDataFacade extends AbstractFacade<ManagerData> {

    @PersistenceContext(unitName = "ssbd05mowPU")
    private EntityManager em;

    public ManagerDataFacade() {
        super(ManagerData.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public ManagerData findByLicenseNumber(String licenseNumber) {
        TypedQuery<ManagerData> tq =
            em.createNamedQuery("ManagerData.findManagerDataByLicenseNumber", ManagerData.class);
        tq.setParameter("licenseNumber", licenseNumber);
        return tq.getSingleResult();
    }
}
