package entity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    private String name, description;
    private double price;

    @ManyToOne
    private Category category;

    @XmlElement(name="_links")
    @Transient
    private List<Link> links = new ArrayList<>();

    /**
     * Empty constructor
     */
    public Ingredient() {}

    /**
     * Constructor of an ingredient
     *
     * @param c the category
     * @param n the name
     * @param p the price
     */
    public Ingredient(Category c, String n, double p, String d) {
        this.category = c;
        this.name = n;
        this.price = p;
        this.description = d;
    }

    /**
     * Method that updates an ingredient
     *
     * @param c the category
     * @param n the name
     * @param p the price
     */
    public Ingredient update(Category c, String n, double p, String d) {
        this.category = c;
        this.name = n;
        this.price = p;
        this.description = d;
        return this;
    }

    /**
     * Helper method
     *
     * @return the category name
     */
    public String categoryName() {
        return category.getName();
    }

    /**
     * Helper method
     *
     * @return the category id
     */
    public String categoryId() {
        return category.getId();
    }

    public void addLink(String uri, String rel) {
        this.links.add(new Link(rel, uri));
    }

    /**
     * - Getter and Setter functions -
     */

    public List<Link> getLinks() {
        return links;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}