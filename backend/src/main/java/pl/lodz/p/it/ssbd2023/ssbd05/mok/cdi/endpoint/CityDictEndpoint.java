package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers.CityDictManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

@RequestScoped
@Path("/city-dict")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class CityDictEndpoint {

    @Inject
    private RollbackUtils rollbackUtils;

    @Inject
    private CityDictManagerLocal addressDictManager;

    @GET
    @PermitAll
    @Path("/city")
    public Response getCitiesStartingWith(@NotBlank @Pattern(regexp = "[A-ZĄĆĘŁÓŚŹŻ]+.*")
                                          @QueryParam(value = "pattern") String pattern)
        throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> addressDictManager.getCitiesStartingWith(pattern),
            addressDictManager
        ).build();
    }
}