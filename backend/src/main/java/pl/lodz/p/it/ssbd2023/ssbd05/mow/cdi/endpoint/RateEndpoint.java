package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppRollbackLimitExceededException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppTransactionRolledBackException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.RatePublicDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.RateManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.AppProperties;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.RateDtoConverter;

import java.util.List;

@RequestScoped
@Path("/rates")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class RateEndpoint {

    @Inject
    private RateManagerLocal rateManager;

    @Inject
    private AppProperties appProperties;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getCurrentRates() throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollBackTX = false;

        List<RatePublicDTO> rates = null;
        do {
            try {
                rates =
                    RateDtoConverter.createPublicRateDtoList(rateManager.getCurrentRates());
                rollBackTX = rateManager.isLastTransactionRollback();
            } catch (AppTransactionRolledBackException atrbe) {
                rollBackTX = true;
            }
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }
        return Response.ok(rates).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response createRate() throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response removeFutureRate(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
