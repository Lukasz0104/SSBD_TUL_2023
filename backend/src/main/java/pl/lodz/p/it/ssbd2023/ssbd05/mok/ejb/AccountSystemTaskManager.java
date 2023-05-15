package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb;

import jakarta.annotation.security.DenyAll;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers.AccountManagerLocal;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
@DenyAll
public class AccountSystemTaskManager {
    @Inject
    private AccountManagerLocal accountManager;

    private static final Logger LOGGER = Logger.getLogger(AccountSystemTaskManager.class.getName());

    @Schedule(hour = "5")
    private void deleteUnverifiedAccounts() {
        try {
            LocalDateTime now = LocalDateTime.now();
            accountManager.deleteUnverifiedAccounts(now);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while deleting unverified accounts: ", e);
        }
    }

    @Schedule(hour = "*", minute = "30")
    private void remindToConfirmRegistration() {
        try {
            LocalDateTime now = LocalDateTime.now();
            accountManager.remindToConfirmRegistration(now);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while reminding to confirm registration: ", e);
        }
    }

    @Schedule(hour = "*", minute = "15")
    private void deleteExpiredTokens() {
        try {
            LocalDateTime now = LocalDateTime.now();
            accountManager.deleteExpiredTokens(now);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while deleting expired tokens: ", e);
        }
    }
}
