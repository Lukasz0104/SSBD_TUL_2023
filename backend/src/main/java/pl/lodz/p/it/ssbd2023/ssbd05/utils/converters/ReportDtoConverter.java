package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Forecast;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ReportYearEntry;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.BuildingReportYearlyDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.BuildingReportsDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceCategoryReportMonthDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceCategoryReportYearDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceReportMonthDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceReportYearDto;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.ReportPlaceForecastMonth;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.ReportPlaceForecastYear;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.List;
import java.util.Map;

public class ReportDtoConverter {

    public static PlaceReportYearDto createPlaceReportYearDtoFromReportPlaceForecastYear(
        ReportPlaceForecastYear reportPlaceForecastYear, Integer year) {

        List<PlaceCategoryReportYearDto> placeCategoryReportYearList =
            createListOfPlaceCategoryReportYearDtoFromReportPlaceForecastYear(reportPlaceForecastYear);

        BigDecimal forecasted =
            placeCategoryReportYearList.stream().map(PlaceCategoryReportYearDto::getForecastValueSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal actual = placeCategoryReportYearList.stream().map(PlaceCategoryReportYearDto::getTotalCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal differential = forecasted.subtract(actual);

        return new PlaceReportYearDto(
            year,
            forecasted.setScale(2, RoundingMode.CEILING),
            actual.setScale(2, RoundingMode.CEILING),
            differential.setScale(2, RoundingMode.CEILING),
            placeCategoryReportYearList,
            getValue(reportPlaceForecastYear.getBalance()).setScale(2, RoundingMode.CEILING));
    }

    public static List<PlaceCategoryReportYearDto> createListOfPlaceCategoryReportYearDtoFromReportPlaceForecastYear(
        ReportPlaceForecastYear reportPlaceForecastYear) {

        return reportPlaceForecastYear.getReports().stream().map((report -> {
            BigDecimal forecastAmountSum = reportPlaceForecastYear.getForecasts().stream()
                .filter(element -> element
                    .getRate()
                    .getCategory()
                    .equals(report.getCategory()))
                .map(Forecast::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal forecastValueSum = reportPlaceForecastYear.getForecasts().stream()
                .filter(element -> element
                    .getRate()
                    .getCategory()
                    .equals(report.getCategory()))
                .map(Forecast::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal rateSum = reportPlaceForecastYear.getForecasts().stream()
                .filter(element -> element
                    .getRate()
                    .getCategory()
                    .equals(report.getCategory()))
                .map(f -> f.getRate().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            return new PlaceCategoryReportYearDto(
                report.getTotalCost().setScale(2, RoundingMode.CEILING),
                report.getTotalConsumption(),
                report.getCategory().getName(),
                report.getCategory().getRates().iterator().next().getAccountingRule(),
                forecastAmountSum,
                forecastValueSum.setScale(2, RoundingMode.CEILING),
                forecastValueSum.subtract(report.getTotalCost()).setScale(2, RoundingMode.CEILING),
                forecastAmountSum.subtract(report.getTotalConsumption()),
                rateSum.divide(BigDecimal.valueOf(12), 6, RoundingMode.CEILING)
                    .setScale(2, RoundingMode.CEILING));
        })).toList();
    }

    public static PlaceReportMonthDto createPlaceReportMonthDtoFromReportPlaceForecastMonth(
        ReportPlaceForecastMonth reportPlaceForecastMonth, Integer month, Integer year, boolean full) {

        List<PlaceCategoryReportMonthDto> dtos = createListOfPlaceCategoryReportMonthDtoFromListOfForecast(
            reportPlaceForecastMonth.getForecasts(), full);

        BigDecimal forecasted =
            dtos.stream().map(PlaceCategoryReportMonthDto::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal actual =
            dtos.stream().map(PlaceCategoryReportMonthDto::getRealValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal differential = forecasted.subtract(actual);
        boolean validMonth = reportPlaceForecastMonth.getForecasts()
            .stream()
            .noneMatch((element) -> element.getRealValue() == null);

        return new PlaceReportMonthDto(
            year,
            Month.of(month),
            forecasted.setScale(2, RoundingMode.CEILING),
            actual.setScale(2, RoundingMode.CEILING),
            differential.setScale(2, RoundingMode.CEILING),
            validMonth,
            dtos,
            getValue(reportPlaceForecastMonth.getBalance()).setScale(2, RoundingMode.CEILING)
        );
    }

    private static PlaceCategoryReportMonthDto createPlaceCategoryReportMonthDtoFromForecast(Forecast forecast) {
        return new PlaceCategoryReportMonthDto(
            forecast.getRate().getCategory().getName(),
            forecast.getRate().getAccountingRule(),
            forecast.getValue().setScale(2, RoundingMode.CEILING),
            getValue(forecast.getRealValue()).setScale(2, RoundingMode.CEILING),
            forecast.getValue().subtract(getValue(forecast.getRealValue())).setScale(2, RoundingMode.CEILING),
            forecast.getAmount(),
            forecast.getRate().getValue()
        );
    }

    private static PlaceCategoryReportMonthDto placeCategoryReportMonthDtoFromFullForecast(List<Forecast> forecasts,
                                                                                           String categoryName) {
        List<Forecast> forecastsPerCategory =
            forecasts.stream().filter((f) -> f.getRate().getCategory().getName().equals(categoryName)).toList();
        BigDecimal value = forecastsPerCategory.stream().map((element) -> getValue(element.getValue()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal realValue = forecastsPerCategory.stream().map((element) -> getValue(element.getRealValue()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal rateSum = forecastsPerCategory.stream().map(element -> element.getRate().getValue())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PlaceCategoryReportMonthDto(
            categoryName,
            forecastsPerCategory.iterator().next().getRate().getAccountingRule(),
            value.setScale(2, RoundingMode.CEILING),
            realValue.setScale(2, RoundingMode.CEILING),
            value.subtract(realValue).setScale(2, RoundingMode.CEILING),
            forecastsPerCategory.stream().map(Forecast::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add),
            rateSum.divide(BigDecimal.valueOf(forecastsPerCategory.size()), 6, RoundingMode.CEILING)
                .setScale(2, RoundingMode.CEILING));
    }

    public static List<PlaceCategoryReportMonthDto> createListOfPlaceCategoryReportMonthDtoFromListOfForecast(
        List<Forecast> forecasts, boolean full) {

        if (full) {
            List<String> categories =
                forecasts.stream().map(forecast -> forecast.getRate().getCategory().getName()).distinct().toList();
            return categories.stream().map(c -> placeCategoryReportMonthDtoFromFullForecast(forecasts, c)).toList();
        }

        return forecasts.stream()
            .map(ReportDtoConverter::createPlaceCategoryReportMonthDtoFromForecast).toList();
    }

    private static BigDecimal getValue(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value;
    }

    public static BuildingReportYearlyDto mapToBuildingReportYearlyDto(Map<String, ReportYearEntry> categories) {
        BigDecimal sumPredValue = BigDecimal.ZERO;
        BigDecimal sumRealValue = BigDecimal.ZERO;
        for (Map.Entry<String, ReportYearEntry> entry : categories.entrySet()) {
            sumPredValue = sumPredValue.add(entry.getValue().getPredValue());
            sumRealValue = sumRealValue.add(entry.getValue().getRealValue());
        }
        return new BuildingReportYearlyDto(categories.values().stream().toList(),
            sumPredValue, sumRealValue, sumPredValue.subtract(sumRealValue));
    }

    public static List<BuildingReportsDto> mapToListOfBuildingReports(Map<Integer, List<Integer>> maps) {
        return maps.entrySet().stream().map(e -> new BuildingReportsDto(e.getKey(), e.getValue())).toList();
    }
}
