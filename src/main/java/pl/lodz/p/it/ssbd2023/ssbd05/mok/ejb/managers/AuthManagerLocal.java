package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.LoginResponseDto;

import java.util.UUID;

@Local
public interface AuthManagerLocal {
    LoginResponseDto registerSuccessfulLogin(String login, String ip) throws AppBaseException;

    void registerUnsuccessfulLogin(String login, String ip) throws AppBaseException;

    void logout(UUID token, String login) throws AppBaseException;
}
