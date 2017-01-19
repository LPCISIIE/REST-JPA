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
        @NamedQuery(name = "Sandwich.findAll", query = "SELECT i FROM Sandwich i")
})
public class Sandwich implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String salades, crudite, charcuterie, fromage, sauce, viande;

    /**
     * Empty constructor
     */
    public Sandwich(){}


    /**
     * Constructor of a sandwich
     * @param c the category
     * @param n the name
     * @param p the price
     */
    public Sandwich(String sal,String cr,String ch,String fr,String sau,String v) {
     this.salades=sal;
     this.crudite=cr;
     this.charcuterie=ch;
     this.fromage=fr;
     this.sauce=sau;
     this.viande=v;
    }

    /**
     * Method that updates a sandwich
     * @param c the category
     * @param n the name
     * @param p the price
     */
    public Sandwich update(String c , String n, double p, String d){
        return this;
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

    public String getSalades() {
        return salades;
    }

    public void setSalades(String salades) {
        this.salades = salades;
    }

    public String getCrudite() {
        return crudite;
    }

    public void setCrudite(String crudite) {
        this.crudite = crudite;
    }

    public String getCharcuterie() {
        return charcuterie;
    }

    public void setCharcuterie(String charcuterie) {
        this.charcuterie = charcuterie;
    }

    public String getFromage() {
        return fromage;
    }

    public void setFromage(String fromage) {
        this.fromage = fromage;
    }

    public String getSauce() {
        return sauce;
    }

    public void setSauce(String sauce) {
        this.sauce = sauce;
    }

    public String getViande() {
        return viande;
    }

    public void setViande(String viande) {
        this.viande = viande;
    }

}
