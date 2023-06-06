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
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.InvalidDateFormatException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.BuildingManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ReportManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.BuildingDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.PlaceDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.ReportDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

import java.time.DateTimeException;
import java.time.Month;
import java.time.Year;

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
        return rollbackUtils.rollBackTXBasicWithOkStatus(
                () -> buildingManager.getBuildingPlaces(id)
                    .stream()
                    .map(PlaceDtoConverter::createPlaceDtoFromPlace)
                    .toList(),
                buildingManager)
            .build();
    }

    @GET
    @Path("/{id}/reports")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public Response getYearsAndMonthsForReports(@PathParam("id") Long id) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> ReportDtoConverter.mapToListOfBuildingReports(reportManager.getYearsAndMonthsForReports(id)),
            buildingManager
        ).build();
    }

    @GET
    @Path("/{id}/reports/{year}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public Response getBuildingYearReport(@PathParam("id") Long id, @PathParam("year") Long yearNum)
        throws AppBaseException {
        Year year;
        try {
            year = Year.of(Math.toIntExact(yearNum));
        } catch (DateTimeException e) {
            throw new InvalidDateFormatException();
        }

        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> ReportDtoConverter.mapToBuildingReportYearlyDto(reportManager.getBuildingReportByYear(id, year)),
            buildingManager
        ).build();
    }

    @GET
    @Path("/{id}/reports/{year}/{month}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public Response getBuildingMonthReport(
        @PathParam("id") Long id, @PathParam("year") Long yearNum,
        @PathParam("month") Long monthNum) throws AppBaseException {
        Year year;
        Month month;
        try {
            year = Year.of(Math.toIntExact(yearNum));
            month = Month.of(Math.toIntExact(monthNum));
        } catch (DateTimeException e) {
            throw new InvalidDateFormatException();
        }

        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> ReportDtoConverter.mapToBuildingReportYearlyDto(
                reportManager.getBuildingReportByYearAndMonth(id, year, month)),
            buildingManager
        ).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response createBuilding() throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
