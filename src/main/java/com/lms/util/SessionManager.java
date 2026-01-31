package main.java.com.lms.util;


import main.java.com.lms.model.user.User;

/**
 * SRP: Manages authentication session state only.
 * Singleton: One session per application.
 */
public final class SessionManager {

    private static SessionManager instance;
    private static User currentUser;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public static void login(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public  static User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
