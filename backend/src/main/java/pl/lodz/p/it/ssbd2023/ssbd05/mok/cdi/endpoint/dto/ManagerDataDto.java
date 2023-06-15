package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.dto.AddressDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ManagerDataDto extends AccessLevelDto {

    @NotNull
    @Valid
    private AddressDto address;

    @NotNull
    @NotBlank
    private String licenseNumber;

    public ManagerDataDto(Long id, Long version, boolean verified, boolean active,
                          LocalDateTime createdTime, String createdBy, LocalDateTime updatedTime,
                          String updatedBy, AddressDto address, String licenseNumber) {
        super(id, version, verified, active, createdTime, createdBy, updatedTime, updatedBy);
        this.address = address;
        this.licenseNumber = licenseNumber;
    }

    public ManagerDataDto(Long id, Long version, boolean verified, boolean active, AddressDto address,
                          String licenseNumber) {
        super(id, version, verified, active);
        this.address = address;
        this.licenseNumber = licenseNumber;
    }
}
