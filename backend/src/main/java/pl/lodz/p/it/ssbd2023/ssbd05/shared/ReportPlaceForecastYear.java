package pl.lodz.p.it.ssbd2023.ssbd05.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Forecast;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Report;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportPlaceForecastYear {
    private List<Report> reports;
    private List<Forecast> forecasts;
    private BigDecimal balance;
}
