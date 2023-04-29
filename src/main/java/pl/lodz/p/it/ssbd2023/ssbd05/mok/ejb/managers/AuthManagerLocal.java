package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import java.util.UUID;
import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.JwtRefreshTokenDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.LoginResponseDto;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;

@Local
public interface AuthManagerLocal extends CommonManagerInterface {
    JwtRefreshTokenDto registerSuccessfulLogin(String login, String ip) throws AppBaseException;

    void registerUnsuccessfulLogin(String login, String ip) throws AppBaseException;

    JwtRefreshTokenDto refreshJwt(UUID refreshToken, String login) throws AppBaseException;
}
