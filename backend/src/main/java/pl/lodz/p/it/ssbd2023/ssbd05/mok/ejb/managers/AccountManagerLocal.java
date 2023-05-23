package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;

import java.time.LocalDateTime;
import java.util.List;

@Local
public interface AccountManagerLocal extends CommonManagerInterface {
    void registerAccount(Account account) throws AppBaseException;

    void confirmRegistration(String token) throws AppBaseException;

    void changePassword(String oldPass, String newPass, String login) throws AppBaseException;

    void sendResetPasswordMessage(String email) throws AppBaseException;

    void resetPassword(String password, String token) throws AppBaseException;

    void changeEmail(String login) throws AppBaseException;

    void confirmEmail(String email, String confirmToken, String login) throws AppBaseException;

    void changeActiveStatusAsManager(String managerLogin, Long userId, boolean status) throws AppBaseException;

    void changeActiveStatusAsAdmin(String adminLogin, Long userId, boolean status) throws AppBaseException;

    Account getAccountDetails(Long id) throws AppBaseException;

    Account getAccountDetails(String login) throws AppBaseException;

    AccessType changeAccessLevel(String login, AccessType accessLevel) throws AppBaseException;

    void changeAccountLanguage(String login, String language) throws AppBaseException;

    void forcePasswordChange(String login) throws AppBaseException;

    void overrideForcedPassword(String password, String token) throws AppBaseException;

    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    void changePreferredTheme(String login, boolean lightTheme) throws AppBaseException;

    List<Account> getAllAccounts(boolean active) throws AppBaseException;

    List<Account> getOwnerAccounts(boolean active) throws AppBaseException;

    List<Account> getManagerAccounts(boolean active) throws AppBaseException;

    List<Account> getAdminAccounts(boolean active) throws AppBaseException;

    List<Account> getUnapprovedOwnerAccounts() throws AppBaseException;

    List<Account> getUnapprovedManagerAccounts() throws AppBaseException;

    void deleteUnverifiedAccounts(LocalDateTime now) throws AppBaseException;

    void deleteExpiredTokens(LocalDateTime now) throws AppBaseException;

    void remindToConfirmRegistration(LocalDateTime now);

    Account editPersonalDataByAdmin(Account account) throws AppBaseException;

    void grantAccessLevel(Long id, AccessLevel accessLevel, String login) throws AppBaseException;

    Account editPersonalData(Account account, String login) throws AppBaseException;

    void revokeAccessLevel(Long id, AccessType accessType, String login) throws AppBaseException;

    void changeTwoFactorAuthStatus(String login, Boolean status) throws AppBaseException;
}
