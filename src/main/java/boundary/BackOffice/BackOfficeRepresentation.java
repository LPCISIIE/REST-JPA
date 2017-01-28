package boundary.BackOffice;

import boundary.Account.AccountResource;
import boundary.Order.OrderResource;
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
public class BackOfficeRepresentation {

    @EJB
    AccountResource accountResource;

    @EJB
    OrderResource orderResource;

    @PUT
    @Path("/order_status")
    @Secured({AccountRole.ADMIN})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response editOrderStatus(@FormParam("orderId") String orderId, @FormParam("status") String orderStatus) {
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
