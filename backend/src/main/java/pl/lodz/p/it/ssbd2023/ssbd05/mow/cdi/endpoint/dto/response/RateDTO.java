package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateDTO {
    @NotNull
    private BigDecimal value;
    @NotNull
    private LocalDate effectiveDate;
    @NotNull
    private String accountingRule;
}
