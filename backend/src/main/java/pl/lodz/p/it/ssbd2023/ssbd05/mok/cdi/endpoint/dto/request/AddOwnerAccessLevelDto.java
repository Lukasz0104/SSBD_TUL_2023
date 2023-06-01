package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.dto.AddressDto;

public record AddOwnerAccessLevelDto(
    @NotNull
    @Valid
    AddressDto address
) {
}
