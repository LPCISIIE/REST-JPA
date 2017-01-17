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
        @NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c"),
})
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String name;

    /**
     * Empty constructor
     */
    public Category(){}

    /**
     * Constructor of an ingredient
     * @param n the name
     */
    public Category(String n) {
        this.name = n;
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

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}