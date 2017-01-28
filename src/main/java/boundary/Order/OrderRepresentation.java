package boundary.Order;

import boundary.Account.AccountRepresentation;
import boundary.Account.AccountResource;
import boundary.Ingredient.IngredientRepresentation;
import boundary.Sandwich.SandwichRepresentation;
import entity.*;
import provider.Secured;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
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

    @Secured({AccountRole.ADMIN})
    @GET
    public Response getAll(){
        List<Shipment> list = orderResource.findAll();
        list.stream().forEach(order -> {
            List<Sandwich> sandwiches = order.getSandwiches();
            order.addLink(this.getUriForSelfShipment(uriInfo,order),"self");
            for (Sandwich sandwich : sandwiches) {
                sandwich.getLinks().clear();
                sandwich.addLink(this.getUriForSelfSandwich(uriInfo,sandwich), "self");
                for (Ingredient ingredient : sandwich.getIngredientsList()) {
                    ingredient.getLinks().clear();
                    ingredient.addLink(this.getUriForSelfIngredient(uriInfo,ingredient), "self");
                }
            }
            order.setSandwiches(sandwiches);
        });

        GenericEntity<List<Shipment>> listGenericEntity = new GenericEntity<List<Shipment>>(list){};
        return Response.ok(listGenericEntity, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        Shipment order = orderResource.findById(id);

        if (order == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        List<Sandwich> sandwiches = order.getSandwiches();
        order.addLink(this.getUriForSelfShipment(uriInfo,order),"self");
        for (Sandwich sandwich : sandwiches) {
            sandwich.getLinks().clear();
            sandwich.addLink(this.getUriForSelfSandwich(uriInfo,sandwich), "self");
            for (Ingredient ingredient : sandwich.getIngredientsList()) {
                ingredient.getLinks().clear();
                ingredient.addLink(this.getUriForSelfIngredient(uriInfo,ingredient), "self");
            }
        }

        order.setSandwiches(sandwiches);

        return Response.ok(order, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured({AccountRole.CUSTOMER, AccountRole.ADMIN})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response delete(@Context SecurityContext securityContext, @FormParam("sandwichId") String sandwich, @PathParam("id") String id) {
        Account account = accountResource.findByEmail(securityContext.getUserPrincipal().getName());

        if (account == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Shipment shipment = orderResource.findById(id);

        if (shipment == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        if (!account.getRole().equals(AccountRole.ADMIN) && !account.getEmail().equals(shipment.getCustomer().getEmail()) )
            return Response.status(Response.Status.UNAUTHORIZED).build();

        boolean isDeleted = orderResource.delete(shipment);

        if (isDeleted)
            return Response.ok().build();

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/edit_delivering")
    @Secured({AccountRole.CUSTOMER, AccountRole.ADMIN})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response editDate(@Context SecurityContext securityContext, @FormParam("date") String date, @FormParam("orderId") String id) {
         Account account = accountResource.findByEmail(securityContext.getUserPrincipal().getName());

        if (account == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Shipment shipment = orderResource.findById(id);

        if (shipment == null || date == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        if (!account.getRole().equals(AccountRole.ADMIN) && !account.getEmail().equals(shipment.getCustomer().getEmail()) )
            return Response.status(Response.Status.UNAUTHORIZED).build();

        if (orderResource.updateDate(shipment,date) == null)
            return Response.status(Response.Status.NOT_FOUND)
                    .type("text/plain")
                    .entity("Invalid date : should be in this format : '01/01/2018 21:30' " +
                            "and have to be in more than 10 minutes")
                    .build();

        return Response.ok().build();
    }

    @POST
    @Path("/remove_sandwich")
    @Secured({AccountRole.CUSTOMER, AccountRole.ADMIN})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response removeSandwich(@Context SecurityContext securityContext, @FormParam("sandwichId") String sandwich, @FormParam("orderId") String id) {
        Account account = accountResource.findByEmail(securityContext.getUserPrincipal().getName());

        if (account == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Shipment shipment = orderResource.findById(id);

        if (shipment == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        if (!account.getRole().equals(AccountRole.ADMIN) && !account.getEmail().equals(shipment.getCustomer().getEmail()) )
            return Response.status(Response.Status.UNAUTHORIZED).build();

       if (sandwich == null)
           return Response.status(Response.Status.NOT_FOUND).build();

        boolean isDeleted =  orderResource.removeSandwich(shipment,sandwich);

        if (isDeleted)
            return Response.ok().build();

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{id}")
    @Secured({AccountRole.CUSTOMER})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response update(@Context SecurityContext securityContext, @PathParam("id") String id,@FormParam("sandwichId") String sandwichId, @FormParam("size") String size ) {
        Account account = accountResource.findByEmail(securityContext.getUserPrincipal().getName());

        if (account == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Shipment shipment = orderResource.findById(id);

        if (sandwichId == null || size == null || shipment == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        if (!shipment.getCustomer().equals(account))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        if (orderResource.updateSize(shipment,sandwichId,size) != null)
            return Response.ok().build();

        return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    @PUT
    @Path("/{id}/process")
    @Secured({AccountRole.CUSTOMER})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response payOrder(@Context SecurityContext securityContext, @PathParam("id") String id, @FormParam("vipCard") String vipCard) {
        Shipment shipment = orderResource.findById(id);

        if(shipment == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        Account account = accountResource.findByEmail(securityContext.getUserPrincipal().getName());

        if (account == null || !shipment.getCustomer().equals(account))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        if (vipCard != null) {
            if (!account.hasVIPCard())
                return Response.status(Response.Status.NOT_FOUND)
                        .type("text/plain")
                        .entity("Supposed to use VIP Card but customer doesn't have one")
                        .build();

            if (account.canGetDiscount()) {
                shipment.applyDiscount();
                account.usePoints();
            } else {
                account.addPoints(shipment.getHigherPrice());
            }

        } else {
            if (account.hasVIPCard())
                account.addPoints(shipment.getHigherPrice());
        }

        if (orderResource.update(shipment, Shipment.ORDER_PAID) == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        
        return Response.ok(shipment, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Secured({AccountRole.CUSTOMER, AccountRole.ADMIN})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response add(
            @Context SecurityContext securityContext,
            @FormParam("dateTime") String dateTime,
            @FormParam("sandwichId") String sandwichId,
            @FormParam("sandwichId2") String sandwichId2,
            @FormParam("sandwichId3") String sandwichId3,
            @FormParam("sandwichId4") String sandwichId4
    ) {
        Account account = accountResource.findByEmail(securityContext.getUserPrincipal().getName());

        if (account == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Boolean isEmpty = (sandwichId == null && sandwichId2 == null && sandwichId3 == null && sandwichId4 == null );

        if (isEmpty)
            return Response.status(Response.Status.NOT_FOUND).build();

        ArrayList<String> sandwiches = new ArrayList<>();

        sandwiches.add(sandwichId);
        if (sandwichId2 != null)
            sandwiches.add(sandwichId2);
        if (sandwichId3 != null)
            sandwiches.add(sandwichId3);
        if (sandwichId4 != null)
            sandwiches.add(sandwichId4);

        Shipment shipment = orderResource.insert(account, dateTime, sandwiches);

        if (shipment == null)
            return Response.status(Response.Status.NOT_FOUND)
                    .type("text/plain")
                    .entity("Error : Sandwiches given do not exit or error with the date" +
                            " / Date should be in this format : '01/01/2018 21:30' " +
                            "and have to be in more than 10 minutes")
                    .build();

        shipment.addLink(getUriForSelfShipment(uriInfo, shipment), "self");
        return Response.ok(shipment, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Secured({AccountRole.ADMIN, AccountRole.CUSTOMER})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/add_sandwich")
    public Response addSandwich(@Context SecurityContext securityContext, @FormParam("orderId") String id, @FormParam("sandwichId") String sandwichId) {
        Account account = accountResource.findByEmail(securityContext.getUserPrincipal().getName());

        if (account == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Shipment shipment = orderResource.findById(id);

        if (shipment == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        if (account.getRole() != AccountRole.ADMIN && !account.getEmail().equals(shipment.getCustomer().getEmail()) )
            return Response.status(Response.Status.UNAUTHORIZED).build();

        if (orderResource.addSandwich(shipment, sandwichId) == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        shipment.getLinks().clear();
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

    private String getUriForSelfIngredient(UriInfo uriInfo, Ingredient ingredient) {
        return uriInfo.getBaseUriBuilder()
                .path(IngredientRepresentation.class)
                .path("id/" + ingredient.getId())
                .build()
                .toString();
    }
}
