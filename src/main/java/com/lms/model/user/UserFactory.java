package main.java.com.lms.model.user;


/**
 * Factory Pattern for User creation.
 *
 * SOLID:
 * - SRP: Object creation only.
 * - OCP: New roles added without touching callers.
 */
public final class UserFactory {

    private UserFactory() {}

    // -------- CREATE (NEW USERS – hash ONCE) --------

    public static User createNewUser(
            String role,
            String id,
            String name,
            String email,
            String rawPassword,
            String extra) {

        return switch (role.toUpperCase()) {
            case "STUDENT" ->
                    new Student(id, name, email, rawPassword, extra);

            case "INSTRUCTOR" ->
                    new Instructor(id, name, email, rawPassword, extra);

            case "ADMIN" ->
                    new Admin(id, name, email, rawPassword);

            default ->
                    throw new IllegalArgumentException("Unknown role: " + role);
        };
    }

    // -------- LOAD (FROM FILE – NO hashing) --------

    public static User loadUser(
            String role,
            String id,
            String name,
            String email,
            String passwordHash,
            String extra) {

        return switch (role.toUpperCase()) {
            case "STUDENT" ->
                    new Student(id, name, email, passwordHash, extra);

            case "INSTRUCTOR" ->
                    new Instructor(id, name, email, passwordHash, extra);

            case "ADMIN" ->
                    new Admin(id, name, email, passwordHash);

            default ->
                    throw new IllegalArgumentException("Unknown role: " + role);
        };
    }
}
