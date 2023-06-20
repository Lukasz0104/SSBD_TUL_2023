package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCostDto {

    @NotNull
    @PositiveOrZero
    private int year;

    @NotNull
    @Range(min = 1, max = 12)
    private int month;

    @NotNull
    private Long categoryId;

    @NotNull
    @PositiveOrZero
    @Max(999999999)
    private BigDecimal totalConsumption;

    @NotNull
    @Positive
    @Max(999999999)
    private BigDecimal realRate;
}
