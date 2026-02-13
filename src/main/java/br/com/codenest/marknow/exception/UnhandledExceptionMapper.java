package br.com.codenest.marknow.exception;

import br.com.codenest.marknow.dto.exception.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.List;

@Provider
public class UnhandledExceptionMapper extends BaseExceptionMapper
        implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(UnhandledExceptionMapper.class);

    @Override
    public Response toResponse(Throwable ex) {
        String cid = correlationId();
        LOG.errorf(ex, "Unhandled error. correlationId=%s path=%s", cid, path());

        ErrorResponse body = ErrorResponse.of(
                500,
                "Internal Server Error",
                ErrorCode.INTERNAL_ERROR,
                "Erro interno inesperado",
                path(),
                cid,
                List.of()
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}