package boundary.Order;

import boundary.Account.AccountRepresentation;
import boundary.Sandwich.SandwichRepresentation;
import entity.Account;
import entity.AccountRole;
import entity.Order;
import entity.Sandwich;
import provider.Secured;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class OrderRepresentation {

    @EJB
    OrderResource orderResource;

    @GET
    @Path("/all")
    //@Secured({AccountRole.ADMIN})
    public Response getAll(@Context UriInfo uriInfo){
        List<Order> list = orderResource.findAll();

        list.stream().forEach(order -> {
             List<Sandwich> sandwiches = order.getSandwiches();
             order.addLink(this.getUriForSelfOrder(uriInfo,order),"self");
             for (Sandwich sandwich : sandwiches) {
                 sandwich.getLinks().clear();
                 sandwich.addLink(this.getUriForSelfSandwich(uriInfo,sandwich), "self");
             }
             order.setSandwiches(sandwiches);
        });

        GenericEntity<List<Order>> listGenericEntity = new GenericEntity<List<Order>>(list) {};
        return Response.ok(listGenericEntity, MediaType.APPLICATION_JSON).build();
    }


    private String getUriForSelfOrder(UriInfo uriInfo, Order order) {
        return uriInfo.getBaseUriBuilder()
                .path(Order.class)
                .path("id/" + order.getId())
                .build()
                .toString();
    }

    private String getUriForOrder(UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder()
                .path(OrderRepresentation.class)
                .build()
                .toString();
    }

    private String getUriForSelfAccount(UriInfo uriInfo, Account account) {
        return uriInfo.getBaseUriBuilder()
                .path(AccountRepresentation.class)
                .path("email/" + account.getEmail())
                .build()
                .toString();
    }

    private String getUriForSelfSandwich(UriInfo uriInfo, Sandwich sandwich) {
        return uriInfo.getBaseUriBuilder()
                .path(SandwichRepresentation.class)
                .path("id/" + sandwich.getId())
                .build()
                .toString();
    }
}
