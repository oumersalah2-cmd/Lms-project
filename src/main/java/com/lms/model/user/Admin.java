package main.java.com.lms.model.user;



/**
 * Admin role.
 *
 * SOLID:
 * - SRP: Represents administrative identity only.
 */
public class Admin extends User  {

    private static final long serialVersionUID = 1L;

    public Admin(String id,
                 String fullName,
                 String email,
                 String passwordHash) {

        super(id, fullName, email, passwordHash);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}