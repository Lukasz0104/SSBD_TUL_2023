package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddCategoryDto {
    @NotNull
    private Long placeId;

    @NotNull
    private Long categoryId;

    @Positive
    @Max(999999999)
    private BigDecimal newReading;
}
