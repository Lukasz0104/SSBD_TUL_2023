package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.ValidPassword;

@Data
public class ResetPasswordDto {

    @ValidPassword
    private String password;

    @UUID
    @NotBlank
    private String token;
}
