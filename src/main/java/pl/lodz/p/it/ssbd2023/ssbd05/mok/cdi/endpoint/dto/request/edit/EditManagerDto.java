package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.edit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AddressDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EditManagerDto extends EditAccessLevelDto {
    @NotBlank
    private String licenseNumber;

    @NotNull
    @Valid
    private AddressDto address;
}
