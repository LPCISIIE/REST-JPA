package boundary.Category;

import boundary.Ingredient.IngredientRepresentation;
import boundary.Ingredient.IngredientResource;
import boundary.Sandwich.SandwichResource;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import control.DatabaseSeeder;
import entity.Category;
import entity.Ingredient;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

@Path("/categories")

@Produces(MediaType.APPLICATION_JSON)
@Stateless
@Api(value = "/categories", description = "Ingredient categories management")
public class CategoryRepresentation {

    @EJB
    IngredientResource ingredientResource;

    @EJB
    CategoryResource categoryResource;

    @EJB
    SandwichResource sandwichResource;

    @GET
    @ApiOperation(value = "Get all the categories", notes = "Access : Guest, Customer and Admin")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response getCategories() {
        DatabaseSeeder.feedCatalog(ingredientResource,categoryResource,sandwichResource);
        GenericEntity<List<Category>> list = new GenericEntity<List<Category>>(categoryResource.findAll()){};
        return Response.ok(list, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Get a category by its id", notes = "Access : Guest, Customer and Admin")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response getCategory(@PathParam("id") String id) {
        Category category = categoryResource.findById(id);
        if (category != null)
            return Response.ok(category, MediaType.APPLICATION_JSON).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/name/{name}")
    @ApiOperation(value = "Get categories by their name", notes = "Access : Guest, Customer and Admin - Can be one or many categories")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response getCategoryByName(@PathParam("name") String categoryName) {
        DatabaseSeeder.feedCatalog(ingredientResource,categoryResource,sandwichResource);

        List<Category> categories = categoryResource.findByName(categoryName);

        if (categories.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).build();

        GenericEntity<List<Category>> list = new GenericEntity<List<Category>>(categories){};
        return Response.ok(list, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/{id}/ingredients")
    @ApiOperation(value = "Get the ingredients of a category", notes = "Access : Guest, Customer and Admin")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response getCategoryIngredients(@Context UriInfo uriInfo, @PathParam("id") String categoryId) {

        if (categoryId == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        Category cat = categoryResource.findById(categoryId);

        if (cat == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        List<Ingredient> ingredients = ingredientResource.findByCategory(cat);
        if(ingredients.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).build();

        for (Ingredient ingredient : ingredients) {
           ingredient.getLinks().clear();
           ingredient.addLink(getUriForSelfIngredient(uriInfo, ingredient), "self");
        }

        GenericEntity<List<Ingredient>> list = new GenericEntity<List<Ingredient>>(ingredients){};
        return Response.ok(list, MediaType.APPLICATION_JSON).build();
    }

    private String getUriForSelfIngredient(UriInfo uriInfo, Ingredient ingredient) {
        return uriInfo.getBaseUriBuilder()
                .path(IngredientRepresentation.class)
                .path(ingredient.getId())
                .build()
                .toString();
    }
}
