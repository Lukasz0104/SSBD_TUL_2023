package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.AccountingRule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Data
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

    public void setValue(BigDecimal value) {
        this.value = value;
        this.value = this.value.setScale(2, RoundingMode.DOWN);
    }

    public CreateRateDto(String accountingRule, LocalDate effectiveDate, BigDecimal value, Long categoryId) {
        this.accountingRule = accountingRule;
        this.effectiveDate = effectiveDate;
        this.value = value;
        this.value = this.value.setScale(2, RoundingMode.DOWN);
        this.categoryId = categoryId;
    }

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
