package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.mapper;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Logger;

@Provider
public class PayloadProcessingExceptionMapper implements ExceptionMapper<ProcessingException> {

    @Override
    public Response toResponse(ProcessingException e) {
        Logger logger = Logger.getLogger(PayloadProcessingExceptionMapper.class.getName());
        if (e.getMessage().contains("deserializ")) {
            logger.warning(e.getMessage() + " " + e.getCause().getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        throw e;
    }
}
