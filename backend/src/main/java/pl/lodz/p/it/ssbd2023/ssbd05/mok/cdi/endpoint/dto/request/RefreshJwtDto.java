package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.ValidUUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefreshJwtDto {
    @NotBlank
    private String login;

    @ValidUUID
    private String refreshToken;
}
