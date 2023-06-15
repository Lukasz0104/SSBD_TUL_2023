package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOverdueForecastDto {

    @NotNull
    private Long placeId;

    @NotNull
    private Long categoryId;

    @Positive
    @NotNull
    private BigDecimal amount;
}
