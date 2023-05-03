package pl.lodz.p.it.ssbd2023.ssbd05.config;

import io.jsonwebtoken.Claims;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.JwtUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.RequestScoped;

@RequestScoped
public class JwtAuthenticationMechanism implements HttpAuthenticationMechanism {

    @Inject
    private JwtUtils jwtUtils;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest httpServletRequest,
                                                HttpServletResponse httpServletResponse,
                                                HttpMessageContext httpMessageContext) {
        String jwt = jwtUtils.getToken(httpServletRequest);
        if (jwt == null || !jwtUtils.validateToken(jwt)) {
            return httpMessageContext.notifyContainerAboutLogin("anonymous",
                new HashSet<>(Collections.singleton("GUEST")));
        }
        Claims claims = jwtUtils.parseJWT(jwt).getBody();
        String subject = claims.getSubject();

        Set<String> accessLevels = new HashSet<>((ArrayList<String>) claims.get("groups"));

        return httpMessageContext.notifyContainerAboutLogin(subject, accessLevels);
    }
}
