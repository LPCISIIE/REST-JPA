package boundary;

import entity.Ingredient;

import javax.ejb.Stateless;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    /**
     * Method that insert an ingredient into the database
     * @param ingredient to add
     * @return the ingredient added
     */
    public Ingredient insert(Ingredient ingredient) {
        ingredient.setId(UUID.randomUUID().toString());
        return this.entityManager.merge(ingredient);
    }

    /**
     * Method that creates fake insert into the database
     */
    public void feedCatalog(){
        this.insert(new Ingredient("Laitue",1.00));
        this.insert(new Ingredient("Roquette",1.00));
        this.insert(new Ingredient("Mache",1.00));
        this.insert(new Ingredient("Carotte",1.50));
        this.insert(new Ingredient("Concombre",1.50));
        this.insert(new Ingredient("Tomate",1.50));
        this.insert(new Ingredient("Jambon",2.50));
        this.insert(new Ingredient("Jambon cru",2.50));
        this.insert(new Ingredient("Burger",3.00));
        this.insert(new Ingredient("Confit",3.00));
        this.insert(new Ingredient("Emmental",1.50));
        this.insert(new Ingredient("Comté",1.50));
        this.insert(new Ingredient("Vinaigrette",0.50));
        this.insert(new Ingredient("Moutarde",0.50));
    }

}
