package boundary.Account;

import com.sun.tools.internal.ws.wsdl.framework.DuplicateEntityException;
import entity.Account;

import javax.ejb.DuplicateKeyException;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

public class AccountResource {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * Method that returns an account for an id given
     * @param email
     * @return Account
     */
    public Account findByEmail(String email) {
        return entityManager.find(Account.class, email);
    }


    /**
     * Method that returns all the accounts
     * NEVER USE THIS IN PUBLIC LMAO
     * @return List of Account
     */
    public List<Account> findAll(){
        return entityManager.createNamedQuery("Account.findAll", Account.class)
                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH)
                .getResultList();
    }



    /**
     * Method that inserts an account into the database
     * @param account to add
     * @return the account
     */
    public Account insert(Account account) throws DuplicateKeyException {
        try {
            return entityManager.merge(account);
        }catch (Exception e){
            throw new DuplicateKeyException();
        }
    }

}
