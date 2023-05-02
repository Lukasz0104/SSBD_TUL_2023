package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AddressDto;

public record AddManagerAccessLevelDto(
    @NotBlank
    @NotNull
    String licenseNumber,

    @NotNull
    @Valid
    AddressDto address
) {
}
