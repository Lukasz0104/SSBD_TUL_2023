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
        BigDecimal forecasted = reportForecastYear.stream().map(ReportForecastYear::getForecastValueSum)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal actual = reportForecastYear.stream().map(ReportForecastYear::getTotalCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal differential = forecasted.subtract(actual);

        return new PlaceReportYearDto(
            reportForecastYear.stream().findFirst().orElseThrow(ReportNotFoundException::new).getYear(),
            forecasted,
            actual,
            differential,
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
            reportForecastYear.getForecastValueSum(),
            reportForecastYear.getForecastValueSum().subtract(reportForecastYear.getTotalCost()),
            reportForecastYear.getForecastAmountSum().subtract(reportForecastYear.getTotalConsumption())
        );
    }

    public static List<PlaceCategoryReportYearDto> createListOfPlaceCategoryReportYearDtoFromListOfReportForecastYear(
        List<ReportForecastYear> reportForecastYear) {
        return reportForecastYear.stream()
            .map(ReportDtoConverter::createPlaceCategoryReportYearDtoFromReportForecastYear).toList();
    }

    public static PlaceReportMonthDto createPlaceReportMonthDtoFromListOfForecast(
        List<Forecast> forecasts) throws AppBaseException {

        BigDecimal forecasted =
            forecasts.stream().map((element) -> getValue(element.getValue())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal actual = forecasts.stream().map((element) -> getValue(element.getRealValue()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal differential = forecasted.subtract(actual);
        boolean validMonth = forecasts.stream().noneMatch((element) -> element.getRealValue() == null);

        return new PlaceReportMonthDto(
            forecasts.stream().findFirst().orElseThrow(ForecastNotFoundException::new).getYear().getValue(),
            forecasts.stream().findFirst().orElseThrow(ForecastNotFoundException::new).getMonth(),
            forecasted,
            actual,
            differential,
            validMonth,
            createListOfPlaceCategoryReportMonthDtoFromListOfForecast(forecasts)
        );
    }

    public static PlaceCategoryReportMonthDto createPlaceCategoryReportMonthDtoFromForecast(Forecast forecast) {
        return new PlaceCategoryReportMonthDto(
            forecast.getRate().getCategory().getName(),
            forecast.getRate().getAccountingRule(),
            forecast.getValue(),
            getValue(forecast.getRealValue()),
            forecast.getValue().subtract(getValue(forecast.getRealValue())),
            forecast.getAmount(),
            forecast.getRate().getValue()
        );
    }

    public static List<PlaceCategoryReportMonthDto> createListOfPlaceCategoryReportMonthDtoFromListOfForecast(
        List<Forecast> forecasts) {
        return forecasts.stream()
            .map(ReportDtoConverter::createPlaceCategoryReportMonthDtoFromForecast).toList();
    }

    public static BigDecimal getValue(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value;
    }
}
