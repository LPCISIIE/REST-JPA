package entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Entity
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Ingredient.findAll", query = "SELECT i FROM Ingredient i"),
        @NamedQuery(name = "Ingredient.findByName", query = "SELECT i FROM Ingredient i WHERE i.name = :name"),
})
public class Ingredient implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String name;
    private double price;
    private Category category;

    /**
     * Empty constructor
     */
    public Ingredient(){}

    /**
     *  Constructor of an ingredient
     * @param n the name
     * @param p the price
     */
    public Ingredient(Category c , String n, double p) {
        this.category = c;
        this.name = n;
        this.price = p;
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

    public Category getCategory(){
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}