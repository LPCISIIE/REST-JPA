package boundary;

import entity.Ingredient;

import javax.ejb.Stateless;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class IngredientResource {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * Method that returns an ingredient for an id given
     * @param id
     * @return Ingredient
     */
    public Ingredient findById(String id) {
        return entityManager.find(Ingredient.class, id);
    }

    /**
     * Method that returns all the ingredients
     * @return List of Ingredient
     */
    public List<Ingredient> findAll(){
        Query query = entityManager.createNamedQuery("Ingredient.findAll", Ingredient.class);
        query.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH); // Solve cache issues
        return query.getResultList();
    }

}
