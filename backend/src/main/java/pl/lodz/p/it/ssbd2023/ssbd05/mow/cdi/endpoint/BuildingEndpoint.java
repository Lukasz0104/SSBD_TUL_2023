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
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.BuildingManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.BuildingDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.PlaceDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

@RequestScoped
@Path("/buildings")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class BuildingEndpoint {

    @Inject
    private BuildingManagerLocal buildingManager;

    @Inject
    private RollbackUtils rollbackUtils;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, MANAGER, OWNER})
    public Response getAllBuildings() throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(() -> buildingManager.getAllBuildings()
            .stream()
            .map(BuildingDtoConverter::mapBuildingToDto)
            .toList(), buildingManager).build();
    }

    @GET
    @Path("/{id}/places")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getBuildingPlaces(@PathParam("id") Long id) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
                () -> buildingManager.getBuildingPlaces(id)
                    .stream()
                    .map(PlaceDtoConverter::createPlaceDtoFromPlace)
                    .toList(),
                buildingManager)
            .build();
    }

}
