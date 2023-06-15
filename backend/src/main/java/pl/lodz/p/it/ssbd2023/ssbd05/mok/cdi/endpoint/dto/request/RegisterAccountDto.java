package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.ValidPassword;

@Data
public class RegisterAccountDto {
    @Email
    @NotNull
    private String email;

    @NotBlank
    private String login;

    @ValidPassword
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    @Pattern(regexp = "PL|EN", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String language;

    @NotBlank
    private String captchaCode;
}
