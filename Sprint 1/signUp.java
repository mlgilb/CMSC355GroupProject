package signin;
import java.util.ArrayList;
import java.util.Scanner;

public class SignIn {

    public static void main(String[] args) {
        Scanner scnr = new Scanner(System.in);
        ArrayList<User> users = new ArrayList<>();
        boolean signingUp = true;
        
        while (signingUp) {
            //ask the user for a username until they chose an available one
            User newUser = new User();
            System.out.println("Enter a username");
            String username = scnr.nextLine().replaceAll("\\s", "");
            
            //if it is available, ask for a password and add them to the users list
            if (!newUser.searchUser(username)) {
                System.out.println("Enter a password");
                String password = scnr.nextLine().replaceAll("\\s", "");
                newUser.setLogin(username, password);
                users.add(newUser);
                signingUp = false;
            }
            else {
                System.out.println("Username is not available.");
            }
        }
    }
    
}
