package pl.lodz.p.it.ssbd2023.ssbd05.interceptors;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerInterceptor {
    @Resource
    private SessionContext sctx;
    private static final Logger LOGGER = Logger.getLogger(LoggerInterceptor.class.getName());

    @AroundInvoke
    public Object traceInvoke(InvocationContext ictx) throws Exception {
        StringBuilder message = new StringBuilder("Captured method invocation: ");
        Object result;
        try {
            try {
                message.append(ictx.getMethod().toString());
                message.append(" user: ").append(sctx.getCallerPrincipal().getName());
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
