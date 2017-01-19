package entity;

import boundary.Category.CategoryResource;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.ejb.EJB;
import javax.persistence.*;
import javax.ws.rs.core.GenericEntity;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Sandwich.findAll", query = "SELECT i FROM Sandwich i")
})
public class Sandwich implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @ManyToMany
    @JsonManagedReference
    List<Ingredient> ingredientsList; // Have to convert it into GenericEntity ==> Expected ':' instead of '}'

    /**
     * Empty constructor
     */
    public Sandwich(){}

    /**
     * Constructor of a sandwich
     * @param ingredients the ingredients in a sandwich
     */
    public Sandwich(Ingredient ... ingredients) {
        ingredientsList = new ArrayList<>();
        for (Ingredient ingredient : ingredients)
            ingredientsList.add(ingredient);

    }

    /**
     * Method that updates a sandwich
     * @param ingredients the ingredients in a sandwich
     * @return the sandiwch
     */
    public Sandwich update(Ingredient ... ingredients){
        ingredientsList.clear();
        for (Ingredient ingredient : ingredients)
            ingredientsList.add(ingredient);
        return this;
    }

    @Override
    public String toString() {
        return "Sandwich{" +
                "id='" + id + '\'' +
                ", ingredientsList=" + ingredientsList +
                '}';
    }

    /**
     * - Getter and Setter functions -
     */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Ingredient> getIngredientList() {
        return ingredientsList;
    }

    public void setIngredientsList(List<Ingredient> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    /**
     * Method that returns the ingredients which belongs the category given
     * @param category of the ingredient
     * @return List of Ingredient if it's found else null
     */
    public List<Ingredient> getIngredient(String category) {
        if (!ingredientsList.isEmpty()) {
            List<Ingredient> res = new ArrayList<>();
            String name;
            for (Ingredient ingredient : ingredientsList) {
                if (ingredient.categoryName().equals(category))
                    ingredientsList.add(ingredient);
            }

            if (!res.isEmpty())
                return res;
        }
            return null;
    }

}
