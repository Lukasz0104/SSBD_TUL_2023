package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.ConstraintViolationException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.ExpiredTokenException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.InvalidTokenTypeException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.TokenNotFoundException;

@Provider
public class AppExceptionMapper implements ExceptionMapper<AppBaseException> {
    @Override
    public Response toResponse(AppBaseException e) {
        if (e instanceof ConstraintViolationException cve) {
            return Response.status(Response.Status.CONFLICT).build();
        } else if (e instanceof TokenNotFoundException tnfe) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else if (e instanceof ExpiredTokenException ete) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else if (e instanceof InvalidTokenTypeException itte) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
