package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Startup
@Singleton
@PermitAll
public class ForecastSystemTaskManager {
    private void createForecasts() {
        throw new UnsupportedOperationException();
    }

    private void recalculateForecasts() {
        throw new UnsupportedOperationException();
    }
}
