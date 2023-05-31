package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.ControlledEntityDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateDTO extends ControlledEntityDTO {

    @NotNull
    private Long id;
    @NotNull
    private BigDecimal value;
    @NotNull
    private LocalDate effectiveDate;
    @NotNull
    private String accountingRule;

    public RateDTO(Long id, LocalDateTime createdTime, String createdBy, LocalDateTime updatedTime,
                   String updatedBy, BigDecimal value, LocalDate effectiveDate, String accountingRule) {
        super(createdTime, createdBy, updatedTime, updatedBy);
        this.id = id;
        this.value = value;
        this.effectiveDate = effectiveDate;
        this.accountingRule = accountingRule;
    }
}
