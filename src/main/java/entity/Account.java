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
        @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a"),
})
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String email;

    private String name, address, password;

    private boolean hasVIPCard;

    /**
     * Empty constructor
     */
    public Account(){}

    /**
     * Constructor of an account
     * @param e the email address
     * @param a the address
     * @param p the password
     */
    public Account(String n, String e, String a, String p) {
        this.name = n;
        this.email = e;
        this.address = a;
        this.password = p;
        this.hasVIPCard = false;
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

    public String getPassword(){
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean hasVIPCard() {
        return hasVIPCard;
    }

    public void setVIPCard(boolean answer){
        hasVIPCard = answer;
    }
}