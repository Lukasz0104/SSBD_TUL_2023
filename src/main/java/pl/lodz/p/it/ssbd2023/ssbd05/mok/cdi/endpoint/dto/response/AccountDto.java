package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountDto extends OwnAccountDto {
    @NotNull
    private boolean verified;

    @NotNull
    private boolean active;

    @NotNull
    private ActivityTrackerDto activityTracker;

    public AccountDto(Long id,
                      Long version,
                      @NotNull Set<AccessLevelDto> accessLevels, @NotNull @Email @Size(min = 3, max = 320) String email,
                      @NotNull @Size(min = 3, max = 100) String login,
                      @NotNull @Size(min = 1, max = 100) String firstName,
                      @NotNull @Size(min = 1, max = 100) String lastName,
                      @NotNull @Size(min = 2, max = 2) String language,
                      boolean verified,
                      boolean active,
                      ActivityTrackerDto activityTracker) {
        super(id, version, accessLevels, email, login, firstName, lastName, language);
        this.verified = verified;
        this.active = active;
        this.activityTracker = activityTracker;
    }
}
