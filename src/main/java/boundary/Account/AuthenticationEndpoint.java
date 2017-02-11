package boundary.Account;


import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import control.KeyGenerator;
import entity.Account;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.mindrot.jbcrypt.BCrypt;
import provider.Secured;

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
@Api(value = "/authentication", description = "Route to login")
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
    @ApiOperation(value = "Authentication for users", notes = "Token given available for 5 minutes")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response authenticateUser(@FormParam("email") String email, @FormParam("password") String password) {
        try {
            authenticate(email, password);
            return Response.ok().header(AUTHORIZATION, "Bearer " + issueToken(email)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).type("text/plain").entity("Invalid credentials").build();
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
            throw new SecurityException("Email address or password is invalid");
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
                .setExpiration(toDate(LocalDateTime.now().plusMinutes(10L)))
                .signWith(SignatureAlgorithm.HS512, keyGenerator.generateKey())
                .compact();
    }
}