package pl.lodz.p.it.ssbd2023.ssbd05.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.HashGenerator;

@DatabaseIdentityStoreDefinition(
    dataSourceLookup = "java:app/jdbc/ssbd05auth",
    callerQuery = "select distinct password from auth_view where login = ?",
    groupsQuery = "select level from auth_view where login = ?",
    hashAlgorithm = HashGenerator.class
)
@ApplicationScoped
public class IdentityStoreConfig {
}
