package boundary;

import entity.Category;
import entity.Ingredient;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/catalog")

@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class IngredientRepresentation {

    @EJB
    IngredientResource ingredientResource;

    @EJB
    CategoryResource categoryResource;

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
    
    @GET
    @Path("/id/category/{id}")
    public Response getIngredientCategory(@PathParam("id") String ingredientId) {
        ingredientResource.feedCatalog();
        Ingredient ingredient = ingredientResource.findById(ingredientId);
        if (ingredient != null) {
            Category cat = categoryResource.findById(ingredient.getCategory());
            
            return Response.ok(cat, MediaType.APPLICATION_JSON).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/id/{ingredientId}")
    public Response editIngredient(
            @PathParam("ingredientId") String ingredientId,
            @FormParam("name") String name,
            @FormParam("categoryId") String categoryId,
            @FormParam("price") double price,
            @FormParam("description") String description
    ) {
        Ingredient ingredient = ingredientResource.findById(ingredientId);

        boolean isFormEmpty = (name == null && categoryId == null && Double.toString(price) == null && description == null);

        if (ingredient == null || isFormEmpty)
            return Response.notModified().build();

        String c,n,d;
        double p;

        n = (name == null) ? ingredient.getName() : name ;
        if (categoryId == null)
            c = ingredient.getCategory() ;
        else {
            Category cat = categoryResource.findById(categoryId);
            if (cat == null)
                return Response.notModified().build();
            else
                c = cat.getId();
        }

        d = (description == null) ? ingredient.getDescription() : description ;
        p = (Double.toString(price) == null) ? ingredient.getPrice() : price;


        if (ingredientResource.update(ingredient.update(c,n,p,d)) == null)
            return Response.notModified().build();


        return Response.ok().build();
    }

    @DELETE
    @Path("/id/{ingredientId}")
    public Response deleteIngredient(@PathParam("ingredientId") String ingredientId) {
        if (ingredientResource.delete(ingredientId))
            return Response.ok().build();
        else
            return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("/add")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response add (
            @FormParam("name") String name,
            @FormParam("categoryId") String categoryId,
            @FormParam("price") double price,
            @FormParam("description") String description
    ) {
        if (ingredientResource.insert(new Ingredient(categoryId,name,price,description)) == null)
            return Response.status(Response.Status.EXPECTATION_FAILED).build();

       return Response.ok().build();
    }

    @PUT
    @Path("/bread/add")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addBread (
            @FormParam("name") String name,
            @FormParam("price") double price,
            @FormParam("description") String description
    ) {

        if (name == null || Double.toString(price) == null || description == null)
            return Response.status(Response.Status.EXPECTATION_FAILED).build();

        List<Category> query = categoryResource.findByName("Pain");
        Category breadCategory = (query.size() > 0) ? query.get(0) : categoryResource.insert(new Category("Pain"));
        ingredientResource.insert(new Ingredient(breadCategory,name,price,description));

        return Response.ok().build();
    }



}
