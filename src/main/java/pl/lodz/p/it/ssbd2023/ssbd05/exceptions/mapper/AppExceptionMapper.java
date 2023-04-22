package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.ConstraintViolationException;

@Provider
public class AppExceptionMapper implements ExceptionMapper<AppBaseException> {
    @Override
    public Response toResponse(AppBaseException e) {

        if (e instanceof ConstraintViolationException cve) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
