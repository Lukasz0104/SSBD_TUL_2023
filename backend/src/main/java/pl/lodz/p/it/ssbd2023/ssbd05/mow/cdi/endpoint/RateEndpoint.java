package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request.CreateRateDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.RateManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.RateDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;


@RequestScoped
@Path("/rates")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class RateEndpoint {

    @Inject
    private RateManagerLocal rateManager;

    @Inject
    private RollbackUtils rollbackUtils;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getCurrentRates() throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> RateDtoConverter.createPublicRateDtoList(rateManager.getCurrentRates()),
            rateManager
        ).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response createRate(@NotNull @Valid CreateRateDto createRateDto) throws AppBaseException {
        Rate newRate = RateDtoConverter.createRateFromCreateRateDto(createRateDto);
        return rollbackUtils.rollBackTXBasicWithReturnNoContentStatus(() -> rateManager.createRate(newRate,
            createRateDto.getCategoryId()), rateManager).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response removeFutureRate(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
