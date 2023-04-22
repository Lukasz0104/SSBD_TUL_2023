package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AddressDto;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterOwnerDto extends RegisterAccountDto {
    @NotNull
    @Valid
    private AddressDto address;
}
