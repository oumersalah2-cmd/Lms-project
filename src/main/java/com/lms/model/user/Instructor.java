package main.java.com.lms.model.user;



/**
 * Instructor role.
 *
 * SOLID:
 * - SRP: Stores instructor-specific state only.
 * - LSP: Fully substitutable for User.
 */
public class Instructor extends User {

    private static final long serialVersionUID = 1L;

    private final String department;

    public Instructor(String id,
                      String fullName,
                      String email,
                      String passwordHash,
                      String department) {

        super(id, fullName, email, passwordHash);

        if (department == null || department.isBlank())
            throw new IllegalArgumentException("Department is required");

        this.department = department;
    }

    @Override
    public String getRole() {
        return "INSTRUCTOR";
    }

    public String getDepartment() {
        return department;
    }
}
