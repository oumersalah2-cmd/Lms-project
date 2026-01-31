
package main.java.com.lms.model.user;

import java.util.ArrayList;
import java.util.List;

/**
 * SOLID:
 * - SRP: Student-specific state only.
 * - OCP: New roles can be added safely.
 */
public class Student extends User {

    private static final long serialVersionUID = 1L;

    private final String studentNumber;
    private final List<String> enrolledCourseIds = new ArrayList<>();

    public Student(String id,
                   String fullName,
                   String email,
                   String passwordHash,
                   String studentNumber) {

        super(id, fullName, email, passwordHash);

        if (studentNumber == null || studentNumber.isBlank())
            throw new IllegalArgumentException("Student number required");

        this.studentNumber = studentNumber;
    }

    @Override
    public String getRole() {
        return "STUDENT";
    }

    public String getStudentNumber(){
        return this.studentNumber;
    }

    public void enroll(String courseId) {
        if (!enrolledCourseIds.contains(courseId)) {
            enrolledCourseIds.add(courseId);
        }
    }

    public List<String> getEnrolledCourseIds() {
        return List.copyOf(enrolledCourseIds); // defensive copy
    }
}
