package br.com.codenest.marknow.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.UUID;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class CorrelationIdFilter implements ContainerRequestFilter {

    public static final String HEADER = "X-Correlation-Id";
    public static final String PROP = "correlationId";

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String cid = ctx.getHeaderString(HEADER);
        if (cid == null || cid.isBlank()) cid = UUID.randomUUID().toString();
        ctx.setProperty(PROP, cid);
    }
}