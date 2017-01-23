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


@Path("/orders")

@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class OrderRepresentation {

    @EJB
    OrderResource orderResource;

    @EJB
    AccountResource accountResource;


    //@Secured({AccountRole.ADMIN})
    @GET
    public Response getAll(@Context UriInfo uriInfo){
        List<Shipment> list = orderResource.findAll();
        list.stream().forEach(Commande -> {
            List<Sandwich> sandwiches = Commande.getSandwiches();
            Commande.addLink(this.getUriForSelfShipment(uriInfo,Commande),"self");
            for (Sandwich sandwich : sandwiches) {
                sandwich.getLinks().clear();
                sandwich.addLink(this.getUriForSelfSandwich(uriInfo,sandwich), "self");
            }
            Commande.setSandwiches(sandwiches);
        });

        GenericEntity<List<Shipment>> listGenericEntity = new GenericEntity<List<Shipment>>(list){};
        return Response.ok(listGenericEntity, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/add")
    @Secured({AccountRole.CUSTOMER})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response add(UriInfo uriInfo, ContainerRequestContext requestContext, @FormParam("date") String dateTime, @FormParam("sandwichId") String sandwichId) {
        Account account = accountResource.findByToken(requestContext);

        if (account == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Shipment Commande = orderResource.insert(account, dateTime, sandwichId);

        if (Commande == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        Commande.addLink(getUriForSelfShipment(uriInfo, Commande), "self");
        return Response.ok(Commande, MediaType.APPLICATION_JSON).build();
    }


    private String getUriForSelfShipment(UriInfo uriInfo, Shipment Commande) {
        return uriInfo.getBaseUriBuilder()
                .path(Shipment.class)
                .path("id/" + Commande.getId())
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
