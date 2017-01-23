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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.security.Key;
import java.security.Principal;


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

            requestContext.setSecurityContext(new SecurityContext() {

                @Override
                public Principal getUserPrincipal() {

                    return new Principal() {

                        @Override
                        public String getName() {
                            return email;
                        }
                    };
                }

                @Override
                public boolean isUserInRole(String role) {
                    return false; // too lazy to do it (tbh deadline is soon)
                }

                @Override
                public boolean isSecure() {
                    return false; // too lazy to do it (tbh deadline is soon)
                }

                @Override
                public String getAuthenticationScheme() {
                    return null; // too lazy to do it (tbh deadline is soon)
                }
            });

        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
