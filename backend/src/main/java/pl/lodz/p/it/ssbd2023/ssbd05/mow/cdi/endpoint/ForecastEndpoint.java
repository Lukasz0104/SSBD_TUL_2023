package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request.AddOverdueForecastDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ForecastManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

import java.time.Year;

@RequestScoped
@Path("/forecasts")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class ForecastEndpoint {

    @Inject
    private ForecastManagerLocal forecastManager;

    @Context
    private SecurityContext securityContext;

    @Inject
    private RollbackUtils rollbackUtils;

    @POST
    @Path("/add-current")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response createOverdueForecast(@Valid @NotNull AddOverdueForecastDto addOverdueForecastDto)
        throws AppBaseException {
        return rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
            () -> forecastManager.createCurrentForecast(
                addOverdueForecastDto.getPlaceId(),
                addOverdueForecastDto.getCategoryId(),
                addOverdueForecastDto.getAmount(),
                securityContext.getUserPrincipal().getName()),
            forecastManager
        ).build();
    }

    @GET
    @Path("/years/{id}/place")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getForecastYearsByPlaceId(@PathParam("id") Long id) {
        return Response.ok(forecastManager.getForecastYearsByPlaceId(id)).build();
    }

    @GET
    @Path("/me/years/{id}/place")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(OWNER)
    public Response getForecastYearsByOwnPlaceId(@PathParam("id") Long id) throws AppBaseException {
        return Response.ok(
            forecastManager.getForecastYearsByOwnPlaceId(id, securityContext.getUserPrincipal().getName())).build();
    }

    @GET
    @Path("/me/min-month/{id}/place")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(OWNER)
    public Response getOwnMinMonthByPlaceAndYear(@PathParam("id") Long id,
                                                 @QueryParam("year") @Min(2020) @Max(2999) Integer year)
        throws AppBaseException {
        return Response.ok(
            forecastManager.getOwnMinMonthFromForecast(
                id,
                Year.of(year),
                securityContext.getUserPrincipal().getName())
        ).build();
    }

    @GET
    @Path("/min-month/{id}/place")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getMinMonthByPlaceAndYear(@PathParam("id") Long id,
                                              @QueryParam("year") @Min(2020) @Max(2999) Integer year)
        throws AppBaseException {
        return Response.ok(forecastManager.getMinMonthFromForecast(id, Year.of(year))).build();
    }
}
