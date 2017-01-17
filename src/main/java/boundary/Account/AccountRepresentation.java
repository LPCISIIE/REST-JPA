package boundary.Account;

import control.PasswordManagement;
import entity.Account;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/account")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class AccountRepresentation {

    @EJB
    AccountResource accountResource;


    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response signup(
            @FormParam("name") String name,
            @FormParam("email") String email,
            @FormParam("password") String password)
    {
        if ((name == null || email == null || password == null))
            return Response.status(Response.Status.NOT_FOUND).build();

        try {
            accountResource.insert(new Account(name,email, PasswordManagement.digestPassword(password)));
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }


}
