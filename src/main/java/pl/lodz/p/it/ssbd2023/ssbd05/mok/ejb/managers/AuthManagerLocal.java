package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.JwtRefreshTokenDto;

import java.util.UUID;

@Local
public interface AuthManagerLocal {
    JwtRefreshTokenDto registerSuccessfulLogin(String login, String ip) throws AppBaseException;

    void registerUnsuccessfulLogin(String login, String ip) throws AppBaseException;

    JwtRefreshTokenDto refreshJwt(UUID refreshToken, String login) throws AppBaseException;
}
