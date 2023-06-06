package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request.AddReadingAsManagerDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request.AddReadingAsOwnerDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ReadingManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.ReadingDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

@RequestScoped
@Path("/readings")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class ReadingEndpoint {

    @Inject
    private ReadingManagerLocal readingManager;

    @Inject
    private RollbackUtils rollbackUtils;

    @Context
    private SecurityContext securityContext;

    @POST
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({OWNER})
    public Response createReadingAsOwner(@Valid @NotNull AddReadingAsOwnerDto dto) throws AppBaseException {
        return rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
            () -> readingManager.createReadingAsOwner(
                ReadingDtoConverter.createReadingFromDto(dto),
                dto.getMeterId(), securityContext.getUserPrincipal().getName()), readingManager).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER})
    public Response createReadingAsManager(@Valid @NotNull AddReadingAsManagerDto dto) throws AppBaseException {
        return rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
            () -> readingManager.createReadingAsManager(
                ReadingDtoConverter.createReadingFromDto(dto),
                dto.getMeterId()), readingManager).build();
    }
}
