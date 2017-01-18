package boundary.Account;

import control.PasswordManagement;
import entity.Account;
import provider.Secured;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/account")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class AccountRepresentation {

    @EJB
    AccountResource accountResource;

    @GET
    @Path("/all")
    @Secured
    public Response getAllAccounts(){
        GenericEntity<List<Account>> list = new GenericEntity<List<Account>>(accountResource.findAll()){};
        return Response.ok(list, MediaType.APPLICATION_JSON).build();
    }

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
