package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.impl;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
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
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Report;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppInternalServerErrorException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ReportYearEntry;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.CategoryFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ForecastFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ReportFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ReportManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericManagerExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class ReportManager extends AbstractManager implements ReportManagerLocal, SessionSynchronization {

    @Inject
    private ReportFacade reportFacade;

    @Inject
    private CategoryFacade categoryFacade;

    @Inject
    private ForecastFacade forecastFacade;

    @Override
    @RolesAllowed({MANAGER, OWNER})
    public Report getReportDetails(Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed(MANAGER)
    public List<Report> getAllCommunityReports(Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed(MANAGER)
    public Report getCommunityReportByYear(Long year) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public Map<Integer, List<Integer>> getYearsAndMonthsForReports(Long id) throws AppBaseException {
        return forecastFacade
            .findYearsAndMonthsByBuildingId(id)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> IntStream.rangeClosed(1, e.getValue()).boxed().toList()
            ));
    }

    @Override
    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public Map<String, ReportYearEntry> getBuildingReportByYear(Long id, Year year) throws AppBaseException {
        Map<String, ReportYearEntry> result = new HashMap<>();
        List<Forecast> forecasts;
        List<Report> reports;

        forecasts = forecastFacade.findByBuildingIdAndYear(id, year);
        reports = reportFacade.findByBuildingIdAndYear(id, year);

        for (Forecast forecast: forecasts) {
            String cat = forecast.getRate().getCategory().getName();
            result.put(cat,
                result.getOrDefault(cat, new ReportYearEntry(forecast.getRate().getValue(),
                        forecast.getRate().getAccountingRule(), cat))
                    .addPred(forecast.getValue(), forecast.getAmount())
            );
        }

        for (Report report: reports) {
            String cat = report.getCategory().getName();
            try {
                result.put(cat,
                    result.get(cat).addReal(report.getTotalCost(), report.getTotalConsumption())
                );
            } catch (NullPointerException e) {
                throw new AppInternalServerErrorException();
            }
        }

        return result;
    }

    @Override
    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public Map<String, ReportYearEntry> getBuildingReportByYearAndMonth(Long id, Year year, Month month)
        throws AppBaseException {

        Map<String, ReportYearEntry> result = new HashMap<>();
        List<Forecast> forecasts = forecastFacade.findByBuildingIdAndYearAndMonth(id, year, month);

        for (Forecast forecast: forecasts) {
            String cat = forecast.getRate().getCategory().getName();
            BigDecimal realValue = forecast.getRealValue();
            if (realValue == null) {
                realValue = BigDecimal.ZERO;
            }
            result.put(cat,
                result.getOrDefault(cat, new ReportYearEntry(forecast.getRate().getValue(),
                        forecast.getRate().getAccountingRule(), cat))
                    .addMonth(forecast.getValue(), forecast.getAmount(), realValue)
            );
        }

        return result;
    }


}





