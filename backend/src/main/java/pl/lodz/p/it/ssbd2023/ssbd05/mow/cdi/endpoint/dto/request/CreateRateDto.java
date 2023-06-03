package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.AccountingRule;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRateDto {
    @NotNull
    @NotBlank
    private String accountingRule;

    @NotNull
    @Future
    private LocalDate effectiveDate;

    @NotNull
    @PositiveOrZero
    private BigDecimal value;

    @NotNull
    private Long categoryId;

    @AssertTrue
    public boolean isValid() {
        if (this.accountingRule == null) {
            return false;
        }

        try {
            AccountingRule ar = AccountingRule.valueOf(this.getAccountingRule());
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
