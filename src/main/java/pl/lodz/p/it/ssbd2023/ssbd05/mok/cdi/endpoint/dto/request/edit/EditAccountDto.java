package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.edit;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Language;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditAccountDto {
    @NotNull
    private Long id;

    @NotNull
    private Long version;

    @NotBlank
    private String firstName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String lastName;

    @NotBlank
    private Language language;

    @NotNull
    private Set<EditAccessLevelDto> accessLevels;
}
