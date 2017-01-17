package boundary.Account;


import control.KeyGenerator;
import entity.Account;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.mindrot.jbcrypt.BCrypt;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;


@Path("/authentication")
public class AuthenticationEndpoint {

    @Inject
    private KeyGenerator keyGenerator;

    @EJB
    private AccountResource accountResource;

    @Context
    private UriInfo uriInfo;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("email") String email, @FormParam("password") String password) {
        try {
            authenticate(email, password);
            return Response.ok().header(AUTHORIZATION, "Bearer " + issueToken(email)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /**
     * Authenticate against a LDAP
     * @param email
     * @param password
     * @throws NotAuthorizedException if the credentials are invalid
     */
    private void authenticate(String email, String password) throws NotAuthorizedException {
        Account account = accountResource.findByEmail(email);

        if (account == null && !BCrypt.checkpw(password,account.getPassword()))
            throw new NotAuthorizedException("Email address or password is invalid");
    }

    /**
     * Helper method that converts a Date for a localDateTime given
     * @param localDateTime to convert
     * @return Date
     */
    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Method that issues a JWT token
     * @param email of the associated user
     * @return the issued token
     */
    private String issueToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date())
                .setExpiration(toDate(LocalDateTime.now().plusMinutes(2L)))
                .signWith(SignatureAlgorithm.HS512, keyGenerator.generateKey())
                .compact();
    }
}