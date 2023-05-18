package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.mapper;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.MessageDTO;

@Provider
public class BeanValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        StringBuilder concat = new StringBuilder();
        exception.getConstraintViolations().forEach(
            cv -> {
                String path = cv.getPropertyPath().toString();
                String msg = path.substring(path.lastIndexOf('.') + 1) + ": " + cv.getMessage();
                concat.append(msg).append(";");
            }
        );

        return Response.status(Response.Status.BAD_REQUEST)
            .entity(new MessageDTO(concat.toString())).build();
    }
}
