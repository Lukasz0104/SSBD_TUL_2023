package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.ValidPassword;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {

    @NotBlank
    private String oldPassword;

    @ValidPassword
    private String newPassword;

    @AssertFalse
    public boolean isPasswordSame() {
        return oldPassword.equals(newPassword);
    }

}
