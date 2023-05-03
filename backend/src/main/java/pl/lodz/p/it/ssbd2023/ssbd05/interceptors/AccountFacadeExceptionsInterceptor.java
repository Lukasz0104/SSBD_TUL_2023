package pl.lodz.p.it.ssbd2023.ssbd05.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.LicenseNumberAlreadyTakenException;

public class AccountFacadeExceptionsInterceptor {
    @AroundInvoke
    public Object intercept(InvocationContext ictx) throws Exception {
        try {
            return ictx.proceed();
        } catch (OptimisticLockException ole) {
            throw ole;
        } catch (PersistenceException pe) {
            if (pe.getMessage().contains("manager_data_license_number_key")) {
                throw new LicenseNumberAlreadyTakenException();
            }
            throw new AppDatabaseException(pe);
        }

    }
}
