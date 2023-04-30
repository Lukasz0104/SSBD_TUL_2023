package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.ManagerData;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
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
