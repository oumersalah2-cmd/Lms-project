package main.java.com.lms.model.user;

import java.io.Serializable;
import java.util.Objects;

/**
 * SOLID:
 * - SRP: Holds user state only (no security logic).
 * - OCP: Extendable via subclasses.
 * - LSP: Subclasses are substitutable.
 */
public abstract class User implements Serializable {

    private static final long serialVersionUID = 1L;

    // protected String name;
    private final String id;              // immutable identity
    private String fullName;
    private String email;
    private String passwordHash;    // already-hashed

    protected User(String id, String fullName, String email, String passwordHash) {

        // Fail-fast validation
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("User ID cannot be blank");

        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Invalid email");

        if (passwordHash == null || passwordHash.isBlank())
            throw new IllegalArgumentException("Password hash required");

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // ---- Encapsulation ----
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return fullName;}
    public String getPasswordHash() {
        return this.passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // ---- Polymorphism ----
    public abstract String getRole();

    // ---- Identity ----
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return id.equals(((User) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}