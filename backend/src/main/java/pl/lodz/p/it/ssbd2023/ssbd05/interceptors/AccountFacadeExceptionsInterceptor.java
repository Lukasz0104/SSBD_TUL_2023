package pl.lodz.p.it.ssbd2023.ssbd05.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.postgresql.util.PSQLException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.AccessLevelAlreadyGrantedException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.EmailAddressAlreadyTakenException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.LicenseNumberAlreadyTakenException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.LoginAlreadyTakenException;

public class AccountFacadeExceptionsInterceptor {
    @AroundInvoke
    public Object intercept(InvocationContext ictx) throws Exception {
        try {
            return ictx.proceed();
        } catch (OptimisticLockException ole) {
            throw ole;
        } catch (PersistenceException | PSQLException | DatabaseException pe1) {
            Throwable pe = pe1;
            do {
                String exceptionMessage = pe.getMessage();
                if (exceptionMessage.contains("manager_data_license_number_key")) {
                    throw new LicenseNumberAlreadyTakenException();
                } else if (exceptionMessage.contains("account_email_key")) {
                    throw new EmailAddressAlreadyTakenException();
                } else if (exceptionMessage.contains("account_login_key")) {
                    throw new LoginAlreadyTakenException();
                } else if (exceptionMessage.contains("access_level_account_id_level_unique")) {
                    throw new AccessLevelAlreadyGrantedException();
                }
                pe = pe.getCause();
            } while (pe != null);
            throw new AppDatabaseException(pe1);
        }

    }
}
