package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createManagerAccessLevelFromDto;
import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createOwnerAccessLevelFromDto;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AdminData;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.AddManagerAccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.AddOwnerAccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers.AccountManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

@RequestScoped
@Path("/accounts/{id}/access-levels")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class AccessLevelEndpoint {

    @Inject
    private AccountManagerLocal accountManager;

    @Context
    private SecurityContext securityContext;

    @Inject
    private RollbackUtils rollbackUtils;

    @PUT
    @RolesAllowed({ADMIN})
    @Path("/administrator")
    public Response grantAdminAccessLevel(@NotNull @PathParam("id") Long id) throws AppBaseException {
        String login = securityContext.getUserPrincipal().getName();
        return rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
            () -> accountManager.grantAccessLevel(id, new AdminData(), login),
            accountManager
        ).build();
    }

    @PUT
    @RolesAllowed({ADMIN})
    @Path("/manager")
    public Response grantManagerAccessLevel(@NotNull @PathParam("id") Long id,
                                            @NotNull @Valid AddManagerAccessLevelDto dto)
        throws AppBaseException {
        String login = securityContext.getUserPrincipal().getName();
        return rollbackUtils.rollBackTXBasicWithReturnNoContentStatus(
            () -> accountManager.grantAccessLevel(id, createManagerAccessLevelFromDto(dto), login),
            accountManager
        ).build();
    }

    @PUT
    @RolesAllowed({MANAGER})
    @Path("/owner")
    public Response grantOwnerAccessLevel(@NotNull @PathParam("id") Long id,
                                          @NotNull @Valid AddOwnerAccessLevelDto dto)
        throws AppBaseException {
        String login = securityContext.getUserPrincipal().getName();
        return rollbackUtils.rollBackTXBasicWithReturnNoContentStatus(
            () -> accountManager.grantAccessLevel(id, createOwnerAccessLevelFromDto(dto), login),
            accountManager
        ).build();
    }

    @DELETE
    @RolesAllowed({ADMIN})
    @Path("/administrator")
    public Response revokeAdminAccessLevel(@NotNull @PathParam("id") Long id) throws AppBaseException {
        String login = securityContext.getUserPrincipal().getName();
        return rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
            () -> accountManager.revokeAccessLevel(id, AccessType.ADMIN, login),
            accountManager
        ).build();
    }

    @DELETE
    @RolesAllowed({ADMIN})
    @Path("/manager")
    public Response revokeManagerAccessLevel(@NotNull @PathParam("id") Long id) throws AppBaseException {
        String login = securityContext.getUserPrincipal().getName();
        return rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
            () -> accountManager.revokeAccessLevel(id, AccessType.MANAGER, login),
            accountManager
        ).build();
    }

    @DELETE
    @RolesAllowed({MANAGER})
    @Path("/owner")
    public Response revokeOwnerAccessLevel(@NotNull @PathParam("id") Long id) throws AppBaseException {
        String login = securityContext.getUserPrincipal().getName();
        return rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
            () -> accountManager.revokeAccessLevel(id, AccessType.OWNER, login),
            accountManager
        ).build();
    }
}
