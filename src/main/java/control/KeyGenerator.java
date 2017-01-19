package control;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;


public class KeyGenerator {

    /**
     * Method that generates a key
     * @return a SecretKey
     */
    public Key generateKey() {
        String keyString = "github";
        return new SecretKeySpec(keyString.getBytes(),0,keyString.getBytes().length, "DES");
    }
}
