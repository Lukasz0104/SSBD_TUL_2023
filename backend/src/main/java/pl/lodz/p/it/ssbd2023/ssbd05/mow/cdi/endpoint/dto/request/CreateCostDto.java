package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request;

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
    private int year;

    @NotNull
    @Range(min = 1, max = 12)
    private int month;

    @NotNull
    private Long categoryId;

    @NotNull
    @PositiveOrZero
    private BigDecimal totalConsumption;

    @NotNull
    @Positive
    private BigDecimal realRate;
}
