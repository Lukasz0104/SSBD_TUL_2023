package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createManagerAccessLevelFromDto;
import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createOwnerAccessLevelFromDto;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AdminData;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.AddManagerAccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.AddOwnerAccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers.AccountManagerLocal;

@RequestScoped
@Path("/accounts/{id}/access-levels")
public class AccessLevelEndpoint {

    @Inject
    private AccountManagerLocal accountManager;

    @Context
    private SecurityContext securityContext;

    @PUT
    @RolesAllowed({"ADMIN"})
    @Path("/administrator")
    public void grantAdminAccessLevel(@PathParam("id") Long id) throws AppBaseException {
        accountManager.grantAccessLevel(id, new AdminData(), securityContext.getUserPrincipal().getName());
    }

    @PUT
    @RolesAllowed({"ADMIN"})
    @Path("/manager")
    public void grantManagerAccessLevel(@PathParam("id") Long id, @NotNull @Valid AddManagerAccessLevelDto dto)
        throws AppBaseException {
        accountManager.grantAccessLevel(
            id, createManagerAccessLevelFromDto(dto),
            securityContext.getUserPrincipal().getName());
    }

    @PUT
    @RolesAllowed({"MANAGER"})
    @Path("/owner")
    public void grantOwnerAccessLevel(@PathParam("id") Long id, @NotNull @Valid AddOwnerAccessLevelDto dto)
        throws AppBaseException {
        accountManager.grantAccessLevel(
            id, createOwnerAccessLevelFromDto(dto),
            securityContext.getUserPrincipal().getName());
    }

    @DELETE
    @RolesAllowed({"ADMIN"})
    @Path("/administrator")
    public void revokeAdminAccessLevel(@PathParam("id") Long id) throws AppBaseException {
        accountManager.revokeAccessLevel(id, AccessType.ADMIN, securityContext.getUserPrincipal().getName());
    }

    @DELETE
    @RolesAllowed({"ADMIN"})
    @Path("/manager")
    public void revokeManagerAccessLevel(@PathParam("id") Long id) throws AppBaseException {
        accountManager.revokeAccessLevel(id, AccessType.MANAGER, securityContext.getUserPrincipal().getName());
    }

    @DELETE
    @RolesAllowed({"MANAGER"})
    @Path("/owner")
    public void revokeOwnerAccessLevel(@PathParam("id") Long id) throws AppBaseException {
        accountManager.revokeAccessLevel(id, AccessType.OWNER, securityContext.getUserPrincipal().getName());
    }
}
