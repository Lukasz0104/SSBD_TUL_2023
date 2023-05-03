package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AddressDto(
    @NotNull
    @Size(min = 6, max = 6)
    String postalCode,

    @NotNull
    @Size(min = 2, max = 85)
    String city,

    @NotNull
    @Size(max = 85)
    String street,

    @Positive
    @NotNull
    Integer buildingNumber
) {
}
