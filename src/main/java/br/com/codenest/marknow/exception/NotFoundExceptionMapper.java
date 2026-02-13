package br.com.codenest.marknow.exception;

import br.com.codenest.marknow.dto.exception.ErrorResponse;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class NotFoundExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException ex) {
        ErrorResponse body = ErrorResponse.of(
                404,
                "Not Found",
                ErrorCode.NOT_FOUND,
                "Recurso não encontrado",
                path(),
                correlationId(),
                List.of()
        );

        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
