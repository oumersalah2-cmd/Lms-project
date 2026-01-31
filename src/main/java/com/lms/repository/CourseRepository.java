package main.java.com.lms.repository;


import main.java.com.lms.model.course.Course;
import java.util.List;

/**
 * Repository abstraction for Course persistence.
 */
public interface CourseRepository {

    void save(Course course);

    void update(Course course); // Add this

    void delete(String id);

    List<Course> findAll();

    Course findById(String id);

    void delete(Course course);

    List<Course> findByInstructor(String instructorId);
}