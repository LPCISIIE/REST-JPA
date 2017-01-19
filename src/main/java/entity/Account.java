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
        @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a ORDER BY a.email DESC"),
        @NamedQuery(name = "Account.findByEmailAndPassword", query = "SELECT a FROM Account a WHERE a.email = :email AND a.password = :password"),
        @NamedQuery(name = "Account.countAll", query = "SELECT COUNT(a) FROM Account a")
})
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String email;

    private String name, password;

    private boolean hasVIPCard;

    private AccountRole role;

    /**
     * Empty constructor
     */
    public Account(){}

    /**
     * Constructor of an account
     * @param n the name
     * @param e the email address
     * @param p the password
     */
    public Account(String n, String e, String p) {
        this.name = n;
        this.email = e;
        this.password = p;
        this.hasVIPCard = false;
        this.role = AccountRole.CUSTOMER;
    }

    /**
     * - Getter and Setter functions -
     */

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

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }
}