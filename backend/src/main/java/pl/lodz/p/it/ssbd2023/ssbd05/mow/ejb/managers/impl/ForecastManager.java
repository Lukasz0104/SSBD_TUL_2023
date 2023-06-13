package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.impl;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.AccountingRule;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Forecast;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Meter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.AmountRequiredException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.CategoryNotInUseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.InactivePlaceException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.IllegalSelfActionException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.InaccessibleReportException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.ForecastNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.MeterNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.PlaceNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.RateNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ForecastFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.PlaceFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.RateFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ForecastManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.ForecastUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Inject
    private RateFacade rateFacade;

    @Inject
    private ForecastUtils forecastUtils;

    @Override
    @RolesAllowed(MANAGER)
    public void createCurrentForecast(Long placeId, Long categoryId, BigDecimal amount, String login)
        throws AppBaseException {
        Place place = placeFacade.find(placeId).orElseThrow(PlaceNotFoundException::new);
        if (place.getOwners().stream().anyMatch((owner) -> owner.getAccount().getLogin().equals(login))) {
            throw new IllegalSelfActionException();
        }
        if (!place.isActive()) {
            throw new InactivePlaceException();
        }
        if (place.getCurrentRates().stream().noneMatch((p) -> p.getCategory().getId().equals(categoryId))) {
            throw new CategoryNotInUseException();
        }
        Rate rate = rateFacade.findCurrentRateByCategoryId(categoryId)
            .orElseThrow(RateNotFoundException::new);
        switch (rate.getAccountingRule()) {
            case PERSON -> amount = BigDecimal.valueOf(place.getResidentsNumber());
            case SURFACE -> amount = place.getSquareFootage();
            case METER -> {
                if (amount == null) {
                    throw new AmountRequiredException();
                }
            }
            default -> amount = BigDecimal.ONE;
        }
        LocalDate now = LocalDate.now();
        Forecast forecast = new Forecast(
            Year.now(),
            now.getMonth(),
            amount.multiply(rate.getValue()),
            amount,
            place,
            rate);
        forecastFacade.create(forecast);
    }

    @Override
    @PermitAll
    public void createForecastsForPlaceAndRateAndYear(Place place, Rate rate, Year year) throws AppBaseException {
        // Pozyskanie aktualnych encji w granicach transakcji
        Place p = placeFacade.find(place.getId())
            .orElseThrow(PlaceNotFoundException::new);
        Rate r = rateFacade.find(rate.getId())
            .orElseThrow(RateNotFoundException::new);
        if (rate.getAccountingRule().equals(AccountingRule.METER)) {
            Meter meter = p.getMeters().stream()
                .filter(Meter::isActive)
                .filter(m -> m.getCategory().equals(r.getCategory()))
                .findFirst()
                .orElseThrow(MeterNotFoundException::new);
            forecastUtils.createMeterForecastsForYear(meter, r, year);
        } else {
            forecastUtils.createOtherForecastsForYear(p, r, year);
        }
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
            throw new InaccessibleReportException();
        }
        return forecastFacade.findForecastYearByPlaceId(placeId).stream().map(Year::getValue).toList();
    }

    @Override
    @RolesAllowed(OWNER)
    public Integer getOwnMinMonthFromForecast(Long placeId, Year year, String login) throws AppBaseException {
        if (placeFacade.findByLogin(login).stream().noneMatch((place) -> Objects.equals(place.getId(), placeId))) {
            throw new InaccessibleReportException();
        }
        return forecastFacade.findMinMonthByPlaceIdAndYear(placeId, year)
            .orElseThrow(ForecastNotFoundException::new).getValue();

    }

    @Override
    @RolesAllowed(MANAGER)
    public Integer getMinMonthFromForecast(Long placeId, Year year) throws AppBaseException {
        return forecastFacade.findMinMonthByPlaceIdAndYear(placeId, year)
            .orElseThrow(ForecastNotFoundException::new).getValue();
    }
}
