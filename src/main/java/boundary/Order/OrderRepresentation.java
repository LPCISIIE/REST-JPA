package boundary.Order;

import boundary.Account.AccountRepresentation;
import boundary.Account.AccountResource;
import boundary.Sandwich.SandwichRepresentation;
import entity.Account;
import entity.AccountRole;
import entity.Shipment;
import entity.Sandwich;
import provider.Secured;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;
import java.util.List;



@Produces(MediaType.APPLICATION_JSON)
@Stateless
@Path("/orders")
public class OrderRepresentation {

    @Context
    SecurityContext securityContext;

    @Context
    UriInfo uriInfo;

    @EJB
    OrderResource orderResource;

    @EJB
    AccountResource accountResource;

    //@Secured({AccountRole.ADMIN})
    @GET
    public Response getAll(){
        List<Shipment> list = orderResource.findAll();
        list.stream().forEach(order -> {
            List<Sandwich> sandwiches = order.getSandwiches();
            order.addLink(this.getUriForSelfShipment(uriInfo,order),"self");
            for (Sandwich sandwich : sandwiches) {
                sandwich.getLinks().clear();
                sandwich.addLink(this.getUriForSelfSandwich(uriInfo,sandwich), "self");
            }
            order.setSandwiches(sandwiches);
        });

        GenericEntity<List<Shipment>> listGenericEntity = new GenericEntity<List<Shipment>>(list){};
        return Response.ok(listGenericEntity, MediaType.APPLICATION_JSON).build();
    }


    @GET
    @Path("/{id}")
    public Response add(@PathParam("id") String id) {

        Shipment shipment = orderResource.findById(id);

        if (shipment == null)
            return Response.status(Response.Status.NOT_FOUND).build();


        return Response.ok(shipment, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Secured({AccountRole.CUSTOMER})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/add")
    public Response add(@Context SecurityContext securityContext, @FormParam("dateTime") String dateTime, @FormParam("sandwichId") String sandwichId) {
        Account account = accountResource.findByEmail(securityContext.getUserPrincipal().getName());

        if (account == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Shipment shipment = orderResource.insert(account, dateTime, sandwichId);

        if (shipment == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        shipment.addLink(getUriForSelfShipment(uriInfo, shipment), "self");
        return Response.ok(shipment, MediaType.APPLICATION_JSON).build();

    }


    private String getUriForSelfShipment(UriInfo uriInfo, Shipment Commande) {
        return uriInfo.getBaseUriBuilder()
                .path(OrderRepresentation.class)
                .path(Commande.getId())
                .build()
                .toString();
    }

    private String getUriForShipment(UriInfo uriInfo) {
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
