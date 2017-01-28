package boundary.Category;

import entity.Category;

import javax.ejb.Stateless;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

@Stateless
public class CategoryResource {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * Method that returns a category for an id given
     * @param id
     * @return Category
     */
    public Category findById(String id) {
        return entityManager.find(Category.class, id);
    }

    /**
     * Method that returns all the categories
     * @return List of Category
     */
    public List<Category> findAll(){
        return entityManager.createNamedQuery("Category.findAll", Category.class)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }

    /**
     * Method that returns categories for a name given
     * @param name of the category we're looking for
     * @return List of Category
     */
    public List<Category> findByName(String name){
        return entityManager.createQuery("SELECT c FROM Category c where c.name = :name ")
                .setParameter("name", name)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }

    /**
     * Method that inserts a category into the database
     * @param category to add
     * @return the category added
     */
    public Category insert(Category category) {
        category.setId(UUID.randomUUID().toString());
        return this.entityManager.merge(category);
    }


}
