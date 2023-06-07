package pl.lodz.p.it.ssbd2023.ssbd05.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.AccountingRule;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Forecast;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Report;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportForecastYear {
    private Integer year;
    private BigDecimal totalCost;
    private BigDecimal totalConsumption;
    private String categoryName;
    private AccountingRule accountingRule;
    private BigDecimal forecastAmountSum;
    private BigDecimal forecastValueSum;

    public static List<ReportForecastYear> convertReportAndForecastListToReportForecastYearList(
        List<Report> reports,
        List<Forecast> forecasts) {
        return reports.stream().map(e -> new ReportForecastYear(
                e.getYear().getValue(),
                e.getTotalCost(),
                e.getTotalConsumption(),
                e.getCategory().getName(),
                e.getCategory().getRates().iterator().next().getAccountingRule(),
                forecasts.stream().filter(element -> element.getRate().getCategory().equals(e.getCategory()))
                    .map(Forecast::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add),
                forecasts.stream().filter(element -> element.getRate().getCategory().equals(e.getCategory()))
                    .map(Forecast::getValue).reduce(BigDecimal.ZERO, BigDecimal::add)))
            .toList();
    }
}
