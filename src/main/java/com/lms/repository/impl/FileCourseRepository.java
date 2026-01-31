package main.java.com.lms.repository.impl;


import main.java.com.lms.model.course.Course;
import main.java.com.lms.repository.CourseRepository;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileCourseRepository implements CourseRepository {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path COURSE_FILE = DATA_DIR.resolve("courses.txt");
    private static final String DELIMITER = "|";

    private static FileCourseRepository instance;
    private final List<Course> cache = new ArrayList<>();

    // --- Singleton ---
    private FileCourseRepository() {
        initStorage();
        refreshCache();
    }

    public static synchronized FileCourseRepository getInstance() {
        if (instance == null) {
            instance = new FileCourseRepository();
        }
        return instance;
    }

    // --- Initialization (Defensive) ---
    private void initStorage() {
        try {
            if (!Files.exists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }
            if (!Files.exists(COURSE_FILE)) {
                Files.createFile(COURSE_FILE);
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to initialize course storage");
            e.printStackTrace();
        }
    }

    // --- CRUD Operations ---

    @Override
    public void save(Course course) {
        cache.add(course);

        try (BufferedWriter writer = Files.newBufferedWriter(
                COURSE_FILE,
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE)) {

            writer.write(String.join(DELIMITER,
                    course.getId(),
                    course.getTitle(),
                    course.getDescription(),
                    course.getInstructorId(),
                    course.getStatus()
            ));
            writer.newLine();

        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save course");
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Course course) {
        cache.removeIf(c -> c.getId().equals(course.getId()));
        try (BufferedWriter writer = Files.newBufferedWriter(
                COURSE_FILE, 
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            for (Course c : cache) {
                writer.write(String.join(DELIMITER,
                        c.getId(),
                        c.getTitle(),
                        c.getDescription(),
                        c.getInstructorId()
                ));
                writer.newLine();
            }
            System.out.println("[INFO] Course deleted: " + course.getId());
            
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to update file after deletion");
            e.printStackTrace();
        }
    }


    @Override
    public void delete(String id) {
        cache.removeIf(c -> c.getId().equals(id));
        rewriteFile();
    }

   @Override
    public Course findById(String id) {
        // Search the cache first for speed
        return cache.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }


    @Override
    public List<Course> findAll() {
        refreshCache();
        return new ArrayList<>(cache);
    } 

    @Override
    public List<Course> findByInstructor(String instructorId) {
        List<Course> result = new ArrayList<>();
        for (Course c : cache) {
            if (c.getInstructorId().equals(instructorId)) {
                result.add(c);
            }
        }
        return result;
    }

    // --- Cache Management ---

    public void refreshCache() {
        cache.clear();

        try (BufferedReader reader = Files.newBufferedReader(COURSE_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(Pattern.quote(DELIMITER));
                
                if (parts.length >= 4) {
                    String id = parts[0];
                    String title = parts[1];
                    String desc = parts[2];
                    String instId = parts[3];
                    
                    String status = (parts.length > 4) ? parts[4] : "Active";

                    cache.add(new Course(id, title, desc, instId, status));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    public void update(Course updatedCourse) {
    for (int i = 0; i < cache.size(); i++) {
        if (cache.get(i).getId().equals(updatedCourse.getId())) {
            cache.set(i, updatedCourse);
            break;
        }
    }
    
        saveAllToFile();
    }

    private void saveAllToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(COURSE_FILE, 
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            
            for (Course c : cache) {
                String line = String.join(DELIMITER,
                    c.getId(),
                    c.getTitle(),
                    c.getDescription(),
                    c.getInstructorId(),
                    c.getStatus()
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to rewrite course file: " + e.getMessage());
        }
    }

    private void rewriteFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(COURSE_FILE, 
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            for (Course c : cache) {
                writer.write(String.join(DELIMITER, 
                    c.getId(), c.getTitle(), c.getDescription(), c.getInstructorId(), c.getStatus()));
                writer.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    } 
       
    
    
}
