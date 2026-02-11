package br.com.codenest.marknow.exception;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

public abstract class BaseExceptionMapper {

    @Context
    UriInfo uriInfo;

    @Context
    ContainerRequestContext requestContext;

    protected String path() {
        if (uriInfo == null || uriInfo.getRequestUri() == null) return "";
        String p = uriInfo.getRequestUri().getPath();
        String q = uriInfo.getRequestUri().getQuery();
        return (q == null || q.isBlank()) ? p : p + "?" + q;
    }

    protected String correlationId() {
        Object cid = requestContext != null ? requestContext.getProperty(CorrelationIdFilter.PROP) : null;
        return cid != null ? cid.toString() : null;
    }
}
