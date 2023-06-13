package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb;

import jakarta.annotation.security.DenyAll;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.PlaceFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ForecastManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
@DenyAll
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(LoggerInterceptor.class)
public class ForecastSystemTaskManager {

    @Inject
    private ForecastManagerLocal forecastManager;

    @Inject
    private PlaceFacade placeFacade;

    @Inject
    private RollbackUtils rollbackUtils;

    private static final Logger LOGGER = Logger.getLogger(ForecastSystemTaskManager.class.getName());


    @Schedule(dayOfMonth = "1", month = "1")
    private void createForecasts() {
        Year year = Year.now();
        try {
            List<Place> places = placeFacade.findByActive(true);
            for (Place place : places) {
                for (Rate rate : place.getCurrentRates()) {
                    try {
                        rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
                            () -> forecastManager.createForecastsForPlaceAndRateAndYear(place.getId(), rate.getId(),
                                year), forecastManager);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE,
                            "Exception while creating forecasts for place with id: "
                                + place.getId() + ", and rate id: "
                                + rate.getId());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                "Exception while creating forecasts for year: " + year.getValue());
        }
    }

    @Schedule(minute = "10", dayOfMonth = "1")
    private void recalculateForecasts() {
        try {
            List<Place> places = placeFacade.findByActive(true);
            for (Place place : places) {
                for (Rate rate : place.getCurrentRates()) {
                    try {
                        rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
                            () -> forecastManager.recalculateForecastsForPlaceAndRate(place.getId(), rate.getId()),
                            forecastManager);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE,
                            "Exception while recalculating forecasts for place with id: "
                                + place.getId() + ", and rate id: "
                                + rate.getId());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                "Unexpected exception during recalculating forecasts, : " + LocalDateTime.now());
        }
    }
}
