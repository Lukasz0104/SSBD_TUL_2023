package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb;

import jakarta.annotation.security.DenyAll;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ForecastManagerLocal;

@Startup
@Singleton
@DenyAll
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(LoggerInterceptor.class)
public class ForecastSystemTaskManager {

    @Inject
    private ForecastManagerLocal forecastManager;

    private void createForecasts() {
        throw new UnsupportedOperationException();
    }

    private void recalculateForecasts() {
        throw new UnsupportedOperationException();
    }
}
