package boundary.Account;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import control.PasswordManagement;
import entity.Account;
import entity.AccountRole;
import provider.Secured;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/accounts")
@Stateless
@Api(value = "/accounts", description = "Gestion des comptes")
@Produces(MediaType.APPLICATION_JSON)
public class AccountRepresentation {

    @EJB
    AccountResource accountResource;



    @GET
    @Secured({AccountRole.CUSTOMER})
    @Path("/create_card")
    @ApiOperation(value = "Création de la carte de fidélité",
            notes = "Accès: Client")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "Something wrong in Server")})

    public Response createCard(@Context SecurityContext securityContext) {
        Account account = accountResource.findByEmail(securityContext.getUserPrincipal().getName());

        if (account == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        if (account.hasVIPCard())
            return Response.status(Response.Status.FORBIDDEN)
                    .type("text/plain")
                    .entity("This customer has already a VIP Card with the amount : " + account.getVipCard() + "€ ")
                    .build();

        account.createCard();

        return Response.ok().build();
    }


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
