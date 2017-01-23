package boundary.Sandwich;

import boundary.Ingredient.IngredientResource;
import entity.Category;
import entity.Ingredient;
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
    IngredientResource ingredientResource;

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

        for (Ingredient ingredient : sandwich.getIngredientsList()) {
            if (ingredientResource.findById(ingredient.getId()) == null)
                return null;
        }

        sandwich.calculatePrice();

        if (sandwich.validate())
            return entityManager.merge(sandwich);

        return null;
    }

    /**
     * Method that inserts a sandwich into the database
     * @param ingredients of the sandwich to add
     * @return the sandwich added or null if the size or Ingredient doesn't exist
     */
    public Sandwich insert(String size, String ... ingredients) {
        Ingredient array[] = new Ingredient[ingredients.length];
        Ingredient i;
        int counter = 0;

        if ( !size.equals(Sandwich.getSandwichSize0()) && !size.equals(Sandwich.getSandwichSize1()) &&
             !size.equals(Sandwich.getSandwichSize2()) && !size.equals(Sandwich.getSandwichSize3())
           )
            return null;



        for (String ingredient : ingredients) {
            i = ingredientResource.findById(ingredient);
            if (i == null)
                return null;

            array[counter++]=i;
        }

        if (array[0] == null)
            return null;


        Sandwich sandwich = new Sandwich(size,array);
        if (sandwich.validate()) {
            sandwich.setId(UUID.randomUUID().toString());
            sandwich.calculatePrice();
            return entityManager.merge(sandwich);
        }

        return null;
    }

     /**
      * Method that updates a sandwich
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
