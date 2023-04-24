package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Getter
public class Properties {

    @Inject
    @ConfigProperty(name = "security.jwt.secret")
    private String secret;

    @Inject
    @ConfigProperty(name = "security.jwt.expirationTime", defaultValue = "900000")
    private int jwtExpirationTime;

    @Inject
    @ConfigProperty(name = "security.login.fail.limit", defaultValue = "5")
    private int unsuccessfulLoginChainLimit;

    @Inject
    @ConfigProperty(name = "security.jwt.issuer")
    private String issuer;
}
