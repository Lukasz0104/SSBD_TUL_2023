package pl.lodz.p.it.ssbd2023.ssbd05;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/")
@DeclareRoles({"ADMIN", "MANAGER", "OWNER"})
public class JAXRSConfiguration extends Application {
}
