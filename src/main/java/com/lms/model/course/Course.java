package main.java.com.lms.model.course;

import java.io.Serializable;
import java.util.Objects;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id; // Keep ID final
    private String title;    // Removed final
    private String description; // Removed final
    private final String instructorId; // Keep Instructor ID final
    private String status;

    public Course(String id, String title, String description, String instructorId, String status) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Course ID required");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Course title required");
        if (instructorId == null || instructorId.isBlank()) throw new IllegalArgumentException("Instructor ID required");

        this.id = id;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.status = status;
    }

    // Getters...
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getInstructorId() { return instructorId; }
    public String getStatus() { return status; }

    // Implement the Setters properly
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        return Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}