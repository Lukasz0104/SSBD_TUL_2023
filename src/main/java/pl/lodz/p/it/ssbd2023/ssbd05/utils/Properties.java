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
    private String jwtSecret;

    @Inject
    @ConfigProperty(name = "security.jws.secret")
    private String jwsSecret;

    @Inject
    @ConfigProperty(name = "security.jwt.expirationTime", defaultValue = "900000")
    private int jwtExpirationTime;

    @Inject
    @ConfigProperty(name = "security.login.fail.limit", defaultValue = "5")
    private int unsuccessfulLoginChainLimit;

    @Inject
    @ConfigProperty(name = "security.jwt.issuer")
    private String issuer;

    @Inject
    @ConfigProperty(name = "mail.smtp.host")
    private String smtpHost;

    @Inject
    @ConfigProperty(name = "mail.smtp.starttls.enable")
    private boolean smtpStarttls;

    @Inject
    @ConfigProperty(name = "mail.smtp.ssl.enable")
    private boolean smtpSsl;

    @Inject
    @ConfigProperty(name = "mail.smtp.auth")
    private boolean smtpAuth;

    @Inject
    @ConfigProperty(name = "mail.smtp.port")
    private int smtpPort;

    @Inject
    @ConfigProperty(name = "mail.sender")
    private String sender;

    @Inject
    @ConfigProperty(name = "mail.password")
    private String senderPassword;

    @Inject
    @ConfigProperty(name = "frontend.url")
    private String frontendUrl;

    @Inject
    @ConfigProperty(name = "backend.url")
    private String backendUrl;

    @Inject
    @ConfigProperty(name = "transaction.repeat.limit", defaultValue = "3")
    private int transactionRepeatLimit;
}
