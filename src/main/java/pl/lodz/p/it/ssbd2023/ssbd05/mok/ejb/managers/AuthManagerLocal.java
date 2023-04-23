package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.LoginResponseDto;

@Local
public interface AuthManagerLocal {
    LoginResponseDto registerSuccessfulLogin(String login, String ip) throws AppBaseException;

    void registerUnsuccessfulLogin(String login, String ip) throws AppBaseException;
}
