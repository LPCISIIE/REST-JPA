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

    @PUT
    @Path("/{id}")
    @Secured({AccountRole.CUSTOMER})
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
    @Path("/{id}/buy")
    @Secured({AccountRole.CUSTOMER})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response payOrder(@Context SecurityContext securityContext, @PathParam("id") String id, @PathParam("fidelite") String fidelite) {
        Shipment shipment = orderResource.findById(id);
        
        if(shipment == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        
        //gérer le cas ou le client possède la carte fidélité
        //on utilise le string fidelite, qui faut soit oui ou non, si oui
        //et montant fidelite supérieur à 30 points, le sandwich le plus cher est gratuit
        if(fidelite.equals("oui")) {
            //il faudra ajouter la condition nécessitant 30 points dans la carte (placée face cachée)
            shipment.setPrice(shipment.getPrice() - shipment.getHighestOrderSandwich());
        }
        else {
            //on gagne un nombre de points égal à 50% du prix de la commande
            //ex : si on paye une commande de 30€, on gagne 15 points
        }
        
        //on met la commande en paid
        if(orderResource.update(shipment, "Paid") == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        
        return Response.ok(shipment, MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("/turnover")
    @Secured({AccountRole.ADMIN})
    public Response getTurnover(@Context SecurityContext securityContext) {
        List<Shipment> list = orderResource.findAll();
        double turnover = 0.0;
        for(int i=0;i<list.size();i++) {
            turnover += list.get(i).getPrice();
        }
        return Response.ok(turnover, MediaType.APPLICATION_JSON).build();
    }
    
    @PUT
    @Path("/modify")
    @Secured({AccountRole.ADMIN})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response modifyOrderState(@Context SecurityContext securityContext, @FormParam("orderId") String orderId, @FormParam("state") String orderState) {
        Shipment shipment = orderResource.findById(orderId);
        
        if(shipment == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        
        if(orderResource.update(shipment,orderState) == null) 
            return Response.status(Response.Status.NOT_FOUND).build();
        
        return Response.ok(shipment, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Secured({AccountRole.CUSTOMER, AccountRole.ADMIN})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
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

    private String getUriForSelfIngredient(UriInfo uriInfo, Ingredient ingredient) {
        return uriInfo.getBaseUriBuilder()
                .path(IngredientRepresentation.class)
                .path("id/" + ingredient.getId())
                .build()
                .toString();
    }
}
