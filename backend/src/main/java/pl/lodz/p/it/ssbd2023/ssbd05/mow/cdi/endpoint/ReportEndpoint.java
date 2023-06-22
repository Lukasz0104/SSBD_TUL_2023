package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.InvalidDateFormatException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ReportManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.ReportDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

import java.time.DateTimeException;
import java.time.Month;
import java.time.Year;

@RequestScoped
@Path("/reports")
@DenyAll
@Interceptors(LoggerInterceptor.class)
public class ReportEndpoint {

    @Inject
    private ReportManagerLocal reportManager;

    @Inject
    private RollbackUtils rollbackUtils;

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("community/{year}/{month}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getReportDetails(
        @PathParam("year") @NotNull Integer year,
        @PathParam("month") @Min(1) @Max(12) @NotNull Integer month) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> reportManager.getReportDetails(year, month),
            reportManager
        ).build();
    }

    @GET
    @Path("/community")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getAllCommunityReports() throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> reportManager.getAllCommunityReportsYearsAndMonths(),
            reportManager).build();
    }

    @GET
    @Path("/community/{year}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getCommunityReportByYear(@PathParam("year") Integer year) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> reportManager.getCommunityReportByYear(year),
            reportManager
        ).build();
    }

    @GET
    @Path("/place/{id}/report/year")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getYearlyReportsForPlace(@PathParam("id") Long id,
                                             @QueryParam("year") @NotNull @Min(2020) @Max(2999) Integer year)
        throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> ReportDtoConverter.createPlaceReportYearDtoFromReportPlaceForecastYear(
                reportManager.getAllReportsDataByPlaceAndYear(id, Year.of(year)), year),
            reportManager).build();
    }

    @GET
    @Path("/me/place/{id}/report/year")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(OWNER)
    public Response getOwnYearlyReportsForPlace(@PathParam("id") Long id,
                                                @QueryParam("year") @NotNull @Min(2020) @Max(2999) Integer year)
        throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> ReportDtoConverter.createPlaceReportYearDtoFromReportPlaceForecastYear(
                reportManager.getAllOwnReportsDataByPlaceAndYear(
                    id,
                    Year.of(year),
                    securityContext.getUserPrincipal().getName()),
                year),
            reportManager).build();
    }

    @GET
    @Path("/place/{id}/report/month")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getMonthlyReportsForPlace(@PathParam("id") Long id,
                                              @QueryParam("year") @Min(2020) @Max(2999) Integer year,
                                              @QueryParam("month") @Min(1) @Max(12) Integer month,
                                              @QueryParam("full") boolean full)
        throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> ReportDtoConverter.createPlaceReportMonthDtoFromReportPlaceForecastMonth(
                reportManager.getAllReportsDataByPlaceAndYearAndMonth(
                    id,
                    Year.of(year),
                    Month.of(month),
                    full),
                month,
                year,
                full),
            reportManager
        ).build();
    }

    @GET
    @Path("/me/place/{id}/report/month")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(OWNER)
    public Response getOwnMonthlyReportsForPlace(@PathParam("id") Long id,
                                                 @QueryParam("year") @Min(2020) @Max(2999) Integer year,
                                                 @QueryParam("month") @Min(1) @Max(12) Integer month,
                                                 @QueryParam("full") boolean full)
        throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> ReportDtoConverter.createPlaceReportMonthDtoFromReportPlaceForecastMonth(
                reportManager.getAllOwnReportsDataByPlaceAndYearAndMonth(
                    id,
                    Year.of(year),
                    Month.of(month),
                    securityContext.getUserPrincipal().getName(),
                    full),
                month,
                year,
                full),
            reportManager
        ).build();
    }

    @GET
    @Path("/place/{id}/is-report")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response isReportForPlace(@PathParam("id") Long id,
                                     @QueryParam("year") @NotNull @Min(2020) @Max(2999) Integer year) {
        return Response.ok(reportManager.isReportForYear(Year.of(year), id)).build();
    }

    @GET
    @Path("/me/place/{id}/is-report")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(OWNER)
    public Response isOwnReportForPlace(@PathParam("id") Long id,
                                        @QueryParam("year") @Min(2020) @Max(2999) Integer year)
        throws AppBaseException {
        return Response.ok(
            reportManager.isOwnReportForYear(Year.of(year), id, securityContext.getUserPrincipal().getName())).build();
    }


    @GET
    @Path("/buildings/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public Response getYearsAndMonthsForReports(@PathParam("id") Long id) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> ReportDtoConverter.mapToBuildingYearsAndMonthsReports(
                reportManager.getYearsAndMonthsForReports(id)),
            reportManager
        ).build();
    }

    @GET
    @Path("/buildings/{id}/{year}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public Response getYearlyReportForBuilding(@PathParam("id") Long id,
                                               @PathParam("year") @Min(2020) @Max(2999) Integer yearNum)
        throws AppBaseException {
        Year year;
        try {
            year = Year.of(yearNum);
        } catch (DateTimeException e) {
            throw new InvalidDateFormatException();
        }

        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> reportManager.getYearlyReportForBuilding(id, year),
            reportManager
        ).build();
    }

    @GET
    @Path("/buildings/{id}/{year}/{month}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public Response getMonthlyReportForBuilding(
        @PathParam("id") Long id,
        @PathParam("year") @Min(2020) @Max(2999) Integer yearNum,
        @PathParam("month") @Min(1) @Max(12) Integer monthNum) throws AppBaseException {
        Year year;
        Month month;
        try {
            year = Year.of(yearNum);
            month = Month.of(monthNum);
        } catch (DateTimeException e) {
            throw new InvalidDateFormatException();
        }

        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> reportManager.getMonthlyReportForBuilding(id, year, month),
            reportManager
        ).build();
    }
}
