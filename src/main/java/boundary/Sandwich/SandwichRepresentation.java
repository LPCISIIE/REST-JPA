package boundary.Sandwich;

import entity.Sandwich;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/sandwichs")

@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class SandwichRepresentation {

    @EJB
    SandwichResource sandwichResource;

    @GET
    public Response getSandwichs() {
        GenericEntity<List<Sandwich>> list = new GenericEntity<List<Sandwich>>(sandwichResource.findAll()){};
        return Response.ok(list, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/id/{sandwichId}")
    public Response getSandwich(@PathParam("sandwichId") String sandwichId) {
        Sandwich sandwich = sandwichResource.findById(sandwichId);
        if (sandwich != null)
            return Response.ok(sandwich, MediaType.APPLICATION_JSON).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    
    @PUT
    @Path("/add") 
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response add (
            @FormParam("salade") String salade,
            @FormParam("crudite") String crudite,
            @FormParam("charcuterie") String charcuterie,
            @FormParam("fromage") String fromage,
            @FormParam("sauce") String sauce,
            @FormParam("viande") String viande
    ) {

        if (sandwichResource.insert(salade,crudite,charcuterie,fromage,sauce,viande) == null)
            return Response.status(Response.Status.EXPECTATION_FAILED).build();

       return Response.ok().build();
    }


}
