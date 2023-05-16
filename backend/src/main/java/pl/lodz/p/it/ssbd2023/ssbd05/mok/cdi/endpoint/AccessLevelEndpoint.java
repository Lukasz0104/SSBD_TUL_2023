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
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AdminData;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppRollbackLimitExceededException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.AddManagerAccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.AddOwnerAccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers.AccountManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.AppProperties;

@RequestScoped
@Path("/accounts/{id}/access-levels")
public class AccessLevelEndpoint {

    @Inject
    private AccountManagerLocal accountManager;

    @Inject
    private AppProperties appProperties;

    @Context
    private SecurityContext securityContext;

    @PUT
    @RolesAllowed({"ADMIN"})
    @Path("/administrator")
    public Response grantAdminAccessLevel(@NotNull @PathParam("id") Long id) throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollbackTx = false;

        do {
            try {
                accountManager.grantAccessLevel(id, new AdminData(), securityContext.getUserPrincipal().getName());
                rollbackTx = accountManager.isLastTransactionRollback();
            } catch (AppOptimisticLockException aole) {
                rollbackTx = true;
                if (txLimit < 2) {
                    throw aole;
                }
            }
        } while (rollbackTx && --txLimit > 0);

        if (rollbackTx && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent().build();
    }

    @PUT
    @RolesAllowed({"ADMIN"})
    @Path("/manager")
    public Response grantManagerAccessLevel(@NotNull @PathParam("id") Long id,
                                            @NotNull @Valid AddManagerAccessLevelDto dto)
        throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollbackTx = false;

        do {
            accountManager.grantAccessLevel(
                id, createManagerAccessLevelFromDto(dto),
                securityContext.getUserPrincipal().getName());
            rollbackTx = accountManager.isLastTransactionRollback();
        } while (rollbackTx && --txLimit > 0);

        if (rollbackTx && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent().build();
    }

    @PUT
    @RolesAllowed({"MANAGER"})
    @Path("/owner")
    public Response grantOwnerAccessLevel(@NotNull @PathParam("id") Long id, @NotNull @Valid AddOwnerAccessLevelDto dto)
        throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollbackTx = false;

        do {
            accountManager.grantAccessLevel(
                id, createOwnerAccessLevelFromDto(dto),
                securityContext.getUserPrincipal().getName());
            rollbackTx = accountManager.isLastTransactionRollback();
        } while (rollbackTx && --txLimit > 0);

        if (rollbackTx && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent().build();
    }

    @DELETE
    @RolesAllowed({"ADMIN"})
    @Path("/administrator")
    public Response revokeAdminAccessLevel(@NotNull @PathParam("id") Long id) throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollbackTx = false;

        do {
            try {
                accountManager.revokeAccessLevel(id, AccessType.ADMIN, securityContext.getUserPrincipal().getName());
                rollbackTx = accountManager.isLastTransactionRollback();
            } catch (AppOptimisticLockException e) {
                rollbackTx = true;
                if (txLimit < 2) {
                    throw e;
                }
            }
        } while (rollbackTx && txLimit-- > 0);

        if (rollbackTx && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent().build();
    }

    @DELETE
    @RolesAllowed({"ADMIN"})
    @Path("/manager")
    public Response revokeManagerAccessLevel(@NotNull @PathParam("id") Long id) throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollbackTx = false;

        do {
            try {
                accountManager.revokeAccessLevel(id, AccessType.MANAGER, securityContext.getUserPrincipal().getName());
                rollbackTx = accountManager.isLastTransactionRollback();
            } catch (AppOptimisticLockException e) {
                rollbackTx = true;
                if (txLimit < 2) {
                    throw e;
                }
            }
        } while (rollbackTx && txLimit-- > 0);

        if (rollbackTx && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent().build();
    }

    @DELETE
    @RolesAllowed({"MANAGER"})
    @Path("/owner")
    public Response revokeOwnerAccessLevel(@NotNull @PathParam("id") Long id) throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollbackTx = false;

        do {
            try {
                accountManager.revokeAccessLevel(id, AccessType.OWNER, securityContext.getUserPrincipal().getName());
                rollbackTx = accountManager.isLastTransactionRollback();
            } catch (AppOptimisticLockException e) {
                rollbackTx = true;
                if (txLimit < 2) {
                    throw e;
                }
            }
        } while (rollbackTx && txLimit-- > 0);

        if (rollbackTx && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent().build();
    }
}
