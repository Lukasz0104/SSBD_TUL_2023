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
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.MeterFacade;
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
    private MeterFacade meterFacade;

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
    public void createForecastsForPlaceAndRateAndYear(Long placeId, Long rateId, Year year) throws AppBaseException {
        Place place = placeFacade.find(placeId)
            .orElseThrow(PlaceNotFoundException::new);
        Rate rate = rateFacade.find(rateId)
            .orElseThrow(RateNotFoundException::new);

        if (rate.getAccountingRule().equals(AccountingRule.METER)) {
            Meter meter = meterFacade.findByCategoryIdAndPlaceId(rate.getCategory().getId(), placeId)
                .orElseThrow(MeterNotFoundException::new);
            forecastUtils.createMeterForecastsForYear(meter, rate, year);
        } else {
            forecastUtils.createOtherForecastsForYear(place, rate, year);
        }
    }

    @Override
    @PermitAll
    public void recalculateForecastsForPlaceAndRate(Long placeId, Long rateId) throws AppBaseException {
        Place place = placeFacade.find(placeId)
            .orElseThrow(PlaceNotFoundException::new);
        Rate rate = rateFacade.find(rateId)
            .orElseThrow(RateNotFoundException::new);

        Rate currentRate = rateFacade.findCurrentRateByCategoryId(rate.getCategory().getId())
            .orElseThrow(RateNotFoundException::new);

        if (!rate.getId().equals(currentRate.getId())) {
            place.getCurrentRates().remove(rate);
            place.getCurrentRates().add(currentRate);
            placeFacade.edit(place);
        }

        if (currentRate.getAccountingRule().equals(AccountingRule.METER)) {
            Meter meter = meterFacade.findByCategoryIdAndPlaceId(currentRate.getCategory().getId(), placeId)
                .orElseThrow(MeterNotFoundException::new);
            forecastUtils.calculateForecastsForMeter(meter, currentRate, true);
        } else {
            forecastUtils.calculateForecasts(place, currentRate, true);
        }
    }

    @Override
    @RolesAllowed(MANAGER)
    public List<Integer> getForecastYearsByPlaceId(Long placeId) {
        return forecastFacade.findForecastYearByPlaceId(placeId).stream().map(Year::getValue).toList();
    }

    @Override
    @RolesAllowed(OWNER)
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
