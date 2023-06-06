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
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Forecast;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Meter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Reading;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.NotEnoughDaysBetweenReliableReadingsException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.ReadingValueHigherThanFutureException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.ReadingValueSmallerThanPreviousException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.MeterNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ForecastFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.MeterFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ReadingFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ReadingManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.AppProperties;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
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
    private ForecastFacade forecastFacade;

    @Override
    @RolesAllowed({OWNER})
    public void createReadingAsOwner(Reading reading, Long meterId, String login) throws AppBaseException {
        Meter meter = meterFacade.findByIdAndOwnerLogin(meterId, login).orElseThrow(MeterNotFoundException::new);

        List<Reading> pastReliableReadings = meter.getPastReliableReadings();
        List<Reading> futureReliableReadings = meter.getFutureReliableReadings();

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
        meterFacade.lockAndEdit(meter);
        calculateForecasts(meter);
    }

    @Override
    @RolesAllowed({MANAGER})
    public void createReadingAsManager(Reading reading, Long meterId) throws AppBaseException {
        Meter meter = meterFacade.find(meterId).orElseThrow(MeterNotFoundException::new);

        List<Reading> pastReliableReadings = meter.getPastReliableReadings();
        List<Reading> futureReliableReadings = meter.getFutureReliableReadings();

        pastReliableReadings.stream()
            .filter(r ->
                r.getValue().compareTo(reading.getValue()) >= 0
                    || r.getDate().until(reading.getDate(), ChronoUnit.DAYS) < 0)
            .forEach(r -> r.setReliable(false));

        futureReliableReadings.stream()
            .filter(r -> r.getValue().compareTo(reading.getValue()) < 0)
            .forEach(r -> r.setReliable(false));

        reading.setMeter(meter);
        meter.getReadings().add(reading);

        readingFacade.create(reading);
        meterFacade.lockAndEdit(meter);
        calculateForecasts(meter);
    }

    private void calculateForecasts(Meter meter)
        throws AppBaseException {
        LocalDateTime now = LocalDateTime.now();
        List<Reading> pastReliableReadings = meter.getPastReliableReadings();

        List<Reading> reliableReadingsFromConsideredMonths = pastReliableReadings.stream()
            .filter(
                r -> r.getDate().isAfter(now.minusMonths(appProperties.getNumberOfMonthsForReadingConsideration())))
            .sorted(Comparator.comparing(Reading::getDate).reversed())
            .toList();

        List<Reading> reliableReadingsFromBeforeConsideredMonths = pastReliableReadings.stream()
            .filter(
                r -> r.getDate().isBefore(now.minusMonths(appProperties.getNumberOfMonthsForReadingConsideration())))
            .sorted(Comparator.comparing(Reading::getDate).reversed())
            .toList();

        Reading lastReading;
        if (reliableReadingsFromConsideredMonths.size() > 0) {
            lastReading = reliableReadingsFromConsideredMonths.get(0);
        } else {
            // W najgorszym wypadku(brak jakichkolwiek innych odczytów) odczyt zerowy (tworzony razem z licznikiem)
            lastReading = reliableReadingsFromBeforeConsideredMonths.get(0);
        }

        Reading firstReading;
        if (reliableReadingsFromConsideredMonths.size() > 1) {
            firstReading = reliableReadingsFromConsideredMonths.get(reliableReadingsFromConsideredMonths.size() - 1);
        } else {
            // Zawsze odczyt zerowy
            firstReading =
                reliableReadingsFromBeforeConsideredMonths.get(reliableReadingsFromBeforeConsideredMonths.size() - 1);
        }

        BigDecimal averageDailyConsumption = calculateAverageDailyConsumption(firstReading, lastReading);

        List<Forecast> forecasts = forecastFacade.findFutureByPlaceIdAndCategoryAndYear(
            meter.getPlace().getId(),
            meter.getCategory().getId(),
            Year.now(),
            LocalDateTime.now().getMonth());

        boolean leapYear = Year.now().isLeap();
        for (Forecast forecast : forecasts) {
            BigDecimal newAmount =
                averageDailyConsumption.multiply(BigDecimal.valueOf(forecast.getMonth().length(leapYear)));
            forecast.setAmount(newAmount);
            forecast.setValue(newAmount.multiply(forecast.getRate().getValue()));
            forecastFacade.edit(forecast);
        }
    }

    private BigDecimal calculateAverageDailyConsumption(Reading firstReading, Reading lastReading) {
        BigDecimal averageDailyConsumption;
        if (firstReading == lastReading) {
            averageDailyConsumption = BigDecimal.ZERO;
        } else {
            long days = firstReading.getDate().until(lastReading.getDate(), ChronoUnit.DAYS);
            averageDailyConsumption =
                lastReading.getValue()
                    .subtract(firstReading.getValue())
                    .divide(BigDecimal.valueOf(days > 0 ? days : 1),
                        3,
                        RoundingMode.CEILING);
        }
        return averageDailyConsumption;
    }
}
