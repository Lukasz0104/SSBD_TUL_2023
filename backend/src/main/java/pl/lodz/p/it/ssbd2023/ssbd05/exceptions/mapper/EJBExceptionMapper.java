package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.mapper;

import jakarta.ejb.AccessLocalException;
import jakarta.ejb.EJBAccessException;
import jakarta.ejb.EJBException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.dto.MessageDTO;

import java.util.Map;

@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {
    @Override
    public Response toResponse(EJBException ejbException) {

        Map<Class<?>, Response.Status> map = Map.of(
            EJBAccessException.class, Response.Status.FORBIDDEN,
            AccessLocalException.class, Response.Status.FORBIDDEN);
        return Response.status(
                map.getOrDefault(getSuper(ejbException.getClass()),
                    Response.Status.INTERNAL_SERVER_ERROR))
            .entity(new MessageDTO(ejbException.getMessage()))
            .build();
    }

    private Class<?> getSuper(Class<?> e) {
        return e.getSuperclass().equals(EJBException.class) ? e : getSuper(e.getSuperclass());
    }
}
