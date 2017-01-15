package boundary;

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
        Query query = entityManager.createNamedQuery("Category.findAll", Category.class);
        query.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH); // Solve cache issues
        return query.getResultList();
    }

    /**
     * Method that insert a category into the database
     * @param category to add
     * @return the category added
     */
    public Category insert(Category category) {
        category.setId(UUID.randomUUID().toString());
        return this.entityManager.merge(category);
    }


}
