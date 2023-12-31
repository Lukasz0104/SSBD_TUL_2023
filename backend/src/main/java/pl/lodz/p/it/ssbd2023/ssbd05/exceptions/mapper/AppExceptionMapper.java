package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBadRequestException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppForbiddenException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppUnauthorizedException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.dto.MessageDTO;

import java.util.Map;

@Provider
public class AppExceptionMapper implements ExceptionMapper<AppBaseException> {
    @Override
    public Response toResponse(AppBaseException appBaseException) {

        Map<Class<?>, Response.Status> map = Map.of(
            AppConflictException.class, Response.Status.CONFLICT,
            AppBadRequestException.class, Response.Status.BAD_REQUEST,
            AppUnauthorizedException.class, Response.Status.UNAUTHORIZED,
            AppNotFoundException.class, Response.Status.NOT_FOUND,
            AppForbiddenException.class, Response.Status.FORBIDDEN);
        return Response.status(
                map.getOrDefault(getSuper(appBaseException.getClass()),
                    Response.Status.INTERNAL_SERVER_ERROR))
            .entity(new MessageDTO(appBaseException.getMessage()))
            .build();
    }

    private Class<?> getSuper(Class<?> e) {
        return e.getSuperclass().equals(AppBaseException.class) ? e : getSuper(e.getSuperclass());
    }
}
