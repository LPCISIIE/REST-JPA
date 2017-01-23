package boundary.Account;

import control.PasswordManagement;
import entity.Account;
import entity.AccountRole;
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
    @Path("/email/{email}")
    public Response get(@PathParam("email") String email) {
        Account account = accountResource.findByEmail(email);
        if (account != null)
            return Response.ok(account, MediaType.APPLICATION_JSON).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Secured({AccountRole.ADMIN})
    public Response getAll(){
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

    @POST
    @Path("/admin-signup")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createAdmin(
            @FormParam("name") String name,
            @FormParam("email") String email,
            @FormParam("password") String password)
    {
        if ((name == null || email == null || password == null))
            return Response.status(Response.Status.NOT_FOUND).build();

        try {
            Account admin = new Account(name,email, PasswordManagement.digestPassword(password));
            admin.setRole(AccountRole.ADMIN);
            accountResource.insert(admin);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }


}
