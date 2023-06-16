package pl.lodz.p.it.ssbd2023.ssbd05.interceptors;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.AppProperties;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.EventLogService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class EventLogInterceptor {
    private static final Set<Class<? extends Annotation>> modifyingOperations =
        Set.of(POST.class, PUT.class, DELETE.class);

    @Inject
    private AppProperties appProperties;

    @Inject
    private EventLogService eventLogService;

    @Inject
    private SecurityContext sctx;

    @AroundInvoke
    public Object traceInvoke(InvocationContext ictx) throws Exception {
        Method method = ictx.getMethod();

        boolean hasModifyingAnnotation = Arrays.stream(ictx.getMethod().getDeclaredAnnotations())
            .map(Annotation::annotationType)
            .anyMatch(modifyingOperations::contains);

        Object result = ictx.proceed();

        if (appProperties.isEventLogEnable() && hasModifyingAnnotation) {
            eventLogService.saveEvent(
                method.getName(),
                ictx.getParameters(),
                Optional.ofNullable(sctx.getCallerPrincipal())
                    .map(Principal::getName)
                    .orElse("anonymous"));
        }

        return result;
    }
}