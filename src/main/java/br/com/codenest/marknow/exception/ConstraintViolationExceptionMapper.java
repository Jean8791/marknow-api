package br.com.codenest.marknow.exception;

import br.com.codenest.marknow.dto.exception.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper extends BaseExceptionMapper
        implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException ex) {
        List<ErrorResponse.FieldError> fields = ex.getConstraintViolations().stream()
                .map(this::toFieldError)
                .toList();

        ErrorResponse body = ErrorResponse.of(
                400,
                "Bad Request",
                ErrorCode.BAD_REQUEST,
                "Falha de validação",
                path(),
                correlationId(),
                fields
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }

    private ErrorResponse.FieldError toFieldError(ConstraintViolation<?> v) {
        String field = v.getPropertyPath() != null ? v.getPropertyPath().toString() : null;

        Object invalid = v.getInvalidValue();
        String rejectedValue = (invalid == null) ? null : String.valueOf(invalid);

        return new ErrorResponse.FieldError(field, v.getMessage(), rejectedValue);
    }

}
