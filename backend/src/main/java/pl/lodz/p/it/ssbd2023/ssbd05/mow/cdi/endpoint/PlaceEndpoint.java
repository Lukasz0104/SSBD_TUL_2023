package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request.AddCategoryDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.PlaceManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.MeterDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.PlaceDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

@RequestScoped
@Path("/places")
@DenyAll
@Interceptors({LoggerInterceptor.class})
public class PlaceEndpoint {

    @Inject
    private PlaceManagerLocal placeManager;

    @Context
    private SecurityContext securityContext;

    @Inject
    private RollbackUtils rollbackUtils;

    @GET
    @RolesAllowed(MANAGER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPlaces() throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> PlaceDtoConverter.createPlaceDtoList(placeManager.getAllPlaces()),
            placeManager
        ).build();
    }

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(OWNER)
    public Response getOwnPlaces() throws AppBaseException {
        String login = securityContext.getUserPrincipal().getName();
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> PlaceDtoConverter.createPlaceDtoList(placeManager.getOwnPlaces(login)),
            placeManager).build();
    }

    @GET
    @Path("/me/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({OWNER})
    public Response getPlaceDetailsAsOwner(@PathParam("id") Long id) throws AppBaseException {
        String login = securityContext.getUserPrincipal().getName();
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> PlaceDtoConverter.createPlaceDtoFromPlace(placeManager.getPlaceDetailsAsOwner(id, login)),
            placeManager
        ).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER})
    public Response getPlaceDetailsAsManager(@PathParam("id") Long id) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> PlaceDtoConverter.createPlaceDtoFromPlace(placeManager.getPlaceDetailsAsManager(id)),
            placeManager
        ).build();
    }

    @GET
    @Path("/{id}/rates")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({OWNER, MANAGER})
    public Response getPlaceRates(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{id}/reports")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({OWNER, MANAGER})
    public Response getPlaceReports(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/me/{id}/meters")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({OWNER})
    public Response getPlaceMetersAsOwner(@PathParam("id") Long id) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> MeterDtoConverter.createMeterDtoListFromMeterList(
                placeManager.getPlaceMetersAsOwner(id, securityContext.getUserPrincipal().getName())),
            placeManager
        ).build();
    }

    @GET
    @Path("/{id}/meters")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER})
    public Response getPlaceMetersAsManager(@PathParam("id") Long id) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> MeterDtoConverter.createMeterDtoListFromMeterList(
                placeManager.getPlaceMetersAsManager(id)),
            placeManager
        ).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response createPlace() throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{id}/owners")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getPlaceOwners(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/{id}/owners")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response addOwnerToPlace(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @DELETE
    @Path("/{id}/owners")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response removeOwnerFromPlace(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{id}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER})
    public Response getPlaceCategories(@NotNull @PathParam("id") Long id) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> PlaceDtoConverter.createPlaceCategoryDtoList(placeManager.getCurrentRatesFromPlace(id)),
            placeManager
        ).build();
    }

    @POST
    @Path("/add/category")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response addCategoryToPlace(@NotNull @Valid AddCategoryDto addCategoryDto) throws AppBaseException {
        return rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
            () -> placeManager.addCategoryToPlace(addCategoryDto.getPlaceId(), addCategoryDto.getCategoryId(),
                addCategoryDto.getNewReading()),
            placeManager
        ).build();
    }

    @GET
    @Path("/{id}/category/required_reading")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkIfReadingRequired(@PathParam("id") Long id, @QueryParam("categoryId") Long categoryId)
        throws AppBaseException {
        return Response.ok(placeManager.checkIfCategoryRequiresReading(id, categoryId)).build();
    }


    @DELETE
    @Path("/{id}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response removeCategoryFromPlace(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response editPlaceDetails(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
