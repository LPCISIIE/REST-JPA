package boundary.BackOffice;

import boundary.Account.AccountResource;
import boundary.Order.OrderResource;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import control.PasswordManagement;
import entity.Account;
import entity.AccountRole;
import entity.Shipment;
import provider.Secured;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Stateless
@Path("/admin")
@Api(value = "/admin", description = "Backoffice : the admin dashboard")
public class BackOfficeRepresentation {

    @EJB
    AccountResource accountResource;

    @EJB
    OrderResource orderResource;

    @PUT
    @Path("/order_status")
    @Secured({AccountRole.ADMIN})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ApiOperation(value = "Edit the status of an order", notes = "Access : Admin only")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Not Found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response editOrderStatus(@FormParam("orderId") String orderId, @FormParam("status") int orderStatus) {
        Shipment shipment = orderResource.findById(orderId);

        if (shipment == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        if (orderResource.update(shipment,orderStatus) == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok().build();
    }

    @GET
    @Path("/dashboard")
    @Secured({AccountRole.ADMIN})
    @ApiOperation(value = "Get the amount of orders and the turnover", notes = "Access : Admin only")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response getTurnover(@Context SecurityContext securityContext) {
        List<Shipment> orders = orderResource.findAll();
        double turnover = 0.0;

        for (Shipment order : orders)
            turnover += order.getPrice();

        Map map = new HashMap();
        map.put("Turnover",turnover);
        map.put("Orders",orders.size());

        return Response.ok(map, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ApiOperation(value = "Create an admin account", notes = "Email address is unique")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Not Found"),
        @ApiResponse(code = 409, message = "Conflict : email address is already used"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response createAdmin(
            @FormParam("name") String name,
            @FormParam("email") String email,
            @FormParam("password") String password)
    {
        if ((name == null || email == null || password == null))
            return Response.status(Response.Status.NOT_FOUND).build();

        if (accountResource.findByEmail(email) != null)
            return Response.status(409)
                    .type("text/plain")
                    .entity("This email address is already used")
                    .build();

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
