package pl.lodz.p.it.ssbd2023.ssbd05.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.ExpiredTokenException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.InvalidTokenException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.unauthorized.AuthenticationException;

public class AuthManagerExceptionsInterceptor {
    @AroundInvoke
    public Object intercept(InvocationContext ictx) throws Exception {
        try {
            return ictx.proceed();

        } catch (InvalidTokenException | ExpiredTokenException e) {
            throw new AuthenticationException();
        }
    }
}
