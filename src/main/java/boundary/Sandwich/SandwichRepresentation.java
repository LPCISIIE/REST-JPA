package boundary.Sandwich;

import boundary.Category.CategoryResource;
import boundary.Ingredient.IngredientRepresentation;
import boundary.Ingredient.IngredientResource;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import control.DatabaseSeeder;
import entity.Account;
import entity.AccountRole;
import entity.Ingredient;
import entity.Sandwich;
import provider.Secured;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

@Path("/sandwiches")

@Produces(MediaType.APPLICATION_JSON)
@Stateless
@Api(value = "/sandwiches", description = "Sandwiches management")
public class SandwichRepresentation {

    @EJB
    SandwichResource sandwichResource;

    @EJB
    CategoryResource categoryResource;

    @EJB
    IngredientResource ingredientResource;

    @GET
    @ApiOperation(value = "Get all the sandwiches", notes = "Access : Guest, Customer and Admin")
    @ApiResponses(value = {
	    @ApiResponse(code = 200, message = "OK"),
	    @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response getSandwiches(@Context UriInfo uriInfo) {
        DatabaseSeeder.feedCatalog(ingredientResource, categoryResource, sandwichResource);
        List<Sandwich> list = sandwichResource.findAll();

        list.stream().forEach(sandwich -> {
            List<Ingredient> ingredientsList = sandwich.getIngredientsList();
            sandwich.addLink(this.getUriForSelfSandwich(uriInfo, sandwich), "self");
            for (Ingredient ingredient : ingredientsList) {
                ingredient.getLinks().clear();
                ingredient.addLink(this.getUriForSelfIngredient(uriInfo, ingredient), "self");
            }
            sandwich.setIngredientsList(ingredientsList);
        });

        GenericEntity<List<Sandwich>> listGenericEntity = new GenericEntity<List<Sandwich>>(list) {
        };

        return Response.ok(listGenericEntity, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Get a sandwich by its id", notes = "Access : Guest, Customer and Admin")
    @ApiResponses(value = {
	    @ApiResponse(code = 200, message = "OK"),
	    @ApiResponse(code = 404, message = "Not Found"),
	    @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response getSandwich(@Context UriInfo uriInfo, @PathParam("id") String sandwichId) {
        Sandwich sandwich = sandwichResource.findById(sandwichId);

        if (sandwich == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        List<Ingredient> ingredientsList = sandwich.getIngredientsList();

        for (Ingredient ingredient : ingredientsList) {
            ingredient.getLinks().clear();
            ingredient.addLink(this.getUriForSelfIngredient(uriInfo, ingredient), "self");
        }

        sandwich.setIngredientsList(ingredientsList);

        return Response.ok(sandwich, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ApiOperation(value = "Create a sandwich", notes = "Access : Guest, Customer and Admin")
    @ApiResponses(value = {
	@ApiResponse(code = 201, message = "Created"),
	@ApiResponse(code = 400, message = "Bad request (missing data)"),
	@ApiResponse(code = 500, message = "Internal server error")})
    public Response add (
            @Context UriInfo uriInfo,
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

        Sandwich sandwich = new Sandwich();
        if (extra == null && extra2 == null && extra3 == null) {
            sandwich = sandwichResource.insert(size, bread, meat, coldMeats, cheese, salad, crudite, sauce);
            if (sandwich == null)
                return Response.status(Response.Status.EXPECTATION_FAILED).build();
        } else if (extra != null && extra2 == null && extra3 == null) {
            sandwich = sandwichResource.insert(size, bread, meat, coldMeats, cheese, salad, crudite, sauce, extra);
            if (sandwich == null)
                return Response.status(Response.Status.EXPECTATION_FAILED).build();
        } else if (extra != null && extra2 != null && extra3 == null) {
            sandwich = sandwichResource.insert(size, bread, meat, coldMeats, cheese, salad, crudite, sauce, extra, extra2);
            if (sandwich == null)
                return Response.status(Response.Status.EXPECTATION_FAILED).build();
        } else if (extra != null && extra2 != null && extra3 != null) {
            sandwich = sandwichResource.insert(size, bread, meat, coldMeats, cheese, salad, crudite, sauce, extra, extra2, extra3);
            if (sandwich == null)
                return Response.status(Response.Status.BAD_REQUEST).build();
        }

        File file = new File(getUriForSelfSandwich(uriInfo,sandwich));

        return Response.created(file.toURI()).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ApiOperation(value = "Edit a sandwich", notes = "Access : Admin only")
    @ApiResponses(value = {
	@ApiResponse(code = 204, message = "Not content"),
	@ApiResponse(code = 304, message = "Not Modified"),
	@ApiResponse(code = 500, message = "Internal server error")})
    @Secured({AccountRole.ADMIN})
    public Response editSandwich(
            @PathParam("id") String sandwichId,
            @FormParam("name") String name,
            @FormParam("description") String description,
            @FormParam("size") String size
    ) {

        Sandwich sandwich = sandwichResource.findById(sandwichId);

        boolean isFormEmpty = (name == null && description == null && size == null);

        if (sandwich == null || isFormEmpty)
            return Response.notModified().build();

        String n = (name == null) ? sandwich.getName() : name;
        String d = (description == null) ? sandwich.getDescription() : description;

        if (size != null) {
            if (!Sandwich.isSizeOk(size))
                return Response.notModified().build();
        } else {
            size = sandwich.getSize();
        }

        if (sandwichResource.update(sandwich.update(size, n, d)) == null)
            return Response.notModified().build();

        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "Delete a sandwich by its id", notes = "Access : Admin only")
    @ApiResponses(value = {
	@ApiResponse(code = 204, message = "No content"),
	@ApiResponse(code = 404, message = "Not Found"),
	@ApiResponse(code = 500, message = "Internal server error")})
    @Secured({AccountRole.ADMIN})
    public Response deleteIngredient(@PathParam("id") String sandwichId) {
        if (sandwichResource.delete(sandwichId))
            return Response.status(204).build();
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
