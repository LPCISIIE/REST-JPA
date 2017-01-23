package boundary.Account;

import control.KeyGenerator;
import entity.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import javax.ejb.DuplicateKeyException;
import javax.ejb.Stateless;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.security.Key;
import java.util.List;
import java.util.UUID;

@Stateless
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
