package boundary.Sandwich;

import boundary.Ingredient.IngredientRepresentation;
import boundary.Ingredient.IngredientResource;
import entity.Ingredient;
import entity.Sandwich;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/sandwiches")

@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class SandwichRepresentation {

    @EJB
    SandwichResource sandwichResource;

    @GET
    public Response getSandwiches(@Context UriInfo uriInfo) {
        List<Sandwich> list = sandwichResource.findAll();

        list.stream().forEach(sandwich -> {
            List<Ingredient> ingredientsList = sandwich.getIngredientsList();
            sandwich.addLink(this.getUriForSelfSandwich(uriInfo,sandwich),"self");
            for (Ingredient ingredient : ingredientsList) {
                ingredient.getLinks().clear();
                ingredient.addLink(this.getUriForSelfIngredient(uriInfo,ingredient), "self");
            }
            sandwich.setIngredientsList(ingredientsList);
        });

        GenericEntity<List<Sandwich>> listGenericEntity = new GenericEntity<List<Sandwich>>(list) {};

        return Response.ok(listGenericEntity, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/id/{sandwichId}")
    public Response getSandwich(@Context UriInfo uriInfo, @PathParam("sandwichId") String sandwichId) {
        Sandwich sandwich = sandwichResource.findById(sandwichId);

        if (sandwich == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        List<Ingredient> ingredientsList = sandwich.getIngredientsList();

        for (Ingredient ingredient : ingredientsList) {
            ingredient.getLinks().clear();
            ingredient.addLink(this.getUriForSelfIngredient(uriInfo,ingredient), "self");
        }

        sandwich.setIngredientsList(ingredientsList);

        return Response.ok(sandwich, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/add") 
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response add (
            @FormParam("size") String size,
            @FormParam("bread") String bread,
            @FormParam("meat") String meat,
            @FormParam("cold_meats") String coldMeats,
            @FormParam("cheese") String cheese,
            @FormParam("salad") String salad,
            @FormParam("crudite") String crudite,
            @FormParam("sauce") String sauce,
            @FormParam("extra") String extra,
            @FormParam("extra2") String extra2,
            @FormParam("extra3") String extra3
    ) {

        boolean isEmpty = (size == null || meat == null || coldMeats == null || cheese == null || salad == null || sauce == null || crudite == null || bread == null);

        if (isEmpty)
            return Response.status(Response.Status.EXPECTATION_FAILED).build();

        if (extra == null && extra2 == null && extra3 == null) {
            if (sandwichResource.insert(size, bread, meat, coldMeats, cheese, salad, crudite, sauce) == null)
                return Response.status(Response.Status.EXPECTATION_FAILED).build();
        } else if (extra != null && extra2 == null && extra3 == null) {
             if (sandwichResource.insert(size, bread, meat, coldMeats, cheese, salad, crudite, sauce, extra) == null)
                 return Response.status(Response.Status.EXPECTATION_FAILED).build();
        } else if (extra != null && extra2 != null && extra3 == null) {
            if (sandwichResource.insert(size, bread, meat, coldMeats, cheese, salad, crudite, sauce, extra, extra2) == null)
                return Response.status(Response.Status.EXPECTATION_FAILED).build();
        } else if (extra != null && extra2 != null && extra3 != null) {
            if (sandwichResource.insert(size, bread, meat, coldMeats, cheese, salad, crudite, sauce, extra, extra2, extra3) == null)
                return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }

        return Response.ok().build();
    }

    @PUT
    @Path("/id/{sandwichId}")
    public Response editIngredient(
            @PathParam("sandwichId") String sandwichId,
            @FormParam("name") String name,
            @FormParam("description") String description,
            @FormParam("size") String size
    ) {

        Sandwich sandwich = sandwichResource.findById(sandwichId);

        boolean isFormEmpty = (name == null && description == null && size == null);

        if (sandwich == null || isFormEmpty)
            return Response.notModified().build();

        String n,d;

        n = (name == null) ? sandwich.getName() : name ;
        d = (description == null) ? sandwich.getDescription() : description ;

        if (size != null) {
            if (!Sandwich.isSizeOk(size))
                return Response.notModified().build();
        } else {
            size = sandwich.getSize();
        }

        if (sandwichResource.update(sandwich.update(size,n,d)) == null)
            return Response.notModified().build();

        return Response.ok().build();
    }

    @DELETE
    @Path("/id/{sandwichId}")
    public Response deleteIngredient(@PathParam("sandwichId") String sandwichId) {
        if (sandwichResource.delete(sandwichId))
            return Response.ok().build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

    private String getUriForSelfSandwich(UriInfo uriInfo, Sandwich sandwich) {
        return uriInfo.getBaseUriBuilder()
                .path(SandwichRepresentation.class)
                .path("id/" + sandwich.getId())
                .build()
                .toString();
    }

    private String getUriForSandwich(UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder()
                .path(SandwichRepresentation.class)
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

    private String getUriForIngredient(UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder()
                .path(IngredientRepresentation.class)
                .build()
                .toString();
    }
}
