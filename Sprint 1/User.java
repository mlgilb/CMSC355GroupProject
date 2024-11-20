package signin;
import java.util.ArrayList;

public class User {
    final private static ArrayList<String> users = new ArrayList<>(); //stores the usernames that are in use
    private String username;
    private String password;
    
    User() {
        this.username = "";
        this.password = "";
    }
    
    public boolean setLogin(String username, String password) {
        //searches for if the username is available
        boolean found = searchUser(username);
        
        //if the username is available, add it to the list
        if (!found) {
            this.username = username;
            this.password = encrypt(password);
            users.add(username);
        }
        else {
            System.out.println("Username is not available.");
        }
        return found;
    }
    
    public void setUsername(String username) {
        //searches for if the username is available
        boolean found = searchUser(username);
        
        //if the username is available, remove the current username from the list and add the new one
        if (!found) {
            removeUser(this.username);
            this.username = username;
            users.add(username);
        }
        else {
            System.out.println("Username is not available.");
        }
        
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setPassword(String password) {
        this.password = encrypt(password);
    }
    
    public boolean getPassword(String password) {
        return this.password.equals(encrypt(password));
    }
    
    public boolean searchUser(String username) {
        //searches for if a username is available
        boolean found = false;
        for (String user : users) {
            if (user.equals(username)) {
                found = true;
                break;
            }
        }
        return found;
    }
    
    private void removeUser(String username) {
        //finds the usersname in users list and removes it
        int index = 0;
        for (String user : users) {
            if (user.equals(username)) {
                users.remove(index);
                break;
            }
            index++;
        }
    }
    
    private String encrypt(String password) {
        int encrypted = 0;
        for(int i = 0; i < password.length(); i++) {
            if (i % 2 == 0) {
                encrypted += password.charAt(i);
            }
            else {
                encrypted *= password.charAt(i);
            }
        }
        encrypted *= password.charAt(0);
        return String.valueOf(encrypted);
    }
}
