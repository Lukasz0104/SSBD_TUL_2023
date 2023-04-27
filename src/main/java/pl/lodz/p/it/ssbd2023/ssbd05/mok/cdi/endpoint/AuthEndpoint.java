package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint;

import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.ExpiredTokenException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.InvalidTokenException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.unauthorized.AuthenticationException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.LoginDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.RefreshJwtDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.JwtRefreshTokenDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers.AuthManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.Properties;

import java.util.UUID;

@Path("")
@RequestScoped
@TransactionAttribute(TransactionAttributeType.NEVER)
public class AuthEndpoint {

    @Inject
    private HttpServletRequest httpServletRequest;

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Inject
    private AuthManagerLocal authManager;

    @Inject
    private Properties properties;

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(@NotNull @Valid LoginDto dto) throws AppBaseException {
        String ip = httpServletRequest.getRemoteAddr();

        CredentialValidationResult credentialValidationResult =
            identityStoreHandler.validate(new UsernamePasswordCredential(dto.getLogin(), dto.getPassword()));

        int txLimit = properties.getTransactionRepeatLimit();
        int txCounter = 0;
        if (credentialValidationResult.getStatus() != CredentialValidationResult.Status.VALID) {
            do {
                try {
                    authManager.registerUnsuccessfulLogin(dto.getLogin(), ip);
                    throw new AuthenticationException();
                } catch (AccountNotFoundException anfe) {
                    throw new AuthenticationException();
                } catch (DatabaseException de) {
                    txCounter++;
                }
            } while (txCounter < txLimit);
            throw new AuthenticationException();
        }

        do {
            try {
                JwtRefreshTokenDto jwtRefreshTokenDto = authManager.registerSuccessfulLogin(dto.getLogin(), ip);
                return Response.status(200).entity(jwtRefreshTokenDto).build();
            } catch (DatabaseException de) {
                txCounter++;
            }
        } while (txCounter < txLimit);
        throw new AuthenticationException();
    }

    @POST
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response refreshJwt(@NotNull @Valid RefreshJwtDto dto) throws AppBaseException {
        UUID token;
        try {
            token = UUID.fromString(dto.getRefreshToken());
        } catch (IllegalArgumentException iae) {
            throw new InvalidTokenException();
        }

        int txLimit = properties.getTransactionRepeatLimit();
        int txCounter = 0;
        do {
            try {
                JwtRefreshTokenDto jwtRefreshTokenDto = authManager.refreshJwt(token, dto.getLogin());
                return Response.status(200).entity(jwtRefreshTokenDto).build();
            } catch (InvalidTokenException | ExpiredTokenException e) {
                throw new AuthenticationException();
            } catch (DatabaseException de) {
                txCounter++;
            }
        } while (txCounter < txLimit);
        throw new AuthenticationException();
    }
}
