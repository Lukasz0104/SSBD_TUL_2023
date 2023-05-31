package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
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
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppRollbackLimitExceededException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppTransactionRolledBackException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.CostDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.CostManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.AppProperties;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.CostDtoConverter;

@RequestScoped
@Path("/costs")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class CostEndpoint {

    @Inject
    private CostManagerLocal costManager;

    @Inject
    private AppProperties appProperties;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getAllCosts(
        @QueryParam("page") int page,
        @QueryParam("pageSize") int pageSize,
        @DefaultValue("true") @QueryParam("asc") Boolean ascending,
        @DefaultValue("") @QueryParam("year") String year,
        @DefaultValue("") @QueryParam("month") String month,
        @DefaultValue("") @QueryParam("category") String category
    ) throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollBackTX = false;

        Page<CostDto> costDtoPage = null;
        do {
            try {
                costDtoPage = CostDtoConverter.createCostDtoPage(
                    costManager.getAllCostsPage(page, pageSize, ascending, year, month, category)
                );
                rollBackTX = costManager.isLastTransactionRollback();
            } catch (AppTransactionRolledBackException atrbe) {
                rollBackTX = true;
            }
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }
        return Response.ok(costDtoPage).build();
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
        throw new UnsupportedOperationException();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response removeCost(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
