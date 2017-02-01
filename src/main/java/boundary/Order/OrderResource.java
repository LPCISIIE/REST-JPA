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
     *
     * @param id ID of the order
     * @return Order
     */
    public Shipment findById(String id) {
        return entityManager.find(Shipment.class, id);
    }

    /**
     * Method that returns all the orders
     *
     * @return List of Order
     */
    public List<Shipment> findAll() {
        return entityManager.createNamedQuery("Shipment.findAll", Shipment.class)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }

    /**
     * Method that returns the orders with pagination and limit method
     *
     * @param offset start at the nth position
     * @param limit number max of result
     * @return List of Order
     */
    public List<Shipment> offsetLimit(int offset, int limit) {
        return entityManager.createNamedQuery("Shipment.findAll", Shipment.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }

    /**
     * Method that returns order for a specific status
     *
     * @param status
     * @return List of Shipment
     */
    public List<Shipment> findByStatus(int status){
        return entityManager.createQuery("SELECT s FROM Shipment s WHERE s.status = :status ")
                .setParameter("status", status)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }


    /**
     * Method that returns order for a specific time
     *
     * @param date
     * @return List of Shipment
     */
    public List<Shipment> findByDate(String date){
        return entityManager.createQuery("SELECT s FROM Shipment s WHERE s.dateTime = :date ")
                .setParameter("date", date)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }


    /**
     * Method to insert an order with a list of sandwiches
     *
     * @param account      Account related to the order
     * @param dateTime     Date and time of the order
     * @param sandwichesId IDs of sandwiches in the order
     * @return the order
     */
    public Shipment insert(Account account, String dateTime, List<String> sandwichesId) {
        Shipment order = new Shipment();
        order.setCustomer(account);
        Date date = order.toDate(dateTime);

        if (date == null)
            return null;

        order.setDateTime(date);

        order.setStatus(Shipment.CREATED);

        Sandwich sandwich;

        for (String id : sandwichesId) {
            sandwich = sandwichResource.findById(id);
            if (sandwich == null)
                return null;
            Sandwich copy = new Sandwich(sandwich);
            sandwichResource.insert(copy);
            order.addSandwich(copy);
        }

        order.setId(UUID.randomUUID().toString());

        return entityManager.merge(order);
    }

    /**
     * Method that updates an Order
     *
     * @param order to update
     * @param status of the order
     * @return the new order
     */
    public Shipment update(Shipment order, int status) {
        if (order.changeState(status))
            return entityManager.merge(order);
        return null;
    }

    /**
     * Method that updates an Order
     *
     * @param order to update
     * @return the new order
     */
    public Shipment update(Shipment order) {
        return entityManager.merge(order);
    }

    /**
     * Method that updates the size of a Sandwich
     *
     * @param order      to update
     * @param sandwichId to update
     * @param size       new size
     * @return the new order
     */
    public Shipment updateSize(Shipment order, String sandwichId, String size) {
        Sandwich sandwich = sandwichResource.findById(sandwichId);

        if (sandwich == null || !Sandwich.isSizeOk(size) || order.getStatus() != Shipment.CREATED)
            return null;

        sandwich.setSize(size);
        sandwichResource.update(sandwich);
        return entityManager.merge(order);
    }

    /**
     * Method to add a sandwich to an order
     *
     * @param order      The order in which we want to add a sandwich
     * @param sandwichId The ID of the sandwich we want to add
     * @return Shipment
     */
    public Shipment addSandwich(Shipment order, String sandwichId) {
        Sandwich sandwich = sandwichResource.findById(sandwichId);
        if (sandwich == null)
            return null;
        order.addSandwich(sandwich);
        return entityManager.merge(order);
    }

    /**
     * Function to update the delivering date
     *
     * @param order    The order of which we want to update the delivering date
     * @param dateTime The date and time of delivery
     * @return a shipment if it's ok else null
     */
    public Shipment updateDate(Shipment order, String dateTime) {
        if (order != null && dateTime != null) {
            if (order.getStatus() != Shipment.CREATED) {
                Date date = order.toDate(dateTime);
                if (date != null) {
                    order.setDateTime(date);
                    return entityManager.merge(order);
                }
            }
        }

        return null;
    }

    /**
     * Method that removes a sandwich in an order
     *
     * @param order      the Shipment
     * @param sandwichId the id of the sandwich asked
     * @return if it's deleted
     */
    public boolean removeSandwich(Shipment order, String sandwichId) {
        if (order != null) {
            if (order.getStatus() != Shipment.CREATED) {
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
     *
     * @param order
     * @return if it's deleted
     */
    public boolean delete(Shipment order) {
        if (order != null) {
            if (order.getStatus() != Shipment.CREATED) {
                for (Sandwich sandwich : order.getSandwiches())
                    sandwichResource.delete(sandwich.getId());

                entityManager.remove(order);
                return true;
            }
        }

        return false;
    }

}
