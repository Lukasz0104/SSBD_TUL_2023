package pl.lodz.p.it.ssbd2023.ssbd05.interceptors;

import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppInternalServerErrorException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppTransactionRolledBackException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.AppOptimisticLockException;

import java.sql.SQLException;

public class GenericFacadeExceptionsInterceptor {
    @AroundInvoke
    public Object intercept(InvocationContext ictx) throws Exception {
        try {
            return ictx.proceed();
        } catch (OptimisticLockException ole) {
            throw new AppOptimisticLockException();
        } catch (PersistenceException | SQLException e) {
            throw new AppDatabaseException(e);
        } catch (AppBaseException abe) {
            throw abe;
        } catch (EJBTransactionRolledbackException ejbtre) {
            throw new AppTransactionRolledBackException();
        } catch (Exception e) {
            throw new AppInternalServerErrorException();
        }
    }
}
