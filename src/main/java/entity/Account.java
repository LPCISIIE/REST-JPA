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

    private final static double NO_VIP_CARD = -1;

    @Id
    private String email;

    private String name, password;

    private double vipCard;

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
        this.vipCard = NO_VIP_CARD;
        this.role = AccountRole.CUSTOMER;
    }

    /**
     * Check if the customer has a VIP Card
     * @return boolean
     */
    public boolean hasVIPCard() {
        return (this.vipCard != NO_VIP_CARD);
    }

    /**
     * Method to create a VIP Card
     */
    public void createCard() {
        this.vipCard = 0;
    }

    /**
     * Method to know if a customer can get a discount card
     * @return boolean
     */
    public boolean canGetDiscount() {
         return vipCard >= 50;
    }

    /**
     * Method to add points on the vip card
     * @param price
     * @return
     */
    public boolean addPoints(Double price){
        if (!hasVIPCard())
            return false;

        vipCard+= price/3;
        return true;
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

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public double getVipCard() {
        return vipCard;
    }

    public void setVipCard(double vipCard) {
        this.vipCard = vipCard;
    }
}