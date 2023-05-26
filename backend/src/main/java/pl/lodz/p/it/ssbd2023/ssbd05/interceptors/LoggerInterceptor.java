package pl.lodz.p.it.ssbd2023.ssbd05.interceptors;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.security.enterprise.SecurityContext;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerInterceptor {
    @Inject
    private SecurityContext sctx;
    private static final Logger LOGGER = Logger.getLogger(LoggerInterceptor.class.getName());

    @AroundInvoke
    public Object traceInvoke(InvocationContext ictx) throws Exception {
        StringBuilder message = new StringBuilder("Captured method invocation: ");
        Object result;
        try {
            try {
                String login = "anonymous";
                if (sctx != null && sctx.getCallerPrincipal() != null) {
                    login = sctx.getCallerPrincipal().getName();
                    login = (login != null) ? login : "anonymous";
                }

                message.append(ictx.getMethod().toString());
                message.append(" user: ").append(login);
                message.append(" parameter values: ");
                if (null != ictx.getParameters()) {
                    for (Object param : ictx.getParameters()) {
                        message.append(param).append(" ");
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected exception in interceptor's code: ", e);
                throw e;
            }

            result = ictx.proceed();

        } catch (Exception e) {
            message.append(" ended with exception: ").append(e);
            LOGGER.log(Level.SEVERE, message.toString(), e);
            throw e;
        }

        message.append(" returned value: ").append(result).append(" ");

        LOGGER.info(message.toString());

        return result;
    }
}
