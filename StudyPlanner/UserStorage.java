import java.io.*;
import java.util.*;

/**
 * Stores user accounts (username, password) in the users.txt file
 * File format: one account per line, separated by | e.g. somchai|1234
 */
public class UserStorage {

    private static final String FILE_NAME = "users.txt";

    // ---------- Check whether username/password matches a registered account ----------
    public static boolean validateLogin(String username, String password) {
        for (String[] user : loadUsers()) {
            if (user[0].equals(username) && user[1].equals(password)) {
                return true;
            }
        }
        return false;
    }

    // ---------- Check whether this username is already taken ----------
    public static boolean usernameExists(String username) {
        for (String[] user : loadUsers()) {
            if (user[0].equals(username)) return true;
        }
        return false;
    }

    // ---------- Register a new account, returns true if successful, false if username is taken ----------
    public static boolean registerUser(String username, String password) {
        if (usernameExists(username)) {
            return false;
        }
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) { // true = append to existing file, don't erase old data
            writer.write(username + "|" + password + "\n");
            return true;
        } catch (IOException e) {
            System.out.println("Failed to save account: " + e.getMessage());
            return false;
        }
    }

    // ---------- Reset the password for the given username, returns true if found and reset successfully ----------
    public static boolean resetPassword(String username, String newPassword) {
        List<String[]> users = loadUsers();
        boolean found = false;

        for (String[] u : users) {
            if (u[0].equals(username)) {
                u[1] = newPassword;
                found = true;
                break;
            }
        }
        if (!found) return false;

        try (FileWriter writer = new FileWriter(FILE_NAME, false)) { // false = overwrite the entire old file
            for (String[] u : users) {
                writer.write(u[0] + "|" + u[1] + "\n");
            }
            return true;
        } catch (IOException e) {
            System.out.println("Failed to save: " + e.getMessage());
            return false;
        }
    }

    // ---------- Load all accounts from the file ----------
    private static List<String[]> loadUsers() {
        List<String[]> list = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return list; // nobody has signed up yet, return an empty list

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) list.add(parts);
            }
        } catch (IOException e) {
            System.out.println("Failed to load accounts: " + e.getMessage());
        }
        return list;
    }
}