package br.com.codenest.marknow.exception;

import br.com.codenest.marknow.dto.exception.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class BusinessExceptionMapper extends BaseExceptionMapper
        implements ExceptionMapper<BusinessException> {

    private String reasonPhrase(int status) {
        Response.Status rs = Response.Status.fromStatusCode(status);
        if (rs != null) return rs.getReasonPhrase();
        // JAX-RS Response.Status doesn't include 422
        if (status == 422) return "Unprocessable Entity";
        return "Error";
    }


    @Override
    public Response toResponse(BusinessException ex) {
        int status = ex.getStatus();
        String error = reasonPhrase(status);

        ErrorResponse body = ErrorResponse.of(status, error, ex.getCode() != null ? ex.getCode() : "BUSINESS_ERROR",
                ex.getMessage(), path(), correlationId(), List.of());

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
