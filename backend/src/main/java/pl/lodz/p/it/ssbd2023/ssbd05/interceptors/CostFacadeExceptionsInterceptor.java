package pl.lodz.p.it.ssbd2023.ssbd05.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.postgresql.util.PSQLException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.CostAlreadyExistsException;

public class CostFacadeExceptionsInterceptor {
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
                if (exceptionMessage.contains("unq_cost_0")) {
                    throw new CostAlreadyExistsException();
                }
                pe = pe.getCause();
            } while (pe != null);
            throw new AppDatabaseException(pe);
        }

    }
}
