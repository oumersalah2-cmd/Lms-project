package main.java.com.lms.service;

import java.util.ArrayList;
import java.util.List;

import main.java.com.lms.model.course.Course;
import main.java.com.lms.model.user.Student;
import main.java.com.lms.model.user.User;
import main.java.com.lms.repository.CourseRepository;
import main.java.com.lms.repository.EnrollmentRepository;
import main.java.com.lms.repository.impl.FileCourseRepository;
import main.java.com.lms.repository.impl.FileEnrollmentRepository;
import main.java.com.lms.util.SessionManager;

public final class EnrollmentService {

    private static EnrollmentService instance;

    private final EnrollmentRepository enrollmentRepo;
    private final CourseRepository courseRepo;

    private EnrollmentService() {
        this.enrollmentRepo = FileEnrollmentRepository.getInstance();
        this.courseRepo = FileCourseRepository.getInstance();
    }

    public static synchronized EnrollmentService getInstance() {
        if (instance == null) {
            instance = new EnrollmentService();
        }
        return instance;
    }

    /**
     * Enrolls the current user. 
     * Throws explicit exceptions so the UI knows exactly what went wrong.
     */
    public void enrollCurrentStudent(String courseId) {
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("You must be logged in to enroll.");
        }

        if (!(currentUser instanceof Student)) {
            throw new IllegalStateException("Only students can enroll in courses.");
        }

        Student student = (Student) currentUser;

        if (enrollmentRepo.isEnrolled(student.getId(), courseId)) {
            throw new IllegalStateException("You are already enrolled in this course.");
        }

        enrollmentRepo.enroll(student.getId(), courseId);
    }

    /**
     * Returns full Course objects for the student's dashboard.
     */
    public List<Course> getStudentEnrolledCourses(String studentId) {
        List<Course> result = new ArrayList<>();
        List<String> courseIds = enrollmentRepo.getCourseIdsByStudent(studentId);

        for (String id : courseIds) {
            Course c = courseRepo.findById(id);
            if (c != null) {
                result.add(c);
            }
        }
        return result;
    }
}