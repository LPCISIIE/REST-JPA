package boundary;

import entity.Ingredient;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/catalog")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class IngredientRepresentation {

    @EJB
    IngredientResource ingredientResource;

    @GET
    public Response getIngredients() {
        ingredientResource.feedCatalog();
        GenericEntity<List<Ingredient>> list = new GenericEntity<List<Ingredient>>(ingredientResource.findAll()){};
        return Response.ok(list, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/id/{ingredientId}")
    public Response getIngredient(@PathParam("ingredientId") String ingredientId) {
        ingredientResource.feedCatalog();
        Ingredient ingredient = ingredientResource.findById(ingredientId);
        if (ingredient != null)
            return Response.ok(ingredient, MediaType.APPLICATION_JSON).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/name/{ingredientName}")
    public Response getIngredientByName(@PathParam("ingredientName") String ingredientName) {
        ingredientResource.feedCatalog();
        List<Ingredient> ingredients = ingredientResource.findByName(ingredientName);

        if (ingredients.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).build();

        GenericEntity<List<Ingredient>> list = new GenericEntity<List<Ingredient>>(ingredients){};
        return Response.ok(list, MediaType.APPLICATION_JSON).build();

    }

    @DELETE
    @Path("/id/{ingredientId}")
    public Response deleteIngredient(@PathParam("ingredientId") String ingredientId) {
        if (ingredientResource.delete(ingredientId))
            return Response.ok().build();
        else
            return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/id")
    public Response add (
            @FormParam("name") String name,
            @FormParam("categoryId") String categoryId,
            @FormParam("price") double price,
            @FormParam("description") String description
    ) {
        return null;
    }


}
