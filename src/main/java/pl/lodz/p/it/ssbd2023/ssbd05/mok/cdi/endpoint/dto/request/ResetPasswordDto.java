package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.ValidPassword;

import java.util.UUID;

@Data
public class ResetPasswordDto {

    @ValidPassword
    private String password;

    @NotNull
    private UUID token;
}
