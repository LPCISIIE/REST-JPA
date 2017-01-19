package control;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordManagement {

    public static String digestPassword(String password) throws RuntimeException {
        try {
            return BCrypt.hashpw(password, BCrypt.gensalt());
        } catch (Exception e) {
            throw new RuntimeException("Hey what did you give me ?! (Cannot digest)");
        }
    }

}

