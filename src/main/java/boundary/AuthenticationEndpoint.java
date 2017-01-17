package boundary;

import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;


@Path("/authentication")
public class AuthenticationEndpoint {

    @POST
    @Produces("application/json")
    @Consumes("application/x-www-form-urlencoded")
    public Response authenticateUser(
            @FormParam("email") String email,
            @FormParam("password") String password)
    {

        try {

            // Authenticate the user using the credentials provided
            authenticate(email, password);

            // Issue a token for the user
            String token = "a";//issueToken(email);

            // Return the token on the response
            return Response.ok(token).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    private void authenticate(String email, String password) throws Exception {
        // Authenticate against a database, LDAP, file or whatever
        // Throw an Exception if the credentials are invalid
    }

    /**private String issueToken(String username) {
        Key key = keyGenerator.generateKey();
        String jwtToken = Jwts.builder()
                .setSubject(login)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date())
                .setExpiration(toDate(LocalDateTime.now().plusMinutes(2L)))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        System.out.println("jwtToken + key = " + jwtToken+ " - " + key);
        return jwtToken;
    }*/
}