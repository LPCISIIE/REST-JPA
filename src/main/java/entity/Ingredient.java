

@Entity
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Ingredient.findAll", query = "SELECT i FROM Ingredient i"),
        @NamedQuery(name="Ingredient.findByName", query="SELECT i FROM Ingredient i WHERE i.name = :name"),
})
public class Ingredient implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id private String id;
    private String name;
    private double price;

    /**
     * Empty constructor
     */
    public Ingredient(){}

    /**
     *  Constructor of an ingredient
     * @param n the name
     * @param p the price
     */
    public Ingredient(String n, double p) {
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

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}