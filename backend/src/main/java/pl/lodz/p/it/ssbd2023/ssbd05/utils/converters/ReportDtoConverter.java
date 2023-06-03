package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Forecast;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.ForecastNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.ReportNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceCategoryReportMonthDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceCategoryReportYearDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceReportMonthDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceReportYearDto;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.ReportForecastYear;

import java.math.BigDecimal;
import java.util.List;

public class ReportDtoConverter {

    public static PlaceReportYearDto createPlaceReportYearDtoFromListOfReportForecastYear(
        List<ReportForecastYear> reportForecastYear) throws AppBaseException {
        return new PlaceReportYearDto(
            reportForecastYear.stream().findFirst().orElseThrow(ReportNotFoundException::new).getYear(),
            reportForecastYear.stream().map(ReportForecastYear::getForecastValueSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add),
            reportForecastYear.stream().map(ReportForecastYear::getTotalCost).reduce(BigDecimal.ZERO, BigDecimal::add),
            createListOfPlaceCategoryReportYearDtoFromListOfReportForecastYear(reportForecastYear));
    }

    public static PlaceCategoryReportYearDto createPlaceCategoryReportYearDtoFromReportForecastYear(
        ReportForecastYear reportForecastYear) {
        return new PlaceCategoryReportYearDto(
            reportForecastYear.getTotalCost(),
            reportForecastYear.getTotalConsumption(),
            reportForecastYear.getCategoryName(),
            reportForecastYear.getAccountingRule(),
            reportForecastYear.getForecastAmountSum(),
            reportForecastYear.getForecastValueSum()
        );
    }

    public static List<PlaceCategoryReportYearDto> createListOfPlaceCategoryReportYearDtoFromListOfReportForecastYear(
        List<ReportForecastYear> reportForecastYear) {
        return reportForecastYear.stream()
            .map(ReportDtoConverter::createPlaceCategoryReportYearDtoFromReportForecastYear).toList();
    }

    public static PlaceReportMonthDto createPlaceReportMonthDtoFromListOfForecast(
        List<Forecast> forecasts) throws AppBaseException {
        return new PlaceReportMonthDto(
            forecasts.stream().findFirst().orElseThrow(ForecastNotFoundException::new).getYear().getValue(),
            forecasts.stream().findFirst().orElseThrow(ForecastNotFoundException::new).getMonth(),
            forecasts.stream().map(Forecast::getValue).reduce(BigDecimal.ZERO, BigDecimal::add),
            createListOfPlaceCategoryReportMonthDtoFromListOfForecast(forecasts)
        );
    }

    public static PlaceCategoryReportMonthDto createPlaceCategoryReportMonthDtoFromForecast(Forecast forecast) {
        return new PlaceCategoryReportMonthDto(
            forecast.getRate().getCategory().getName(),
            forecast.getRate().getAccountingRule(),
            forecast.getValue(),
            forecast.getRealValue(),
            forecast.getAmount(),
            forecast.getRate().getValue()
        );
    }

    public static List<PlaceCategoryReportMonthDto> createListOfPlaceCategoryReportMonthDtoFromListOfForecast(
        List<Forecast> forecasts) {
        return forecasts.stream()
            .map(ReportDtoConverter::createPlaceCategoryReportMonthDtoFromForecast).toList();
    }
}
