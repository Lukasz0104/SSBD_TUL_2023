package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.ValidPassword;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginDto {
    @NotBlank
    private String login;

    @ValidPassword
    private String password;
}
