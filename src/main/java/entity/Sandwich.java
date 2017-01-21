package entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
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

    private String name;

    private final static String SANDWICH_CUSTOM = "CUSTOM";

    @ManyToMany
    @JsonManagedReference
    List<Ingredient> ingredientsList;
    @XmlElement(name="_links")
    @Transient
    private List<Link> links = new ArrayList<>();

    /**
     * Empty constructor
     */
    public Sandwich() {
        name = SANDWICH_CUSTOM;
        ingredientsList = new ArrayList<>();
    }

    /**
     * Constructor of a sandwich
     * @param ingredients the ingredients in a sandwich
     */
    public Sandwich(Ingredient ... ingredients) {
        name = SANDWICH_CUSTOM;
        ingredientsList = new ArrayList<>();
        for (Ingredient ingredient : ingredients)
            ingredientsList.add(ingredient);

    }

    /**
     * Constructor of a sandwich
     * @param name the sandwich name
     * @param ingredients the ingredients in a sandwich
     */
    public Sandwich(String name, Ingredient ... ingredients) {
        this.name = name;
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
        String res = "Sandwich{" +
                "id='" + id + '\''
                + " Ingredients : [";
        for (Ingredient i: ingredientsList)
            res += i.getName() + " - " + i.getId() + '\'';
        res+= ']';

        return res;
    }

    public void addLink(String uri, String rel) {
        this.links.add(new Link(rel,uri));
    }

    /**
     * - Getter and Setter functions -
     */

    public List<Link> getLinks() {
        return links;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Ingredient> getIngredientsList() {
        return ingredientsList;
    }

    public void setIngredientsList(List<Ingredient> ingredientsList) {
        this.ingredientsList = new ArrayList<>(ingredientsList);
    }

    /**
     * Method that returns the ingredients which belongs the category given
     * @param category of the ingredient
     * @return List of Ingredient if it's found else null
     */
    public List<Ingredient> getIngredient(String category) {
        if (!ingredientsList.isEmpty()) {
            List<Ingredient> res = new ArrayList<>();
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
