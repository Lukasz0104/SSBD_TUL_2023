package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;

@RequestScoped
@Path("/meters")
@DenyAll
public class MeterEndpoint {
    @GET
    @Path("/{id}/readings")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("MANAGER")
    public Response getMeterReadings(@PathParam("id") Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
