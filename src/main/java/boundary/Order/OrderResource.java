package boundary.Order;

import boundary.Sandwich.SandwichResource;
import entity.Account;
import entity.Shipment;
import entity.Sandwich;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Stateless
public class OrderResource {

    @PersistenceContext
    EntityManager entityManager;

    @EJB
    SandwichResource sandwichResource;

    /**
     * Method that returns an order for an id given
     * @param id
     * @return Order
     */
    public Shipment findById(String id) {
        return entityManager.find(Shipment.class, id);
    }

    /**
     * Method that returns all the orders
     * @return List of Order
     */
    public List<Shipment> findAll() {
        return entityManager.createNamedQuery("Shipment.findAll", Shipment.class)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }

    public Shipment insert(Account account, String dateTime, List<String> sandwichesId) {

        Shipment order = new Shipment();
        order.setCustomer(account);
        Date date = order.toDate(dateTime);

        if (date == null)
            return null;

        order.setDateTime(date);

        Sandwich sandwich;

        for (String id : sandwichesId) {
           sandwich = sandwichResource.findById(id);

            if (sandwich == null) {
                return null;
            } else {
                Sandwich copy = new Sandwich(sandwich);
                sandwichResource.insert(copy);
                order.addSandwich(copy);
            }
        }


        order.setId(UUID.randomUUID().toString());

        return entityManager.merge(order);
    }

    /**
     * Method that updates an Order
     * @param order to update
     * @return the new order
     */
    public Shipment update(Shipment order) {

        return entityManager.merge(order);
    }

    /**
     * Method that updates the size of a Sandwich
     * @param order to update
     * @param sandwichId to update
     * @param size new size
     * @return the new order
     */
    public Shipment updateSize(Shipment order, String sandwichId, String size) {
        Sandwich sandwich = sandwichResource.findById(sandwichId);

        if (sandwich == null || !Sandwich.isSizeOk(size) || order.getStatus() != Shipment.ORDER_CREATED)
            return null;

        sandwich.setSize(size);
        sandwichResource.update(sandwich);
        return entityManager.merge(order);
    }

    /**
     * Method to add a sandwich to an order
     * @param order
     * @param sandwichId
     * @return
     */
    public Shipment addSandwich(Shipment order, String sandwichId) {
        Sandwich sandwich = sandwichResource.findById(sandwichId);
        if (sandwich == null)
            return null;
        order.addSandwich(sandwich);
        return entityManager.merge(order);
    }

    /**
     * Method that removes a sandwich in an order
     * @param order the Shipment
     * @param sandwichId the id of the sandwich asked
     * @return if it's deleted
     */
    public boolean removeSandwich(Shipment order, String sandwichId) {
        if (order != null) {
            if (order.getStatus().equals(Shipment.ORDER_CREATED)) {
                Sandwich sandwich = sandwichResource.findById(sandwichId);
                if (sandwich != null) {
                    order.removeSandwich(sandwichId);
                    sandwichResource.delete(sandwich.getId());
                    entityManager.merge(order);
                    if (order.getSandwiches().size() == 0)
                        delete(order);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method that deletes an Order
     * @param order
     * @return if it's deleted
     */
    public boolean delete(Shipment order) {
       if (order != null) {
           if (order.getStatus().equals(Shipment.ORDER_CREATED)) {
               for (Sandwich sandwich : order.getSandwiches())
                    sandwichResource.delete(sandwich.getId());
                entityManager.remove(order);
                return true;
            }
        }
        return false;
    }
}
