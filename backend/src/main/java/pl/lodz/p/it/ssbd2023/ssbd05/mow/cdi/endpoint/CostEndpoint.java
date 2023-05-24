package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;

@RequestScoped
@Path("/costs")
@DenyAll
public class CostEndpoint {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("MANAGER")
    public Response getAllCosts() throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("MANAGER")
    public Response createCost() throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("MANAGER")
    public Response getCostDetails(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("MANAGER")
    public Response removeCost(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
