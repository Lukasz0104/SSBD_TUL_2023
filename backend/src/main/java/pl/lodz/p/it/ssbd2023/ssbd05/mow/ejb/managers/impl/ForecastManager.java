package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.impl;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.UnaccessibleReportException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ForecastFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.PlaceFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ForecastManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;

import java.time.Year;
import java.util.List;
import java.util.Objects;

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

    @Inject
    private PlaceFacade placeFacade;

    @Override
    @RolesAllowed(MANAGER)
    public void createOverdueForecast() throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed({MANAGER})
    public List<Integer> getForecastYearsByPlaceId(Long placeId) {
        return forecastFacade.findForecastYearByPlaceId(placeId).stream().map(Year::getValue).toList();
    }

    @Override
    @RolesAllowed({OWNER})
    public List<Integer> getForecastYearsByOwnPlaceId(Long placeId, String login) throws AppBaseException {
        if (placeFacade.findByLogin(login).stream().noneMatch((place) -> Objects.equals(place.getId(), placeId))) {
            throw new UnaccessibleReportException();
        }
        return forecastFacade.findForecastYearByPlaceId(placeId).stream().map(Year::getValue).toList();
    }
}
