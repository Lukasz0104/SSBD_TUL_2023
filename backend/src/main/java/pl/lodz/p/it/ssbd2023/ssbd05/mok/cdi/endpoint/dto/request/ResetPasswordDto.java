package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.ValidPassword;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.ValidUUID;

@Data
public class ResetPasswordDto {

    @ValidPassword
    private String password;

    @ValidUUID
    private String token;
}
