package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.ValidCode;

@Data
public class ConfirmLoginDTO {

    @ValidCode
    private String code;

    @NotBlank
    private String login;
}
