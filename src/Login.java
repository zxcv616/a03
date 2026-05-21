/**
 * Login class for user authentication.
 * Validates username and password credentials.
 *
 * @author Anthony Soto
 * @version 1.0
 */

import java.util.LinkedList;
import java.util.Scanner;


public class Login {

    private String user; 
    private String pass; 

    public Login() {
    }

    public void setUser(String newUser) {
        this.user = newUser;
    }

    public void setPass(String newPass) {
        this.pass = newPass;
    }

    public boolean checkValidity() {
        /*
        Description: Check if the user is a valid email or password is "password"
        Email: at least 3 letters before @, some text after @, followed by .net, .com, .org, or .edu
        Password: accept "password" universally for testing, or any password if email is valid
        */

        String emailRegex = "^[a-zA-Z]{3,}@.+\\.(net|com|org|edu)$";
        return (user != null && user.matches(emailRegex)) || "password".equals(pass);
    }
}
