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
import jakarta.ws.rs.HeaderParam;
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
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.SignatureMismatchException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.EventLogInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request.AddCategoryDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request.CreatePlaceDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request.EditPlaceDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.PlaceManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.JwsProvider;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.MeterDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.PlaceDtoConverter;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.functionalinterfaces.FunctionThrows;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback.RollbackUtils;

@RequestScoped
@Path("/places")
@DenyAll
@Interceptors({EventLogInterceptor.class, LoggerInterceptor.class})
public class PlaceEndpoint {

    @Inject
    private PlaceManagerLocal placeManager;

    @Context
    private SecurityContext securityContext;

    @Inject
    private RollbackUtils rollbackUtils;

    @Inject
    private MeterDtoConverter meterDtoConverter;

    @Inject
    private JwsProvider jwsProvider;

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
        return getPlaceDetails(() -> placeManager.getPlaceDetailsAsOwner(id, login));
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({MANAGER})
    public Response getPlaceDetailsAsManager(@PathParam("id") Long id) throws AppBaseException {
        return getPlaceDetails(() -> placeManager.getPlaceDetailsAsManager(id));
    }

    private Response getPlaceDetails(FunctionThrows<Place> func) throws AppBaseException {
        PlaceDto dto = rollbackUtils.rollBackTXBasicWithReturnTypeT(
            () -> PlaceDtoConverter.createPlaceDtoFromPlace(func.apply()),
            placeManager
        );
        String ifMatch = jwsProvider.signPayload(dto.getSignableFields());
        return Response.ok(dto).header("ETag", ifMatch).build();
    }

    @GET
    @Path("/me/{id}/meters")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({OWNER})
    public Response getPlaceMetersAsOwner(@PathParam("id") Long id) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> meterDtoConverter.createMeterDtoListFromMeterList(
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
            () -> meterDtoConverter.createMeterDtoListFromMeterList(
                placeManager.getPlaceMetersAsManager(id)),
            placeManager
        ).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response createPlace(@NotNull @Valid CreatePlaceDTO dto) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithReturnNoContentStatus(
            () -> placeManager.createPlace(
                dto.placeNumber(),
                dto.squareFootage(),
                dto.residentsNumber(),
                dto.buildingId()),
            placeManager
        ).build();
    }

    @GET
    @Path("/{id}/owners")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getPlaceOwners(@PathParam("id") Long id) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
                () -> placeManager.getPlaceOwners(id)
                    .stream()
                    .map(PlaceDtoConverter::createPlaceOwnerDtoFromOwnerData)
                    .toList(),
                placeManager)
            .build();
    }

    @POST
    @Path("/{id}/owners")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response addOwnerToPlace(@PathParam("id") Long id, @NotNull @QueryParam("ownerId") Long ownerId)
        throws AppBaseException {
        String managerLogin = securityContext.getUserPrincipal().getName();
        return rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
            () -> placeManager.addOwnerToPlace(id, ownerId, managerLogin),
            placeManager
        ).build();
    }

    @GET
    @Path("/{id}/not-owners")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response getOwnerNotOwningPlace(@PathParam("id") Long id) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> placeManager.getOwnerNotOwningPlace(id)
                .stream()
                .map(PlaceDtoConverter::createPlaceOwnerDtoFromOwnerData)
                .toList(),
            placeManager
        ).build();
    }

    @DELETE
    @Path("/{id}/owners")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response removeOwnerFromPlace(@PathParam("id") Long id, @NotNull @QueryParam("ownerId") Long ownerId)
        throws AppBaseException {
        String managerLogin = securityContext.getUserPrincipal().getName();
        return rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
            () -> placeManager.removeOwnerFromPlace(id, ownerId, managerLogin),
            placeManager
        ).build();
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

    @GET
    @Path("/me/{id}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({OWNER})
    public Response getOwnPlaceRates(@NotNull @PathParam("id") Long id) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> PlaceDtoConverter.createPlaceCategoryDtoList(placeManager
                .getCurrentRatesFromOwnPlace(id, securityContext.getUserPrincipal().getName())),
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
                addCategoryDto.getNewReading(), securityContext.getUserPrincipal().getName()),
            placeManager
        ).build();
    }

    @GET
    @Path("/{id}/category/required_reading")
    @RolesAllowed(MANAGER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkIfReadingRequired(@PathParam("id") Long id, @QueryParam("categoryId") Long categoryId)
        throws AppBaseException {
        return Response.ok(placeManager.checkIfCategoryRequiresReading(id, categoryId)).build();
    }

    @GET
    @Path("/{id}/categories/missing")
    @RolesAllowed(MANAGER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMissingCategories(@PathParam("id") Long id) throws AppBaseException {
        return rollbackUtils.rollBackTXBasicWithOkStatus(
            () -> PlaceDtoConverter.createPlaceCategoryDtoList(placeManager.findCurrentRateByPlaceIdNotMatch(id)),
            placeManager
        ).build();
    }

    @DELETE
    @Path("/{id}/categories/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response removeCategoryFromPlace(@PathParam("id") Long id,
                                            @PathParam("categoryId") Long categoryId)
        throws AppBaseException {
        return rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
            () -> placeManager.removeCategoriesFromPlace(
                id,
                categoryId,
                securityContext.getUserPrincipal().getName()),
            placeManager).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MANAGER)
    public Response editPlaceDetails(@PathParam("id") Long id, @Valid @NotNull EditPlaceDto placeDto,
                                     @NotNull @HeaderParam("If-Match") String ifMatch)
        throws AppBaseException {
        if (!jwsProvider.verify(ifMatch, placeDto.getSignableFields())) {
            throw new SignatureMismatchException();
        }
        return rollbackUtils.rollBackTXWithOptimisticLockReturnNoContentStatus(
            () -> placeManager.editPlaceDetails(id,
                PlaceDtoConverter.mapPlaceFromEditDto(placeDto)),
            placeManager
        ).build();
    }
}
