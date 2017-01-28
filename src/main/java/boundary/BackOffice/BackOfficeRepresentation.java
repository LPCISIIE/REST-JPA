package boundary.BackOffice;

import boundary.Order.OrderResource;
import entity.AccountRole;
import entity.Shipment;
import provider.Secured;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Stateless
@Path("/admin")
public class BackOfficeRepresentation {

    @Context
    SecurityContext securityContext;

    @Context
    UriInfo uriInfo;

    @EJB
    OrderResource orderResource;


    @PUT
    @Path("/order_status")
    @Secured({AccountRole.ADMIN})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response modifyOrderStatus(@FormParam("orderId") String orderId, @FormParam("status") String orderStatus) {
        Shipment shipment = orderResource.findById(orderId);

        if(shipment == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        if(orderResource.update(shipment,orderStatus) == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok().build();
    }

    @GET
    @Path("/turnover")
    @Secured({AccountRole.ADMIN})
    public Response getTurnover(@Context SecurityContext securityContext) {
        List<Shipment> orders = orderResource.findAll();
        double turnover = 0.0;

        for (Shipment order : orders)
            turnover += order.getPrice();

        return Response.ok(turnover, MediaType.APPLICATION_JSON).build();
    }

}
