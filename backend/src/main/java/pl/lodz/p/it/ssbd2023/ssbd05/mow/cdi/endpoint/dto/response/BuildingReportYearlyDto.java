package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ReportYearEntry;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuildingReportYearlyDto {

    @NotNull
    private Map<String, ReportYearEntry> categories;

    @PositiveOrZero
    private BigDecimal sumPredValue;

    @PositiveOrZero
    private BigDecimal sumRealValue;

    @NotNull
    private BigDecimal diff;
}
