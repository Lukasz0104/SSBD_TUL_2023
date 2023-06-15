package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.BuildingReportYearlyDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.CommunityReportDto;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.ReportPlaceForecastMonth;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.ReportPlaceForecastYear;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Map;

@Local
public interface ReportManagerLocal extends CommonManagerInterface {

    CommunityReportDto getReportDetails(Integer year, Integer month) throws AppBaseException;

    Map<Integer, List<Integer>> getAllCommunityReportsYearsAndMonths() throws AppBaseException;

    Map<Integer, List<Integer>> getYearsAndMonthsForReports(Long id) throws AppBaseException;

    BuildingReportYearlyDto getYearlyReportForBuilding(Long id, Year year)
        throws AppBaseException;

    BuildingReportYearlyDto getMonthlyReportForBuilding(Long id, Year year, Month month)
        throws AppBaseException;

    CommunityReportDto getCommunityReportByYear(Integer year) throws AppBaseException;

    ReportPlaceForecastYear getAllOwnReportsDataByPlaceAndYear(Long placeId, Year year, String login)
        throws AppBaseException;

    ReportPlaceForecastYear getAllReportsDataByPlaceAndYear(Long placeId, Year year) throws AppBaseException;

    ReportPlaceForecastMonth getAllOwnReportsDataByPlaceAndYearAndMonth(Long placeId, Year year, Month month,
                                                                        String login, boolean full)
        throws AppBaseException;

    ReportPlaceForecastMonth getAllReportsDataByPlaceAndYearAndMonth(Long placeId, Year year, Month month, boolean full)
        throws AppBaseException;

    boolean isReportForYear(Year year, Long placeId);

    boolean isOwnReportForYear(Year year, Long placeId, String login) throws AppBaseException;

    void createReportForPlace(Long placeId) throws AppBaseException;
}
