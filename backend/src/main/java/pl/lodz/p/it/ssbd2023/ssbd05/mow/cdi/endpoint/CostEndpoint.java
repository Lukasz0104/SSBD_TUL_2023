package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.CostManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.CostDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

@RequestScoped
@Path("/costs")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class CostEndpoint {

    @Inject
    private CostManagerLocal costManager;

    @Inject
    private RollbackUtils rollbackUtils;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER})
    public Response getAllCosts(
        @QueryParam("page") int page,
        @QueryParam("pageSize") int pageSize,
        @DefaultValue("true") @QueryParam("asc") Boolean ascending,
        @NotNull @DefaultValue("-1") @QueryParam("year") Integer year,
        @DefaultValue("") @QueryParam("categoryName") String categoryName
    ) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> CostDtoConverter.createCostDtoPage(
                costManager.getAllCostsPage(page, pageSize, ascending, year, categoryName)),
            costManager
        ).build();
    }

    @GET
    @Path("/years")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER})
    public Response getYearsFromCosts() throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> costManager.getDistinctYearsFromCosts(),
            costManager
        ).build();
    }

    @GET
    @Path("/categories")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER})
    public Response getCategoryNamesFromCosts() throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> costManager.getDistinctCategoryNamesFromCosts(),
            costManager
        ).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response createCost() throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getCostDetails(@PathParam("id") Long id) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> CostDtoConverter.createDetailedCostDto(
                costManager.getCostDetails(id)),
            costManager
        ).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response removeCost(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
