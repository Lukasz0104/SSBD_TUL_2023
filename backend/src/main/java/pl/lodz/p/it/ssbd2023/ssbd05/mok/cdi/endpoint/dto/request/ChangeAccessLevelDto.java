package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeAccessLevelDto {

    @NotBlank
    private String accessType;

    @AssertTrue
    public boolean isValid() {
        try {
            AccessType a = AccessType.valueOf(this.getAccessType());
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
