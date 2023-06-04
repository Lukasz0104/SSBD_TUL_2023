package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.constraints.NotBlank;
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
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ReportYearEntry;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.BuildingManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ReportManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.BuildingDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

import java.time.Year;
import java.util.Map;

@RequestScoped
@Path("/buildings")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class BuildingEndpoint {

    @Inject
    private BuildingManagerLocal buildingManager;

    @Inject
    private ReportManagerLocal reportManager;

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
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{id}/reports")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, MANAGER, OWNER})
    public Response getBuildingReports(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{id}/reports/{year}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER, OWNER})
    public Response getBuildingReportByYear(
        @PathParam("id") Long id,
        @PathParam("year") Long yearNum,
        @DefaultValue("all") @NotBlank @QueryParam("category") String category) throws AppBaseException {

        Year year = Year.of(Math.toIntExact(yearNum));
        Map<String, ReportYearEntry> report = reportManager.getBuildingReportByYear(id, year, category);
        return Response.ok(report).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response createBuilding() throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
