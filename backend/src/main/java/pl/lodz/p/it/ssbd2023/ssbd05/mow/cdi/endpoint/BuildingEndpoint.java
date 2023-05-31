package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
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
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.BuildingDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.BuildingManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.AppProperties;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.BuildingDtoConverter;

import java.util.ArrayList;
import java.util.List;

@RequestScoped
@Path("/buildings")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class BuildingEndpoint {

    @Inject
    private AppProperties appProperties;

    @Inject
    private BuildingManagerLocal buildingManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, MANAGER, OWNER})
    public Response getAllBuildings() throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollback = false;
        List<BuildingDto> buildings = new ArrayList<>();

        do {
            try {
                buildings = buildingManager.getAllBuildings()
                    .stream()
                    .map(BuildingDtoConverter::mapBuildingToDto)
                    .toList();
                rollback = buildingManager.isLastTransactionRollback();
            } catch (AppTransactionRolledBackException atrbe) {
                rollback = true;
            }
        } while (rollback && --txLimit > 0);

        if (rollback && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.ok(buildings).build();
    }

    @GET
    @Path("/{id}/reports")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, MANAGER, OWNER})
    public Response getBuildingReports(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{id}/places")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getBuildingPlaces(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{id}/reports/{year}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER, OWNER})
    public Response getBuildingReportByYear(@PathParam("id") Long id, @PathParam("year") Long year)
        throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response createBuilding()
        throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
