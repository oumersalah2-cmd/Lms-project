package main.java.com.lms.repository.impl;



import main.java.com.lms.model.user.Admin;
import main.java.com.lms.model.user.Instructor;
import main.java.com.lms.model.user.Student;
import main.java.com.lms.model.user.User;
import main.java.com.lms.model.user.UserFactory;
import main.java.com.lms.repository.UserRepository;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based UserRepository implementation.
 *
 * SOLID:
 * - SRP: Handles file persistence only.
 * - OCP: Can be replaced with DB repository without affecting UI.
 * - DIP: Used via UserRepository interface.
 *
 * Design Patterns:
 * - Singleton: Shared data source across JavaFX controllers.
 * - Repository: Abstracts persistence logic.
 */
public final class FileUserRepository implements UserRepository {

    private static FileUserRepository instance;

    private static final Path DATA_PATH = Paths.get("data");
    private static final Path USER_FILE = DATA_PATH.resolve("users.txt");


    private final List<User> cache = new ArrayList<>();


    private FileUserRepository() {
        initStorage();
        refreshCache();
    }


    public static synchronized FileUserRepository getInstance() {
        if (instance == null) {
            instance = new FileUserRepository();
        }
        return instance;
    }

    // ---------------- Initialization ----------------

    private void initStorage() {
        try {

            Path parentDir = USER_FILE.getParent();


            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                System.out.println("[INFO] Created storage directory: " + parentDir);
            }


            if (!Files.exists(USER_FILE)) {
                Files.createFile(USER_FILE);
                System.out.println("[INFO] Created new user database file: " + USER_FILE);
            }
        } catch (IOException e) {

            System.err.println("[FATAL ERROR] Could not initialize storage at " + USER_FILE);
            System.err.println("Reason: " + e.getMessage());
        }
    }

    // ---------------- Cache Management ----------------

    /**
     * Reloads file data into memory.
     * Call this ONCE at app startup or after bulk operations.
     */
    public void refreshCache() {
        cache.clear();

        try (BufferedReader reader = Files.newBufferedReader(USER_FILE)) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");

                if (parts.length < 5) {
                    System.err.println("[WARN] Skipping malformed line: " + line);
                    continue;
                }

                // Map the parts carefully
                String role = parts[0].trim();
                String id = parts[1].trim();
                String name = parts[2].trim();
                String email = parts[3].trim();
                String passwordHash = parts[4].trim();

                String extra = (parts.length > 5) ? parts[5].trim() : null;

                User user = UserFactory.loadUser(
                        role, id, name, email, passwordHash, extra
                );

                cache.add(user);
            }
            System.out.println("[INFO] Successfully loaded " + cache.size() + " users into cache.");

        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load users from file");
            e.printStackTrace();
        }
    }

    // ---------------- Repository Methods ----------------

    @Override
    public void save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        cache.add(user);

        try (BufferedWriter writer = Files.newBufferedWriter(
                USER_FILE, StandardOpenOption.APPEND)) {

            writer.write(user.getRole() + ",");

            if (user instanceof Student student) {
                writer.write(String.join(",",
                        student.getId(),
                        student.getFullName(),
                        student.getEmail(),
                        student.getPasswordHash(),
                        student.getStudentNumber()
                ));
                writer.newLine();
            }
            else if (user instanceof Instructor instructor) {
                writer.write(String.join(",",
                        instructor.getId(),
                        instructor.getFullName(),
                        instructor.getEmail(),
                        instructor.getPasswordHash(),
                        instructor.getDepartment()
                ));
                writer.newLine();
            }
            else if (user instanceof Admin admin) {
                writer.write(String.join(",",
                        admin.getId(),
                        admin.getFullName(),
                        admin.getEmail(),
                        admin.getPasswordHash(),
                        "-"
                ));
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save user to file");
            e.printStackTrace();
        }
    }


    @Override
    public User findByEmail(String email) {
        if (email == null) return null;
        return cache.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(cache);
    }

    @Override
    public void delete(String id) {
        cache.removeIf(u -> u.getId().equals(id));
        rewriteFile();
    }

    // ---------------- Helper ----------------

    @Override
    public void update(User updatedUser) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(updatedUser.getId())) {
                cache.set(i, updatedUser);
                rewriteFile();
                return;
            }
        }
    }

    private void rewriteFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(USER_FILE)) {
            for (User user : cache) {
                StringBuilder line = new StringBuilder();
                line.append(user.getRole()).append(",");
                line.append(user.getId()).append(",");
                line.append(user.getName()).append(",");
                line.append(user.getEmail()).append(",");
                line.append(user.getPasswordHash());

                // Handle the "extra" field based on subclass
                if (user instanceof Student s) line.append(",").append(s.getStudentNumber());
                else if (user instanceof Instructor inst) line.append(",").append(inst.getDepartment());
                else line.append(",-");

                writer.write(line.toString());
                writer.newLine();
            }
            System.out.println("[INFO] users.txt successfully updated.");
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to rewrite user file: " + e.getMessage());
        }
    }


    public User findById(String id) {

        if (cache.isEmpty()) {
            refreshCache();
        }

        return cache.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
