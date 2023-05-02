package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ManagerDataDto extends AccessLevelDto {

    @NotNull
    @Valid
    private AddressDto address;

    @NotNull
    private String licenseNumber;

    public ManagerDataDto(Long id, Long version, AddressDto address, String licenseNumber) {
        super(id, version);
        this.address = address;
        this.licenseNumber = licenseNumber;
    }

}
