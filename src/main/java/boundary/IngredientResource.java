package boundary;

import entity.Ingredient;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class IngredientResource {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * Method that returns an ingredient for an id given
     * @param id
     * @return Ingredient of this id
     */
    public Ingredient findById(String id) {
        return entityManager.find(Ingredient.class, id);
    }

}
