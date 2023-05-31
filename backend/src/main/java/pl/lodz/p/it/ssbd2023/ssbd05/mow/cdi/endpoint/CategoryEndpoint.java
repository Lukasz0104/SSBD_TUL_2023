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
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppRollbackLimitExceededException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppTransactionRolledBackException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.CategoryDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.RateDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.CategoryManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.AppProperties;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.CategoryDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.RateDtoConverter;

import java.util.List;

@RequestScoped
@Path("/categories")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class CategoryEndpoint {

    @Inject
    private AppProperties appProperties;

    @Inject
    private CategoryManagerLocal categoryManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getAllCategories() throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollBackTX = false;

        List<CategoryDTO> categories = null;
        do {
            try {
                categories =
                    CategoryDtoConverter.createCategoryDtoListFromCategoryList(categoryManager.getAllCategories());
                rollBackTX = categoryManager.isLastTransactionRollback();
            } catch (AppTransactionRolledBackException atrbe) {
                rollBackTX = true;
            }
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }
        return Response.ok(categories).build();
    }

    @GET
    @Path("/{id}/rates")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getCategoryRates(@PathParam("id") @NotNull Long categoryId,
                                     @QueryParam("page") @NotNull @PositiveOrZero int page,
                                     @QueryParam("pageSize") @NotNull @PositiveOrZero int pageSize)
        throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollBackTX = false;

        Page<RateDTO> rates = null;
        do {
            try {
                rates = RateDtoConverter.createRateDTOPage(
                    categoryManager.getCategoryRates(categoryId, page, pageSize));
                rollBackTX = categoryManager.isLastTransactionRollback();
            } catch (AppTransactionRolledBackException atrbe) {
                rollBackTX = true;
            }
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }
        return Response.ok(rates).build();
    }
}
