package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.impl;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Category;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.CategoryNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.CategoryFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.RateFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.RateManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;

import java.util.List;
import java.util.Optional;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericManagerExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class RateManager extends AbstractManager implements RateManagerLocal, SessionSynchronization {

    @Inject
    private RateFacade rateFacade;

    @Inject
    private CategoryFacade categoryFacade;

    @Override
    @PermitAll
    public List<Rate> getCurrentRates() throws AppBaseException {
        return rateFacade.findCurrentRates();
    }

    @Override
    @RolesAllowed(MANAGER)
    public void createRate(Rate newRate, Long categoryId) throws AppBaseException {
        Optional<Category> category = categoryFacade.find(categoryId);
        newRate.setCategory(category.orElseThrow(CategoryNotFoundException::new));
        rateFacade.create(newRate);
    }

    @Override
    @RolesAllowed(MANAGER)
    public void removeFutureRate(Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
