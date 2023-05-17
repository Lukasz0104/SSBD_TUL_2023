package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AccountSignableDto;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OwnAccountDto extends AccountSignableDto {

    @NotNull
    private Long id;
    @NotNull
    @Email
    @Size(min = 3, max = 320)
    private String email;

    @NotNull
    @Size(min = 1, max = 100)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 100)
    private String lastName;

    @NotNull
    @Size(min = 2, max = 2)
    private String language = "PL";

    private boolean twoFactorAuth;

    public OwnAccountDto(Long id, Long version, Set<AccessLevelDto> accessLevels, String email, String login,
                         String firstName, String lastName, String language, boolean twoFactorAuth) {
        super(login, version, accessLevels);
        this.email = email;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.language = language;
        this.twoFactorAuth = twoFactorAuth;
    }
}
