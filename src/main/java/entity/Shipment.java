package entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Entity
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Shipment.findAll", query = "SELECT s FROM Shipment s")
})
public class Shipment implements Serializable {
// NOT ORDER because JAVA is so stupid it makes a SQL error with the name Order ... thanks Oracle

    private static final long serialVersionUID = 1L;

    public final static String ORDER_CREATED = "Created";
    public final static String ORDER_PAID = "Paid";
    public final static String ORDER_IN_PROCESS = "In process";
    public final static String ORDER_READY = "Available for pickup";
    public final static String ORDER_DELIVERED = "Order delivered";

    private String dateTime;
    private String status;
    private double price;

    @Id
    private String id;

    @ManyToOne
    @JsonManagedReference
    private Account customer;

    @ManyToMany
    @JsonManagedReference
    private List<Sandwich> sandwiches = new ArrayList<>();

    @XmlElement(name="_links")
    @Transient
    private List<Link> links = new ArrayList<>();

    public Shipment(){
        this.price = 0;
        this.status = ORDER_CREATED;
    }

    public Shipment(Account customer, String dateTime, List<Sandwich> sandwiches) {
        this.price = 0;
        this.customer = customer;
        this.dateTime = dateTime;
        this.sandwiches = sandwiches;
        this.status = ORDER_CREATED;

        if (!sandwiches.isEmpty()) {
            for (Sandwich sandwich : sandwiches) {
                this.price += sandwich.getPrice();
            }
        }
    }

    /**
     * Method to add a sandwich to the order
     * @param sandwich to add
     */
    public void addSandwich(Sandwich sandwich) {
        sandwiches.add(sandwich);
        price+= sandwich.getPrice();
    }

    /**
     * Method to remove a sandwich from the order
     * @param id of the Sandwich to remove
     * @return boolean : if the sandwich has been removed
     */
    public boolean removeSandwich(String id) {
        for (Sandwich sandwich : sandwiches) {
            if (sandwich.getId().equals(id)) {
                sandwiches.remove(sandwich);
                price-= sandwich.getPrice();
                return true;
            }
        }
        return false;
    }

    /**
     * Method to change the status of an order
     * @param status
     * @return is the status changed
     */
    public boolean changeState(String status) {
        if (!isStatusOk(status))
            return false;
        this.status = status;
        return true;
    }

    /**
     * Method to get the higher price in our sandwiches
     * @return the higher price
     */
    public double getHigherPrice() {
        double price = 0;
        for (Sandwich sandwich : sandwiches)
            if (sandwich.getPrice() > price)
                price = sandwich.getPrice();
        return price;
    }


    /**
     * Method to apply discount on the order
     */
    public void applyDiscount(){
        if (price - getHigherPrice() < 0)
            price = 0;
        else
            price -= getHigherPrice();

    }

    /**
     * Helper function that converts a String into a Date
     * @param s the String in the format 'dd/MM/yyyy HH:mm'
     * @return the Date if it's ok else null
     */
    public Date toDate(String s) {
        Date date = null;
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        inputDateFormat.setTimeZone(TimeZone.getDefault());
        Date dateMax = Date.from(LocalDateTime
                        .now()
                        .plusMinutes(10)
                        .atZone(ZoneId.systemDefault())
                        .toInstant());


        
        String dateString = inputDateFormat.format(dateMax);

        try {
            date = inputDateFormat.parse(s);
            dateMax = inputDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
           if (!(date.compareTo(dateMax) > 0))
                date = null;
        }

        return date;
    }

    /**
     * Helper function to know if the status given exists
     * @param status of the order
     * @return if it exists
     */
    public static boolean isStatusOk(String status) {
        return status.equals(ORDER_CREATED) || status.equals(ORDER_PAID) ||
               status.equals(ORDER_IN_PROCESS) || status.equals(ORDER_READY) ||
               status.equals(ORDER_DELIVERED);
    }

    public void addLink(String uri, String rel) {
        this.links.add(new Link(rel,uri));
    }

    public List<Link> getLinks() {
        return links;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        this.dateTime = formatter.format(dateTime);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Account getCustomer() {
        return customer;
    }

    public void setCustomer(Account customer) {
        this.customer = customer;
    }

    public List<Sandwich> getSandwiches() {
        return sandwiches;
    }

    public void setSandwiches(List<Sandwich> sandwiches) {
        this.sandwiches = new ArrayList<>(sandwiches);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
