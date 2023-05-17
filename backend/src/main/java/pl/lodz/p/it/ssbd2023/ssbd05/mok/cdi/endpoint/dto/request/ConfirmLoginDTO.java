package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ConfirmLoginDTO {

    @Pattern(regexp = "\\d{8}")
    private String code;

    @NotBlank
    private String login;
}
