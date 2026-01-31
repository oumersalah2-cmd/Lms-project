package main.java.com.lms.repository;


import java.util.List;

/**
 * EnrollmentRepository
 *
 * SOLID Principles Applied:
 * - DIP (Dependency Inversion Principle):
 *   High-level services depend on this abstraction, not on file/database implementations.
 *
 * - SRP (Single Responsibility Principle):
 *   Defines enrollment persistence contracts only.
 *
 * This interface allows the persistence mechanism (file, database, API)
 * to change without affecting the service or UI layers.
 */
public interface EnrollmentRepository {

    /**
     * Enrolls a student in a course.
     * Implementations must prevent duplicate enrollments.
     *
     * @param studentId the unique student identifier
     * @param courseId  the unique course identifier
     */
    void enroll(String studentId, String courseId);

    /**
     * Checks if a student is already enrolled in a course.
     *
     * @param studentId the student ID
     * @param courseId  the course ID
     * @return true if enrolled, false otherwise
     */
    boolean isEnrolled(String studentId, String courseId);

    /**
     * Returns all course IDs a student is enrolled in.
     * Implementations must return a defensive copy.
     *
     * @param studentId the student ID
     * @return list of course IDs
     */
    List<String> getCourseIdsByStudent(String studentId);
}
