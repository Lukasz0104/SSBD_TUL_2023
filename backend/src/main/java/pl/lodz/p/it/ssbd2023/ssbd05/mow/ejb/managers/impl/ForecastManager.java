package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.impl;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ForecastFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ForecastManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericManagerExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class ForecastManager extends AbstractManager implements ForecastManagerLocal, SessionSynchronization {

    @Inject
    private ForecastFacade forecastFacade;

    @Override
    @RolesAllowed(MANAGER)
    public void createOverdueForecast() throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
