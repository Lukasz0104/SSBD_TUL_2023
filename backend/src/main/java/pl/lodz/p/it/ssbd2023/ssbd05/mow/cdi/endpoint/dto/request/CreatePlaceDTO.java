package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request;

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
    BigDecimal squareFootage,

    @NotNull
    @PositiveOrZero
    Integer residentsNumber,

    @NotNull
    Long buildingId
) {
}
