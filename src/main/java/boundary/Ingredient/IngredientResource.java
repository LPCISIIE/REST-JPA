package boundary.Ingredient;

import boundary.Category.CategoryResource;
import entity.Ingredient;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Stateless
public class IngredientResource {

    @PersistenceContext
    EntityManager entityManager;

    @EJB
    CategoryResource categoryResource;

    // To feed the database
    boolean done = false;

    /**
     * Method that returns an ingredient for an id given
     * @param id ID of the ingredient
     * @return Ingredient
     */
    public Ingredient findById(String id) {
        return entityManager.find(Ingredient.class, id);
    }

    /**
     * Method that returns all the ingredients
     * @return List of Ingredient
     */
    public List<Ingredient> findAll() {
        return entityManager.createNamedQuery("Ingredient.findAll", Ingredient.class)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }

    /**
     * Method that returns ingredients for a name given
     * @param name Name of the ingredient we're looking for
     * @return List of Ingredient
     */
    public List<Ingredient> findByName(String name){
        return entityManager.createQuery("SELECT i FROM Ingredient i where i.name = :name ")
                .setParameter("name", name)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }
    
    public List<Ingredient> findByCategory(String category) {
        return entityManager.createQuery("SELECT i from Ingredient i where i.category = :category ")
                .setParameter("category", category)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }

    /**
     * Method that inserts an ingredient into the database
     * @param ingredient The ingredient to add
     * @return the ingredient added or null if the Category doesn't exist
     */
    public Ingredient insert(Ingredient ingredient) {
        ingredient.setId(UUID.randomUUID().toString());

        if (categoryResource.findById(ingredient.getCategory().getId()) != null)
            return entityManager.merge(ingredient);

        return null;
    }

    /**
     * Method that updates an ingredient
     * @param ingredient to update
     * @return the new ingredient
     */
    public Ingredient update(Ingredient ingredient) {
        return entityManager.merge(ingredient);
    }

    /**
     * Method that deletes an ingredient
     * @param id ID of the ingredient
     * @return if it's deleted
     */
    public boolean delete(String id) {
        Ingredient ingredient = entityManager.find(Ingredient.class, id);

        if (ingredient != null) {
            entityManager.remove(ingredient);
            return true;
        }

        return false;
    }

}
