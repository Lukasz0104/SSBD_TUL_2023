package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OwnAccountDto {

    @NotNull
    private Long id;

    @NotNull
    private Long version;

    @NotNull
    private Set<AccessLevelDto> accessLevels = new HashSet<>();

    @NotNull
    @Email
    @Size(min = 3, max = 320)
    private String email;

    @NotNull
    @Size(min = 3, max = 100)
    private String login;

    @NotNull
    @Size(min = 1, max = 100)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 100)
    private String lastName;

    @NotNull
    @Size(min = 2, max = 2)
    private String language = "PL";
}
