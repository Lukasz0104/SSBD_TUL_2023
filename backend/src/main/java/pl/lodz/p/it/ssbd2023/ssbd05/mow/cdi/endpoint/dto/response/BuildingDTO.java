package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AddressDto;

public record BuildingDTO(
    @NotNull
    Long id,

    @NotNull
    @Valid
    AddressDto address) {

}
