package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Forecast;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Report;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.ReportForecastYear;

import java.time.Month;
import java.time.Year;
import java.util.List;

@Local
public interface ReportManagerLocal extends CommonManagerInterface {
    Report getReportDetails(Long id) throws AppBaseException;

    List<Report> getAllCommunityReports(Long id) throws AppBaseException;

    Report getCommunityReportByYear(Long year) throws AppBaseException;

    List<ReportForecastYear> getAllOwnReportsDataByPlaceAndYear(Long placeId, Year year, String login)
        throws AppBaseException;

    List<ReportForecastYear> getAllReportsDataByPlaceAndYear(Long placeId, Year year) throws AppBaseException;

    List<Forecast> getAllOwnReportsDataByPlaceAndYearAndMonth(Long placeId, Year year, Month month, String login)
        throws AppBaseException;

    List<Forecast> getAllReportsDataByPlaceAndYearAndMonth(Long placeId, Year year, Month month);

    boolean isReportForYear(Year year, Long placeId);

    boolean isOwnReportForYear(Year year, Long placeId, String login) throws AppBaseException;
}
