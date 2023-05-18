package pl.lodz.p.it.ssbd2023.ssbd05.interceptors;

import jakarta.ejb.AccessLocalException;
import jakarta.ejb.EJBAccessException;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppForbiddenException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppInternalServerErrorException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppTransactionRolledBackException;

public class GenericManagerExceptionsInterceptor {
    @AroundInvoke
    public Object intercept(InvocationContext ictx) throws Exception {
        try {
            return ictx.proceed();

        } catch (AppBaseException abe) {
            throw abe;
        } catch (EJBAccessException | AccessLocalException ejbae) {
            throw new AppForbiddenException();
        } catch (EJBTransactionRolledbackException etre) {
            throw new AppTransactionRolledBackException();
        } catch (Exception e) {
            throw new AppInternalServerErrorException();
        }
    }
}
