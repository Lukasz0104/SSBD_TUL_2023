package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AccessLevelDto;

import java.time.LocalDateTime;
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

    public AccountDto(Long id, Long version, Set<AccessLevelDto> accessLevels, String email,
                      String login, String firstName, String lastName, String language, boolean twoFactorAuth,
                      LocalDateTime createdTime, String createdBy, LocalDateTime updatedTime,
                      String updatedBy, boolean verified, boolean active, ActivityTrackerDto activityTracker) {
        super(id, version, accessLevels, email, login, firstName, lastName, language, twoFactorAuth, createdTime,
            createdBy,
            updatedTime,
            updatedBy,
            activityTracker);
        this.verified = verified;
        this.active = active;
    }
}
