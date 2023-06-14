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
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Meter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Reading;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.InactiveMeterException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.NotEnoughDaysAfterInitialReadingException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.NotEnoughDaysBetweenReliableReadingsException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.ReadingDateBeforeInitialReadingException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.ReadingValueHigherThanFutureException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.ReadingValueSmallerThanInitialException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.ReadingValueSmallerThanPreviousException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.OwnPlaceReadingAttemptException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.MeterNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.RateNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.MeterFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.RateFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ReadingFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ReadingManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.AppProperties;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.ForecastUtils;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericManagerExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class ReadingManager extends AbstractManager implements ReadingManagerLocal, SessionSynchronization {

    @Inject
    private AppProperties appProperties;

    @Inject
    private ReadingFacade readingFacade;

    @Inject
    private MeterFacade meterFacade;

    @Inject
    private RateFacade rateFacade;

    @Inject
    private ForecastUtils forecastUtils;

    @Override
    @RolesAllowed({OWNER})
    public void createReadingAsOwner(Reading reading, Long meterId, String login) throws AppBaseException {
        Meter meter = meterFacade.findByIdAndOwnerLogin(meterId, login).orElseThrow(MeterNotFoundException::new);
        if (!meter.isActive()) {
            throw new InactiveMeterException();
        }

        List<Reading> pastReliableReadings = meter.getPastReliableReadings(reading.getDate());
        List<Reading> futureReliableReadings = meter.getFutureReliableReadings(reading.getDate());

        if (pastReliableReadings.size() > 0) {
            Reading lastReliableReading = pastReliableReadings.get(0);
            if (lastReliableReading.getDate().until(reading.getDate(), ChronoUnit.DAYS)
                < appProperties.getDaysBetweenReadingsForOwner()) {
                throw new NotEnoughDaysBetweenReliableReadingsException();
            } else if (reading.getValue().compareTo(lastReliableReading.getValue()) < 0) {
                throw new ReadingValueSmallerThanPreviousException();
            }
        }

        for (Reading futureReading : futureReliableReadings) {
            if (reading.getValue().compareTo(futureReading.getValue()) > 0) {
                throw new ReadingValueHigherThanFutureException();
            } else if (reading.getDate().until(futureReading.getDate(), ChronoUnit.DAYS) == 0) {
                throw new NotEnoughDaysBetweenReliableReadingsException();
            }
        }

        reading.setMeter(meter);
        meter.getReadings().add(reading);

        readingFacade.create(reading);
        meterFacade.edit(meter);

        Rate rate = rateFacade.findCurrentRateByCategoryId(meter.getCategory().getId())
            .orElseThrow(RateNotFoundException::new);
        forecastUtils.calculateForecastsForMeter(meter, rate, false);
    }

    @Override
    @RolesAllowed({MANAGER})
    public void createReadingAsManager(Reading reading, Long meterId, String login) throws AppBaseException {
        Meter meter = meterFacade.find(meterId).orElseThrow(MeterNotFoundException::new);
        if (!meter.isActive()) {
            throw new InactiveMeterException();
        }
        if (meter.getPlace().getOwners().stream()
            .filter(OwnerData::isActive)
            .anyMatch(ownerData -> ownerData.getAccount().getLogin().equals(login))) {
            throw new OwnPlaceReadingAttemptException();
        }

        List<Reading> pastReliableReadings = meter.getPastReliableReadings(reading.getDate());
        if (pastReliableReadings.size() == 0) {
            throw new ReadingDateBeforeInitialReadingException();
        }
        if (pastReliableReadings.get(pastReliableReadings.size() - 1).getDate()
            .until(reading.getDate(), ChronoUnit.DAYS) < 1) {
            throw new NotEnoughDaysAfterInitialReadingException();
        }
        if (reading.getValue().compareTo(pastReliableReadings.get(pastReliableReadings.size() - 1).getValue()) < 0) {
            throw new ReadingValueSmallerThanInitialException();
        }

        pastReliableReadings.stream()
            .filter(r ->
                r.getValue().compareTo(reading.getValue()) > 0
                    || r.getDate().until(reading.getDate(), ChronoUnit.DAYS) < 1)
            .forEach(r -> r.setReliable(false));


        List<Reading> futureReliableReadings = meter.getFutureReliableReadings(reading.getDate());
        futureReliableReadings.stream()
            .filter(r ->
                r.getValue().compareTo(reading.getValue()) < 0
                    || reading.getDate().until(r.getDate(), ChronoUnit.DAYS) < 1)
            .forEach(r -> r.setReliable(false));

        reading.setMeter(meter);
        meter.getReadings().add(reading);

        readingFacade.create(reading);
        meterFacade.edit(meter);

        Rate rate = rateFacade.findCurrentRateByCategoryId(meter.getCategory().getId())
            .orElseThrow(RateNotFoundException::new);
        forecastUtils.calculateForecastsForMeter(meter, rate, false);
    }
}
