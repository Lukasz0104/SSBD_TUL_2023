package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.dto.AddressDto;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.ValidLicenseNumber;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterManagerDto extends RegisterAccountDto {
    @ValidLicenseNumber
    private String licenseNumber;

    @NotNull
    @Valid
    private AddressDto address;
}
