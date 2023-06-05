package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.impl;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;
import static pl.lodz.p.it.ssbd2023.ssbd05.utils.ReportForecastYear.convertReportAndForecastListToReportForecastYearList;

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
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.UnaccessibleReportException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ForecastFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.PlaceFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ReportFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ReportManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.ReportForecastYear;

import java.time.Month;
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
public class ReportManager extends AbstractManager implements ReportManagerLocal, SessionSynchronization {

    @Inject
    private ReportFacade reportFacade;

    @Inject
    private PlaceFacade placeFacade;

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
    @RolesAllowed(MANAGER)
    public List<ReportForecastYear> getAllReportsDataByPlaceAndYear(Long placeId, Year year) throws AppBaseException {
        return convertReportAndForecastListToReportForecastYearList(
            reportFacade.findByPlaceIdAndYear(placeId, year),
            forecastFacade.findByPlaceIdAndYear(placeId, year));

    }

    @Override
    @RolesAllowed(OWNER)
    public List<ReportForecastYear> getAllOwnReportsDataByPlaceAndYear(Long placeId, Year year, String login)
        throws AppBaseException {
        checkUserPlace(placeId, login);
        return convertReportAndForecastListToReportForecastYearList(
            reportFacade.findByPlaceIdAndYear(placeId, year),
            forecastFacade.findByPlaceIdAndYear(placeId, year));
    }

    @Override
    @RolesAllowed(MANAGER)
    public List<Forecast> getAllReportsDataByPlaceAndYearAndMonth(Long placeId, Year year, Month month) {
        return forecastFacade.findByPlaceIdAndYearAndMonth(placeId, year, month);
    }

    @Override
    @RolesAllowed(OWNER)
    public List<Forecast> getAllOwnReportsDataByPlaceAndYearAndMonth(Long placeId, Year year, Month month, String login)
        throws AppBaseException {
        checkUserPlace(placeId, login);
        return forecastFacade.findByPlaceIdAndYearAndMonth(placeId, year, month);
    }

    @Override
    @RolesAllowed({MANAGER})
    public boolean isReportForYear(Year year, Long placeId) {
        return reportFacade.findReportYearsByPlaceId(placeId).stream()
            .anyMatch((e) -> e.getValue() == year.getValue());
    }

    @Override
    @RolesAllowed({OWNER})
    public boolean isOwnReportForYear(Year year, Long placeId, String login) throws AppBaseException {
        checkUserPlace(placeId, login);
        return reportFacade.findReportYearsByPlaceId(placeId).stream()
            .anyMatch((e) -> e.getValue() == year.getValue());
    }

    @RolesAllowed({OWNER, MANAGER})
    private void checkUserPlace(Long placeId, String login) throws AppBaseException {
        if (placeFacade.findByLogin(login).stream().noneMatch((place) -> Objects.equals(place.getId(), placeId))) {
            throw new UnaccessibleReportException();
        }
    }
}
