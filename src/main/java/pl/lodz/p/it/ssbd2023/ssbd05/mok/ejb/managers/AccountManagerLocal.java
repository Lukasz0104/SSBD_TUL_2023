package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;

import java.util.UUID;

@Local
public interface AccountManagerLocal {
    void registerAccount(Account account) throws AppBaseException;

    void confirmRegistration(UUID token) throws AppBaseException;
}
