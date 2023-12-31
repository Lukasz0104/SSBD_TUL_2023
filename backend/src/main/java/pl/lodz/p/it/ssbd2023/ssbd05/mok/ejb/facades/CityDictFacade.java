package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.CityDict;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class CityDictFacade extends AbstractFacade<CityDict> {
    @PersistenceContext(unitName = "ssbd05mokPU")
    private EntityManager em;

    public CityDictFacade() {
        super(CityDict.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @PermitAll
    public void create(CityDict entity) throws AppBaseException {
        try {
            super.create(entity);
        } catch (DatabaseException | PersistenceException exp) {
            throw new AppDatabaseException(exp);
        }
    }

    @Override
    @PermitAll
    public void edit(CityDict entity) throws AppBaseException {
        super.edit(entity);
    }

    @Override
    @RolesAllowed(ADMIN)
    public void remove(CityDict entity) throws AppBaseException {
        super.remove(entity);
    }


    @PermitAll
    public List<String> findCitiesStartingWithAndLimitTo10(String pattern) {
        TypedQuery<String> tq =
            em.createNamedQuery("CityDict.findCitiesStartingWithLimit10", String.class);
        tq.setParameter("pattern", pattern);
        tq.setMaxResults(10);
        return tq.getResultList();
    }
}