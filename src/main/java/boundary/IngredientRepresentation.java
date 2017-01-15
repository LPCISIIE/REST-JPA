package boundary;

import entity.Ingredient;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
        GenericEntity<List<Ingredient>> list = new GenericEntity<List<Ingredient>>(ingredientResource.findAll()){};
        return Response.ok(list, MediaType.APPLICATION_JSON).build();
    }

}
