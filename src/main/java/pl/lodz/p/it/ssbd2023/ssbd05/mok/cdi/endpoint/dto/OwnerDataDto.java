package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class OwnerDataDto extends AccessLevelDto {
    @NotNull
    private AddressDto address;

    public OwnerDataDto(Long id, Long version, AddressDto address) {
        super(id, version);
        this.address = address;
    }
}
