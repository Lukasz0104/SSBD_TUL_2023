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
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppRollbackLimitExceededException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppTransactionRolledBackException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers.CityDictManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.AppProperties;

import java.util.ArrayList;
import java.util.List;

@RequestScoped
@Path("/city-dict")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class CityDictEndpoint {

    @Inject
    private AppProperties appProperties;

    @Inject
    private CityDictManagerLocal addressDictManager;

    @GET
    @PermitAll
    @Path("/city")
    public Response getCitiesStartingWith(@NotBlank @Pattern(regexp = "[A-ZĄĆĘŁÓŚŹŻ]+.*")
                                          @QueryParam(value = "pattern") String pattern)
        throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollBackTX = false;

        List<String> cityList = new ArrayList<>();
        do {
            try {
                cityList = addressDictManager.getCitiesStartingWith(pattern);
                rollBackTX = addressDictManager.isLastTransactionRollback();
            } catch (AppTransactionRolledBackException atrbe) {
                rollBackTX = true;
            }
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }
        return Response.ok(cityList).build();
    }
}