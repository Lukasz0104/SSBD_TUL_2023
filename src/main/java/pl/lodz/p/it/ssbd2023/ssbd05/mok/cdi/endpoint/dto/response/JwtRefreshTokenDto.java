package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtRefreshTokenDto {
    @NotNull
    private String jwt;

    @NotNull
    private UUID refreshToken;
}
