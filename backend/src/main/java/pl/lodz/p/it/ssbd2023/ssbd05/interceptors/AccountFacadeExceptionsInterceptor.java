package pl.lodz.p.it.ssbd2023.ssbd05.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
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
        } catch (PersistenceException pe) {
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
            throw new AppDatabaseException(pe);
        }

    }
}
