package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeAccessLevelDto {

    @NotNull
    private AccessType accessType;
}
