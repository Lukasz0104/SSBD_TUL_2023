package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefreshJwtDto {
    @NotBlank
    private String login;

    @UUID
    @NotNull
    private String refreshToken;
}
