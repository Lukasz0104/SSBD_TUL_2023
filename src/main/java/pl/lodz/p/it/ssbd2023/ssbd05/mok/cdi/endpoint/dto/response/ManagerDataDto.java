package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AddressDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ManagerDataDto extends AccessLevelDto {

    @NotNull
    private AddressDto address;

    @NotNull
    private String licenseNumber;

    public ManagerDataDto(Long id,
                          Long version,
                          @NotNull AccessType level,
                          AddressDto address,
                          String licenseNumber) {
        super(id, version, level);
        this.address = address;
        this.licenseNumber = licenseNumber;
    }
}