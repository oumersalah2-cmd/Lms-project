package main.java.com.lms.service;

import main.java.com.lms.model.user.User;
import main.java.com.lms.model.user.UserFactory;
import main.java.com.lms.repository.UserRepository;
import main.java.com.lms.repository.impl.FileUserRepository;
import main.java.com.lms.util.SecurityUtil;
import main.java.com.lms.util.SessionManager;

/**
 * AuthService
 *
 * SOLID:
 * - SRP: Handles authentication & registration logic only.
 * - DIP: Depends on UserRepository abstraction (not storage details).
 * - OCP: New auth rules can be added without UI changes.
 *
 * Security:
 * - No plain-text passwords stored.
 * - Password verification delegated to SecurityUtil.
 */
public final class AuthService {

    private static AuthService instance;

    private final UserRepository userRepository;
    private AuthService() {
        this.userRepository = FileUserRepository.getInstance();
        SessionManager.getInstance();
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    // ---------------- LOGIN ----------------

    /**
     * Authenticates a user using email and raw password.
     */
    public void login(String email, String rawPassword) {
        if (email == null || rawPassword == null) {
            throw new IllegalArgumentException("Email and password must not be null");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("No user found with email: " + email);
        }

        if (!SecurityUtil.verifyPassword(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        SessionManager.login(user);
    }

    // ---------------- REGISTRATION ----------------

    /**
     * Registers a new user and persists it.
     */
    public void register(
            String role,
            String id,
            String fullName,
            String email,
            String rawPassword,
            String extraField
    ) {
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalStateException("Email is already registered");
        }

        String passwordHash = SecurityUtil.hash(rawPassword);

        User user = UserFactory.createNewUser(
                role,
                id,
                fullName,
                email,
                passwordHash,
                extraField
        );

        userRepository.save(user);
    }

    // ---------------- Custom Exceptions ----------------

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }
}
