package main.java.com.lms.repository.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.java.com.lms.model.course.Enrollment;
import main.java.com.lms.repository.EnrollmentRepository;

/**
 * File-based implementation of EnrollmentRepository.
 *
 * SOLID:
 * - SRP: Handles enrollment persistence only.
 * - DIP: Implements EnrollmentRepository abstraction.
 * - OCP: Can be replaced by DB repository without changing services.
 *
 * Design:
 * - Singleton to ensure one shared data source across JavaFX controllers.
 * - Cache used for fast UI access.
 */
public final class FileEnrollmentRepository implements EnrollmentRepository {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path FILE = DATA_DIR.resolve("enrollments.txt");
    private static final String DELIMITER = "|";

    private static FileEnrollmentRepository instance;
    private final List<Enrollment> cache = new ArrayList<>();

    private FileEnrollmentRepository() {
        initStorage();
        refreshCache();
    }

    public static synchronized FileEnrollmentRepository getInstance() {
        if (instance == null) {
            instance = new FileEnrollmentRepository();
        }
        return instance;
    }

    // ---------- Initialization ----------

    private void initStorage() {
        try {
            Files.createDirectories(DATA_DIR);
            if (!Files.exists(FILE)) {
                Files.createFile(FILE);
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to initialize enrollment storage");
            e.printStackTrace();
        }
    }

    // ---------- Repository Operations ----------

    @Override
    public void enroll(String studentId, String courseId) {
        if (studentId == null || courseId == null) {
            throw new IllegalArgumentException("Student ID and Course ID cannot be null");
        }

        if (isEnrolled(studentId, courseId)) {
            throw new IllegalStateException("Student already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment(
                studentId,
                courseId,
                LocalDate.now()
        );

        cache.add(enrollment);

        try (BufferedWriter writer = Files.newBufferedWriter(
                FILE,
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE)) {

            writer.write(String.join(DELIMITER,
                    enrollment.getStudentId(),
                    enrollment.getCourseId(),
                    enrollment.getEnrollmentDate().toString()
            ));
            writer.newLine();

        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save enrollment");
            e.printStackTrace();
        }
    }

    @Override
    public boolean isEnrolled(String studentId, String courseId) {
        return cache.stream()
                .anyMatch(e ->
                        e.getStudentId().equals(studentId) &&
                        e.getCourseId().equals(courseId)
                );
    }

    @Override
    public List<String> getCourseIdsByStudent(String studentId) {
        List<String> result = new ArrayList<>();
        for (Enrollment e : cache) {
            if (e.getStudentId().equals(studentId)) {
                result.add(e.getCourseId());
            }
        }
        return List.copyOf(result);
    }

    // ---------- Cache Management ----------

    private void refreshCache() {
        cache.clear();

        try (BufferedReader reader = Files.newBufferedReader(FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {

                String[] p = line.split("\\|", -1); 
                if (p.length != 3) {
                    System.err.println("[WARN] Skipping malformed enrollment line: " + line);
                    continue;
                }

                cache.add(new Enrollment(
                        p[0],
                        p[1],
                        LocalDate.parse(p[2])
                ));
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load enrollments");
            e.printStackTrace();
        }
    }

    public int getEnrollmentCount(String courseId) {
        return (int) cache.stream()
                .filter(e -> e.getCourseId().equals(courseId))
                .count();
    }

    public void deleteEnrollmentsByCourse(String courseId) {
    cache.removeIf(e -> e.getCourseId().equals(courseId));
    }

    public List<String> getStudentNamesForCourse(String courseId) {
    if (cache == null) return java.util.Collections.emptyList();

    return cache.stream()
        .filter(e -> e.getCourseId().equals(courseId))
        .map((Enrollment e) -> {
            main.java.com.lms.model.user.User student = 
                main.java.com.lms.repository.impl.FileUserRepository.getInstance().findById(e.getStudentId());
            
            return (student != null) ? student.getFullName() : "Unknown Student (" + e.getStudentId() + ")";
        })
        .collect(java.util.stream.Collectors.toList());
}
}
