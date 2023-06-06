package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.AccountingRule;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlaceCategoryReportMonthDto {
    private String categoryName;
    private AccountingRule accountingRule;
    private BigDecimal value;
    private BigDecimal realValue;
    private BigDecimal valueDifferential;
    private BigDecimal amount;
    private BigDecimal rateValue;
}
