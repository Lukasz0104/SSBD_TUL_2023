package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Local
public interface AccountManagerLocal extends CommonManagerInterface {
    void registerAccount(Account account) throws AppBaseException;

    void confirmRegistration(UUID token) throws AppBaseException;

    void changePassword(String oldPass, String newPass, String login) throws AppBaseException;

    void sendResetPasswordMessage(String email) throws AppBaseException;

    void resetPassword(String password, UUID token) throws AppBaseException;

    void changeEmail(String login) throws AppBaseException;

    void confirmEmail(String email, UUID confirmToken, String login) throws AppBaseException;

    void changeActiveStatusAsManager(String managerLogin, Long userId, boolean status) throws AppBaseException;

    void changeActiveStatusAsAdmin(String adminLogin, Long userId, boolean status) throws AppBaseException;

    Account getAccountDetails(Long id) throws AppBaseException;

    Account getAccountDetails(String login) throws AppBaseException;

    AccessType changeAccessLevel(String login, AccessType accessLevel) throws AppBaseException;

    void changeAccountLanguage(String login, String language) throws AppBaseException;

    List<Account> getAllAccounts(boolean active);

    List<Account> getOwnerAccounts(boolean active);

    List<Account> getManagerAccounts(boolean active);

    List<Account> getAdminAccounts(boolean active);

    void deleteUnverifiedAccounts(LocalDateTime now) throws AppBaseException;

    void deleteExpiredTokens(LocalDateTime now) throws AppBaseException;

    void remindToConfirmRegistration(LocalDateTime now);

    void grantAccessLevel(Long id, AccessLevel accessLevel, String login) throws AppBaseException;

    Account editPersonalData(Account account, String login) throws AppBaseException;

    void revokeAccessLevel(Long id, AccessType accessType, String login) throws AppBaseException;
}
