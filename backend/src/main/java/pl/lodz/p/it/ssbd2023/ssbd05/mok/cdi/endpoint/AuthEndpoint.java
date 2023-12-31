package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.unauthorized.AuthenticationException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ConfirmLoginDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.LoginDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.RefreshJwtDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.JwtRefreshTokenDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers.AuthManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.IpUtils;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.ValidUUID;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;


@Path("")
@RequestScoped
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class AuthEndpoint {

    protected static final Logger LOGGER = Logger.getGlobal();

    @Inject
    private HttpServletRequest httpServletRequest;

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Inject
    private AuthManagerLocal authManager;

    @Context
    private SecurityContext securityContext;

    @Inject
    private RollbackUtils rollbackUtils;

    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response login(@NotNull @Valid LoginDto dto) throws AppBaseException {
        String ip = IpUtils.getIpAddress(httpServletRequest);

        CredentialValidationResult credentialValidationResult =
            identityStoreHandler.validate(new UsernamePasswordCredential(dto.getLogin(), dto.getPassword()));

        if (credentialValidationResult.getStatus() == CredentialValidationResult.Status.VALID) {
            JwtRefreshTokenDto jwtRefreshTokenDto = rollbackUtils.rollBackTXWithOptimisticLockReturnTypeT(
                () -> authManager.registerSuccessfulLogin(dto.getLogin(), ip, false), authManager);

            if (jwtRefreshTokenDto == null) {
                return Response.accepted().build();
            }
            LOGGER.log(Level.INFO, "User={0} has started new session at {1} from address {2}.",
                new Object[] {dto.getLogin(), Instant.now(), ip});
            return Response.ok(jwtRefreshTokenDto).build();
        } else {
            rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
                () -> authManager.registerUnsuccessfulLogin(dto.getLogin(), ip), authManager);

            throw new AuthenticationException();
        }
    }

    @POST
    @Path("refresh")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response refreshJwt(@NotNull @Valid RefreshJwtDto dto) throws AppBaseException {
        String ip = IpUtils.getIpAddress(httpServletRequest);
        JwtRefreshTokenDto jwtRefreshTokenDto = rollbackUtils.rollBackTXWithOptimisticLockReturnTypeT(
            () -> authManager.refreshJwt(dto.getRefreshToken(), dto.getLogin()), authManager);
        LOGGER.log(Level.INFO, "User={0} has renewed his session at {1} from address {2}.",
            new Object[] {dto.getLogin(), Instant.now(), ip});
        return Response.ok(jwtRefreshTokenDto).build();
    }

    @DELETE
    @Path("logout")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, MANAGER, OWNER})
    public Response logout(@ValidUUID @QueryParam("token") String token)
        throws AppBaseException {
        String ip = IpUtils.getIpAddress(httpServletRequest);
        String login = securityContext.getUserPrincipal().getName();

        Response.ResponseBuilder responseBuilder = rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
            () -> authManager.logout(token, login),
            authManager
        );

        LOGGER.log(Level.INFO, "User={0} has ended his session at {1} from address {2}.",
            new Object[] {login, Instant.now(), ip});
        return responseBuilder.build();
    }

    @POST
    @Path("/confirm-login")
    @PermitAll
    public Response confirmLogin(@NotNull @Valid ConfirmLoginDTO dto) throws AppBaseException {
        try {
            authManager.confirmLogin(dto.getLogin(), dto.getCode());
        } catch (TokenNotFoundException e) {
            rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
                () -> authManager.registerUnsuccessfulLogin(dto.getLogin(), IpUtils.getIpAddress(httpServletRequest)),
                authManager);
            throw e;
        }

        String ip = IpUtils.getIpAddress(httpServletRequest);
        JwtRefreshTokenDto jwtRefreshTokenDto = rollbackUtils.rollBackTXWithOptimisticLockReturnTypeT(
            () -> authManager.registerSuccessfulLogin(dto.getLogin(), ip, true),
            authManager
        );
        return Response.ok(jwtRefreshTokenDto).build();
    }
}
