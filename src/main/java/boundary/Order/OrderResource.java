package boundary.Order;

import entity.Account;
import entity.Order;
import entity.Sandwich;

import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrderResource {
    @PersistenceContext
    EntityManager entityManager;

    /**
     * Method that returns an order for an id given
     * @param id
     * @return Order
     */
    public Order findById(String id) {
        return entityManager.find(Order.class, id);
    }

    /**
     * Method that returns all the orders
     * @return List of Order
     */
    public List<Order> findAll() {
        return entityManager.createNamedQuery("Sandwich.findAll", Order.class)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }

    public Order insert(String accountId, String dateTime, String ...sandwichesId) {
        Account account = entityManager.find(Account.class, accountId);
        if (account == null)
            return null;

        Order order = new Order();
        order.setCustomer(account);
        Date date = order.toDate(dateTime);

        if (date == null)
            return null;

        order.setDateTime(date);
        Sandwich sandwich;

        for (String id : sandwichesId) {
           sandwich = entityManager.find(Sandwich.class, id);
           if (sandwich == null)
               return null;
           else
               order.addSandwich(sandwich);
        }

        order.setId(UUID.randomUUID().toString());

        return entityManager.merge(order);
    }

    /**
     * Method that updates an Order
     * @param order to update
     * @return the new order
     */
    public Order update(Order order) {
        return entityManager.merge(order);
    }

    /**
     * Method that deletes an Order
     * @param orderId
     * @return if it's deleted
     */
    public boolean delete(String orderId) {
        Order order = entityManager.find(Order.class, orderId);

        if (order != null) {
            entityManager.remove(order);
            return true;
        }

        return false;
    }
}
