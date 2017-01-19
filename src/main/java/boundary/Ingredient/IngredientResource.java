package boundary.Ingredient;

import boundary.Category.CategoryResource;
import entity.Category;
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
        return entityManager.createNamedQuery("Ingredient.findAll", Ingredient.class)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }

    /**
     * Method that returns ingredients for a name given
     * @param name of the ingredient we're looking for
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
     * @param ingredient to add
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
     * Method that delete an ingredient
     * @param ingredientId
     * @return if it's deleted
     */
    public boolean delete(String ingredientId) {
        Ingredient ingredient = entityManager.find(Ingredient.class, ingredientId);

        if (ingredient != null) {
            entityManager.remove(ingredient);
            return true;
        }

        return false;
    }

    /**
     * Method that creates fake insertions into the database
     */
    public void feedCatalog(){
        if (!done) {
            Category category = categoryResource.insert(new Category("Salade"));

            this.insert(new Ingredient(category,"Laitue",1.00, "A salad with a french name"));
            this.insert(new Ingredient(category,"Roquette",1.00, "A salad with a french name"));
            this.insert(new Ingredient(category,"Mache",1.00, "A salad with a french name"));

            category = categoryResource.insert(new Category("Crudité"));

            this.insert(new Ingredient(category,"Carotte",1.50, "A carrot with a french name"));
            this.insert(new Ingredient(category,"Concombre",1.50, "A cucumber with a french name"));
            this.insert(new Ingredient(category,"Tomate",1.50, "A tomato with a french name"));

            category = categoryResource.insert(new Category("Charcuterie"));

            this.insert(new Ingredient(category,"Jambon",2.50, "Vegan people would hate you !"));
            this.insert(new Ingredient(category,"Jambon cru",2.50, "Vegan people would hate you !"));

            category = categoryResource.insert(new Category("Viande"));

            this.insert(new Ingredient(category,"Burger",3.00, "Vegan people would hate you !"));
            this.insert(new Ingredient(category,"Confit",3.00, "Vegan people would hate you !"));

            category = categoryResource.insert(new Category("Fromage"));

            this.insert(new Ingredient(category,"Emmental",1.50, "Is it swiss or french ?"));
            this.insert(new Ingredient(category,"Comté",1.50, "For strong people like you!"));

            category = categoryResource.insert(new Category("Sauce"));

            this.insert(new Ingredient(category,"Vinaigrette",0.50, "At first it was wine but something weird happened"));
            this.insert(new Ingredient(category,"Moutarde",0.50, "You don't choose mustard it's it that chooses you"));

            done = true;
        }
    }

}
