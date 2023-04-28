package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint;

import jakarta.annotation.security.RolesAllowed;
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
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.unauthorized.AuthenticationException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.LoginDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.LoginResponseDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers.AuthManagerLocal;

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

    @Context
    private SecurityContext securityContext;

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public LoginResponseDto login(@NotNull @Valid LoginDto dto) throws AppBaseException {
        String ip = httpServletRequest.getRemoteAddr();

        CredentialValidationResult credentialValidationResult =
            identityStoreHandler.validate(new UsernamePasswordCredential(dto.getLogin(), dto.getPassword()));

        if (credentialValidationResult.getStatus() != CredentialValidationResult.Status.VALID) {
            try {
                authManager.registerUnsuccessfulLogin(dto.getLogin(), ip);
            } catch (AccountNotFoundException anfe) {
                throw new AuthenticationException();
            } catch (DatabaseException de) {
                //TODO repeat transaction
            }
            throw new AuthenticationException();
        }

        try {
            return authManager.registerSuccessfulLogin(dto.getLogin(), ip);
        } catch (DatabaseException de) {
            //TODO repeat transaction
            throw new AuthenticationException();
        }
    }

    @DELETE
    @Path("/logout")
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logout(@NotNull @QueryParam("token") UUID token) throws AppBaseException {
        try {
            authManager.logout(token, securityContext.getUserPrincipal().getName());
        } catch (DatabaseException de) {
            //repeat transaction
            //should log out successfully anyway
            return Response.noContent().build();
        }
        return Response.noContent().build();
    }
}
