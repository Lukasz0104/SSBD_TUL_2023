package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.MeterManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.ReadingDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

@RequestScoped
@Path("/meters")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class MeterEndpoint {

    @Inject
    private MeterManagerLocal meterManager;

    @Inject
    private RollbackUtils rollbackUtils;

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/{id}/readings")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getMeterReadingsAsManager(@PathParam("id") Long id,
                                              @QueryParam("page") int page,
                                              @QueryParam("pageSize") int pageSize) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> ReadingDtoConverter.createReadingDtoPage(meterManager.getMeterReadingsAsManager(id, page, pageSize)),
            meterManager
        ).build();
    }

    @GET
    @Path("/me/{id}/readings")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(OWNER)
    public Response getMeterReadingsAsOwner(@PathParam("id") Long id,
                                            @QueryParam("page") int page,
                                            @QueryParam("pageSize") int pageSize) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> ReadingDtoConverter.createReadingDtoPage(
                meterManager.getMeterReadingsAsOwner(id, securityContext.getUserPrincipal().getName(), page, pageSize)),
            meterManager
        ).build();
    }
}
