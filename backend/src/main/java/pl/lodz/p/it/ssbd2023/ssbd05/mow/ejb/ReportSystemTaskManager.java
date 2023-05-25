package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb;

import jakarta.annotation.security.DenyAll;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Startup
@Singleton
@DenyAll
public class ReportSystemTaskManager {
    
    private void createReports() {
        throw new UnsupportedOperationException();
    }
}
