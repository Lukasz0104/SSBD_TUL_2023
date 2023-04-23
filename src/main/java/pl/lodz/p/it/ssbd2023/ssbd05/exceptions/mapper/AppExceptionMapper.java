package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBadRequestException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;

@Provider
public class AppExceptionMapper implements ExceptionMapper<AppBaseException> {
    @Override
    public Response toResponse(AppBaseException e) {
        if (e instanceof AppConflictException ace) {
            return Response.status(Response.Status.CONFLICT).build();
        } else if (e instanceof AppBadRequestException abre) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
