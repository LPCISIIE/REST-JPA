package provider.Filter;

import control.KeyGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import provider.AuthenticatedAccount;
import provider.Secured;

import javax.annotation.Priority;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.security.Key;


@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Inject
    @AuthenticatedAccount
    Event<String> accountAuthenticatedEvent;

    @Override
    public void filter(ContainerRequestContext requestContext) throws NotAuthorizedException {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if ( authHeader == null || !authHeader.startsWith("Bearer "))
            throw new NotAuthorizedException("Authorization header must be provided");

        String token = authHeader.substring("Bearer".length()).trim();

        try {

            Key key = new KeyGenerator().generateKey();

            Jws<Claims> jwts = Jwts.parser().setSigningKey(key).parseClaimsJws(token);

            String email = jwts.getBody().getSubject();

            accountAuthenticatedEvent.fire(email);

        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }



}
