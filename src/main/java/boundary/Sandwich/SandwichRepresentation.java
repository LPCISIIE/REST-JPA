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
            @FormParam("salade") String salades,
            @FormParam("crudite") String crudites,
            @FormParam("charcuterie") String charcuterie,
            @FormParam("fromage") String fromage,
            @FormParam("sauce") String sauce,
            @FormParam("viande") String viande
    ) {
        if (sandwichResource.insert(new Sandwich(salades,crudites,charcuterie,fromage,sauce,viande)) == null)
            return Response.status(Response.Status.EXPECTATION_FAILED).build();

       return Response.ok().build();
    }
   

//    @POST
//    @Path("/id/{sandwichId}")
//    public Response editSandwich(
//            @PathParam("sandwichId") String sandwichId,
//            @FormParam("name") String name,
//            @FormParam("categoryId") String categoryId,
//            @FormParam("price") double price,
//            @FormParam("description") String description
//    ) {
//        Sandwich sandwich = sandwichResource.findById(sandwichId);
//
//        boolean isFormEmpty = (name == null && categoryId == null && Double.toString(price) == null && description == null);
//
//        if (sandwich == null || isFormEmpty)
//            return Response.notModified().build();
//
//        String c,n,d;
//        double p;
//
//        n = (name == null) ? sandwich.getName() : name ;
//        if (categoryId == null)
//            c = sandwich.getCategory() ;
//        else {
//            Category cat = categoryResource.findById(categoryId);
//            if (cat == null)
//                return Response.notModified().build();
//            else
//                c = cat.getId();
//        }
//
//        d = (description == null) ? sandwich.getDescription() : description ;
//        p = (Double.toString(price) == null) ? sandwich.getPrice() : price;
//
//
//        if (sandwichResource.update(sandwich.update(c,n,p,d)) == null)
//            return Response.notModified().build();
//
//
//        return Response.ok().build();
//    }

//    @DELETE
//    @Path("/id/{sandwichId}")
//    public Response deleteSandwich(@PathParam("sandwichId") String sandwichId) {
//        if (sandwichResource.delete(sandwichId))
//            return Response.ok().build();
//        else
//            return Response.status(Response.Status.NO_CONTENT).build();
//    }


//
//    @PUT
//    @Path("/bread/add")
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//    public Response addBread (
//            @FormParam("name") String name,
//            @FormParam("price") double price,
//            @FormParam("description") String description
//    ) {
//
//        if (name == null || Double.toString(price) == null || description == null)
//            return Response.status(Response.Status.EXPECTATION_FAILED).build();
//
//        List<Category> query = categoryResource.findByName("Pain");
//        Category breadCategory = (query.size() > 0) ? query.get(0) : categoryResource.insert(new Category("Pain"));
//        sandwichResource.insert(new Sandwich(breadCategory,name,price,description));
//
//        return Response.ok().build();
//    }
//


}
