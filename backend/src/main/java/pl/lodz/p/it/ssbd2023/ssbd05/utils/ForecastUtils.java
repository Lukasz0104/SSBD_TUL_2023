package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Forecast;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Meter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Reading;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ForecastFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.RateFacade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors(LoggerInterceptor.class)
public class ForecastUtils {

    @Inject
    private AppProperties appProperties;

    @Inject
    private ForecastFacade forecastFacade;

    @Inject
    private RateFacade rateFacade;

    public void calculateForecastsForMeter(Meter meter, Rate rate, boolean includeCurrentMonth)
        throws AppBaseException {
        List<Reading> firstAndLastReading = findFirstAndLastReading(meter);
        Reading firstReading = firstAndLastReading.get(0);
        Reading lastReading = firstAndLastReading.get(1);

        BigDecimal averageDailyConsumption = calculateAverageDailyConsumption(firstReading, lastReading);

        List<Forecast> forecasts;
        if (includeCurrentMonth) {
            forecasts = forecastFacade.findFutureAndCurrentByPlaceIdAndCategoryAndYear(
                meter.getPlace().getId(),
                meter.getCategory().getId(),
                Year.now(),
                LocalDateTime.now().getMonth());
        } else {
            forecasts = forecastFacade.findFutureByPlaceIdAndCategoryAndYear(
                meter.getPlace().getId(),
                meter.getCategory().getId(),
                Year.now(),
                LocalDateTime.now().getMonth());
        }

        boolean leapYear = Year.now().isLeap();
        if (forecasts.size() > 0) {
            for (Forecast forecast : forecasts) {
                BigDecimal newAmount =
                    averageDailyConsumption.multiply(
                        BigDecimal.valueOf(forecast.getMonth().length(leapYear))
                            .setScale(3, RoundingMode.CEILING));
                forecast.setAmount(newAmount);
                forecast.setRate(rate);
                forecast.setValue(newAmount.multiply(forecast.getRate().getValue()));
                forecastFacade.edit(forecast);
            }
        } else {
            int monthCounter = LocalDateTime.now().getMonthValue();
            if (!includeCurrentMonth) {
                monthCounter++;
            }
            for (int i = monthCounter; i < 13; i++) {
                BigDecimal newAmount =
                    averageDailyConsumption.multiply(BigDecimal.valueOf(Month.of(i).length(leapYear)))
                        .setScale(3, RoundingMode.CEILING);
                forecastFacade.create(
                    new Forecast(Year.now(), Month.of(i), newAmount.multiply(rate.getValue()),
                        newAmount,
                        meter.getPlace(),
                        rate));
            }
        }
    }

    public void calculateForecasts(Place place, Rate rate, boolean includeCurrentMonth) throws AppBaseException {
        BigDecimal amount = findAmountByPlaceAndRate(place, rate);

        List<Forecast> forecasts;
        if (includeCurrentMonth) {
            forecasts = forecastFacade.findFutureAndCurrentByPlaceIdAndCategoryAndYear(
                place.getId(),
                rate.getCategory().getId(),
                Year.now(),
                LocalDateTime.now().getMonth());
        } else {
            forecasts = forecastFacade.findFutureByPlaceIdAndCategoryAndYear(
                place.getId(),
                rate.getCategory().getId(),
                Year.now(),
                LocalDateTime.now().getMonth());
        }

        if (forecasts.size() > 0) {
            for (Forecast forecast : forecasts) {
                forecast.setAmount(amount);
                forecast.setValue(amount.multiply(rate.getValue()));
                forecast.setRate(rate);
                forecastFacade.edit(forecast);
            }
        } else {
            int monthCounter = LocalDateTime.now().getMonthValue();
            if (!includeCurrentMonth) {
                monthCounter++;
            }
            for (int i = monthCounter; i < 13; i++) {
                forecastFacade.create(
                    new Forecast(Year.now(), Month.of(i), amount.multiply(rate.getValue()),
                        amount,
                        place,
                        rate));
            }
        }
    }

    public void createMeterForecastsForYear(Meter meter, Rate rate, Year year) throws AppBaseException {
        List<Reading> firstAndLastReading = findFirstAndLastReading(meter);
        Reading firstReading = firstAndLastReading.get(0);
        Reading lastReading = firstAndLastReading.get(1);

        BigDecimal averageDailyConsumption = calculateAverageDailyConsumption(firstReading, lastReading);

        for (int i = 1; i < 13; i++) {
            BigDecimal newAmount =
                averageDailyConsumption.multiply(BigDecimal.valueOf(Month.of(i).length(year.isLeap())))
                    .setScale(3, RoundingMode.CEILING);
            forecastFacade.create(
                new Forecast(year, Month.of(i), newAmount.multiply(rate.getValue()),
                    newAmount,
                    meter.getPlace(),
                    rate));
        }
    }

    public void createOtherForecastsForYear(Place place, Rate rate, Year year) throws AppBaseException {
        BigDecimal amount = findAmountByPlaceAndRate(place, rate);
        for (int i = 1; i < 13; i++) {
            forecastFacade.create(
                new Forecast(year, Month.of(i), amount.multiply(rate.getValue()),
                    amount,
                    place,
                    rate));
        }
    }

    private BigDecimal findAmountByPlaceAndRate(Place place, Rate rate) {
        return switch (rate.getAccountingRule()) {
            case PERSON -> BigDecimal.valueOf(place.getResidentsNumber());
            case UNIT -> BigDecimal.ONE;
            case SURFACE -> place.getSquareFootage();
            default -> BigDecimal.ZERO;
        };
    }

    private List<Reading> findFirstAndLastReading(Meter meter) {
        LocalDateTime now = LocalDateTime.now();
        List<Reading> pastReliableReadings = meter.getPastReliableReadings(now);

        List<Reading> reliableReadingsFromConsideredMonths = pastReliableReadings.stream()
            .filter(
                r -> r.getDate().isAfter(now.minusMonths(appProperties.getNumberOfMonthsForReadingConsideration())))
            .sorted(Comparator.comparing(Reading::getDate).reversed())
            .toList();

        List<Reading> reliableReadingsFromBeforeConsideredMonths = pastReliableReadings.stream()
            .filter(
                r -> r.getDate()
                    .isBefore(now.minusMonths(appProperties.getNumberOfMonthsForReadingConsideration())))
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
            firstReading =
                reliableReadingsFromConsideredMonths.get(reliableReadingsFromConsideredMonths.size() - 1);
        } else if (reliableReadingsFromBeforeConsideredMonths.size() > 0) {
            firstReading =
                reliableReadingsFromBeforeConsideredMonths.get(
                    reliableReadingsFromBeforeConsideredMonths.size() - 1);
        } else {
            firstReading = lastReading;
        }
        List<Reading> readings = new ArrayList<>();
        readings.add(firstReading);
        readings.add(lastReading);

        return readings;
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
                        6,
                        RoundingMode.CEILING);
        }
        return averageDailyConsumption;
    }
}
