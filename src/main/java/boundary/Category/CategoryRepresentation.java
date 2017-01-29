package boundary.Category;

import boundary.Ingredient.IngredientResource;
import entity.Category;
import entity.Ingredient;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/categories")

@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class CategoryRepresentation {

    @EJB
    IngredientResource ingredientResource;

    @EJB
    CategoryResource categoryResource;

    @GET
    public Response getCategories() {
        GenericEntity<List<Category>> list = new GenericEntity<List<Category>>(categoryResource.findAll()){};
        return Response.ok(list, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/id/{id}")
    public Response getCategory(@PathParam("id") String id) {
        Category category = categoryResource.findById(id);
        if (category != null)
            return Response.ok(category, MediaType.APPLICATION_JSON).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/name/{name}")
    public Response getCategoryByName(@PathParam("name") String categoryName) {
        List<Category> categories = categoryResource.findByName(categoryName);

        if (categories.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).build();
        
        GenericEntity<List<Category>> list = new GenericEntity<List<Category>>(categories){};
        return Response.ok(list, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/ingredients/{id}")
    public Response getCategoryIngredients(@PathParam("id") String categoryId) {
        Category cat = categoryResource.findById(categoryId);
        
        if (cat == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        
        List<Ingredient> ingredients = ingredientResource.findByCategory(cat.getId());
        if(ingredients.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).build();
            
        GenericEntity<List<Ingredient>> list = new GenericEntity<List<Ingredient>>(ingredients){};
        return Response.ok(list, MediaType.APPLICATION_JSON).build();
    }

}
