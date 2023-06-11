package pl.lodz.p.it.ssbd2023.ssbd05.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.postgresql.util.PSQLException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.PlaceNumberAlreadyTaken;

public class PlaceFacadeExceptionsInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext ictx) throws Exception {
        try {
            return ictx.proceed();
        } catch (OptimisticLockException ole) {
            throw ole;
        } catch (PersistenceException | PSQLException | DatabaseException pe) {
            Throwable exceptionCopy = pe;
            do {
                String message = exceptionCopy.getMessage();
                if (message.contains("place_number_building_id")) {
                    throw new PlaceNumberAlreadyTaken();
                }

                exceptionCopy = exceptionCopy.getCause();
            } while (exceptionCopy != null);

            throw new AppDatabaseException(pe);
        }
    }
}
