package boundary.Sandwich;

import boundary.Ingredient.IngredientResource;
import entity.Category;
import entity.Sandwich;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Stateless
public class SandwichResource {

    @PersistenceContext
    EntityManager entityManager;

    @EJB
    IngredientResource ingResource;

    /**
     * Method that returns a sandwich for an id given
     * @param id
     * @return Sandwich
     */
    public Sandwich findById(String id) {
        return entityManager.find(Sandwich.class, id);
    }

    /**
     * Method that returns all the sandwiches
     * @return List of Sandwich
     */
    public List<Sandwich> findAll(){
        return entityManager.createNamedQuery("Sandwich.findAll", Sandwich.class)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }

    /**
     * Method that inserts a sandwich into the database
     * @param sandwich to add
     * @return the sandwich added or null if the Ingredient doesn't exist
     */
    public Sandwich insert(Sandwich sandwich) {
        sandwich.setId(UUID.randomUUID().toString());
        if (
            ingResource.findById(sandwich.getSalades()) != null &&
            ingResource.findById(sandwich.getCharcuterie()) != null &&
            ingResource.findById(sandwich.getCrudite()) != null &&
            ingResource.findById(sandwich.getFromage()) != null &&
            ingResource.findById(sandwich.getSauce()) != null &&
            ingResource.findById(sandwich.getViande()) != null
        )   return entityManager.merge(sandwich);
        return null; 
    }

     /**
      * Method that updates an sandwich
      * @param sandwich to update
      * @return the new sandwich
      */
     public Sandwich update(Sandwich sandwich) {
         return entityManager.merge(sandwich);
     }

     /**
      * Method that deletes an sandwich
      * @param sandwichId
      * @return if it's deleted
      */
      public boolean delete(String sandwichId) {
          Sandwich sandwich = entityManager.find(Sandwich.class, sandwichId);

          if (sandwich != null) {
              entityManager.remove(sandwich);
              return true;
          }

          return false;
      }
}
