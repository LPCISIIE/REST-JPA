package provider;

import javax.ws.rs.container.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@PreMatching
@Provider
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private final static String METHODS = "GET,POST,PUT,DELETE,OPTIONS,HEAD";
    private final static String HEADERS = "origin,content-type,accept,authorization";
    private final static int TIME = 5 * 60;

    @Override
    public void filter(ContainerRequestContext requestCtx) throws IOException {
        if (requestCtx.getRequest().getMethod().equals("OPTIONS"))
            requestCtx.abortWith(Response.status(Response.Status.OK).build());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        try {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
            responseContext.getHeaders().add("Access-Control-Allow-Headers", createHeader(requestContext));
            responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
            responseContext.getHeaders().add("Access-Control-Allow-Methods", METHODS);
            responseContext.getHeaders().add("Access-Control-Max-Age", TIME);
        } catch (Exception e) {
            throw new SecurityException();
        }
    }

    private String createHeader(ContainerRequestContext requestContext) {
        String headers = requestContext.getHeaderString("Access-Control-Request-Headers");
        return headers == null ? HEADERS : headers;
    }
}
