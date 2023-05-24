package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Category;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;

import java.util.List;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericManagerExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class CategoryManager extends AbstractManager implements CategoryManagerLocal, SessionSynchronization {
    @Override
    @RolesAllowed("MANAGER")
    public List<Category> getAllCategories() throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed("MANAGER")
    public List<Rate> getCategoryRates(Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
