package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.dto.AddressDto;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.ValidLicenseNumber;

public record AddManagerAccessLevelDto(
    @ValidLicenseNumber
    String licenseNumber,

    @NotNull
    @Valid
    AddressDto address
) {
}
