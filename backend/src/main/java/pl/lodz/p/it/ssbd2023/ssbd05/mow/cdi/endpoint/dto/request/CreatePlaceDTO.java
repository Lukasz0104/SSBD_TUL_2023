package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreatePlaceDTO(
    @NotNull
    @Positive
    Integer placeNumber,

    @NotNull
    @Positive
    @Max(1000000)
    BigDecimal squareFootage,

    @NotNull
    @PositiveOrZero
    @Max(100)
    Integer residentsNumber,

    @NotNull
    Long buildingId
) {
}
