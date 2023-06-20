package pl.lodz.p.it.ssbd2023.ssbd05.mow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Forecast;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportPlaceForecastMonth {
    private List<Forecast> forecasts;
    private BigDecimal balance;
}
