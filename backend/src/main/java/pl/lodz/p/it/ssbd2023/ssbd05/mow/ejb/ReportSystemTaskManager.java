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
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.PlaceFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ReportManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
@DenyAll
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(LoggerInterceptor.class)
public class ReportSystemTaskManager {

    private static final Logger LOGGER = Logger.getLogger(ReportSystemTaskManager.class.getName());

    @Inject
    private ReportManagerLocal reportManager;

    @Inject
    private PlaceFacade placeFacade;

    @Inject
    private RollbackUtils rollbackUtils;

    @Schedule(dayOfMonth = "1", month = "1", minute = "15")
    private void createReports() {
        List<Place> places;
        try {
            places = placeFacade.findByActive(true);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while retrieving active places before generating reports: ", e);
            return;
        }

        if (places == null || places.isEmpty()) {
            return;
        }

        for (var place : places) {
            try {
                rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
                    () -> reportManager.createReportForPlace(place.getId()),
                    reportManager);
            } catch (AppBaseException e) {
                LOGGER.log(Level.SEVERE,
                    "Exception while generating report for place with id=%d".formatted(place.getId()), e);
            }
        }
    }
}
