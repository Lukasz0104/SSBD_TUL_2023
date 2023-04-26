package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminDataDto extends AccessLevelDto {
    public AdminDataDto(
        Long id,
        Long version,
        @NotNull AccessType level) {
        super(id, version, level);
    }
}
