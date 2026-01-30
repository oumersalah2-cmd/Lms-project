package main.java.com.lms.model.course;


import java.io.Serializable;
import java.time.LocalDate;

/**
 * SRP: Represents a Student–Course relationship.
 * Immutable + Serializable for persistence safety.
 */
public class Enrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String studentId;
    private final String courseId;
    private final LocalDate enrollmentDate;

    public Enrollment(String studentId, String courseId, LocalDate enrollmentDate) {
        if (studentId == null || courseId == null || enrollmentDate == null) {
            throw new IllegalArgumentException("Enrollment fields cannot be null");
        }
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
}
