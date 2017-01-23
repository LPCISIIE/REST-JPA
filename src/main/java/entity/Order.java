package entity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Order.findAll", query = "SELECT o FROM Order o")
})
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date dateTime;

    @Id
    private String id;

    @ManyToOne
    private Account user;


    /**
     * Helper function that converts a String into a Date
     * @param s the String in the format 'dd/MM/yyyy HH:mm'
     * @return the Date if it's ok else null
     */
    public Date toDate(String s) {
        Date date = null;
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            date = inputDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }



}
