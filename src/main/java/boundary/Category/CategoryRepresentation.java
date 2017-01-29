package boundary.Category;

import boundary.Ingredient.IngredientResource;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
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
@Api(value = "/categories", description = "Gestion des catégories d'aliments")
public class CategoryRepresentation {

    @EJB
    IngredientResource ingredientResource;

    @EJB
    CategoryResource categoryResource;


    @GET
    @ApiOperation(value = "Récupération de toutes les catégories existantes",
         notes = "Accès: Client, Admin")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response getCategories() {
        GenericEntity<List<Category>> list = new GenericEntity<List<Category>>(categoryResource.findAll()){};
        return Response.ok(list, MediaType.APPLICATION_JSON).build();
    }


    @GET
    @Path("/id/{id}")
    @ApiOperation(value = "Recupération d'une catégorie par son id",
         notes = "Accès: Client, Admin")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response getCategory(@PathParam("id") String id) {
        Category categorie = categoryResource.findById(id);
        if (categorie != null)
            return Response.ok(categorie, MediaType.APPLICATION_JSON).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/name/{name}")
    @ApiOperation(value = "Recupération d'une catégorie par son nom",
         notes = "Accès: Client, Admin")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found"),
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response getCategoryByName(@PathParam("name") String categoryName) {
        List<Category> categories = categoryResource.findByName(categoryName);

        if (categories.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).build();
        
        GenericEntity<List<Category>> list = new GenericEntity<List<Category>>(categories){};
        return Response.ok(list, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/ingredients/{id}")
    @ApiOperation(value = "Recupération des ingrédients d'une catégorie",
         notes = "Accès: Client, Admin")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found"),
        @ApiResponse(code = 500, message = "Internal server error")})
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
