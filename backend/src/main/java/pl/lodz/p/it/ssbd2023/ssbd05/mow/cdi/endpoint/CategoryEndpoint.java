package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.CategoryManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.CategoryDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.RateDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;


@RequestScoped
@Path("/categories")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class CategoryEndpoint {

    @Inject
    private CategoryManagerLocal categoryManager;


    @Inject
    private RollbackUtils rollbackUtils;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getAllCategories() throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> CategoryDtoConverter.createCategoryDtoListFromCategoryList(categoryManager.getAllCategories()),
            categoryManager
        ).build();
    }

    @GET
    @Path("/{id}/rates")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getCategoryRates(@PathParam("id") @NotNull Long categoryId,
                                     @QueryParam("page") @NotNull @PositiveOrZero int page,
                                     @QueryParam("pageSize") @NotNull @PositiveOrZero int pageSize)
        throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> RateDtoConverter.createRateDTOPage(categoryManager.getCategoryRates(categoryId, page, pageSize)),
            categoryManager
        ).build();
    }

}
