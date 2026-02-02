package main.java.com.lms.service;

import java.util.ArrayList;
import java.util.List;

import main.java.com.lms.model.course.StudentTask;

public final class TaskService {
    private static TaskService instance;
    private final List<StudentTask> taskCache = new ArrayList<>();

    private TaskService() {
        // Now this works perfectly because the constructor matches
        taskCache.add(new StudentTask("T1", "U-1769678864695", "Submit Java Lab", false));
        taskCache.add(new StudentTask("T2", "U-1769678864695", "Quiz 1", false));
    }

    public static synchronized TaskService getInstance() {
        if (instance == null) instance = new TaskService();
        return instance;
    }

    // Get tasks only for the logged-in student
    public List<StudentTask> getTasksByStudent(String studentId) {
        return taskCache.stream()
                .filter(t -> t.getStudentId().equals(studentId))
                .toList();
    }

    public long getPendingTaskCount(String studentId) {
        return taskCache.stream()
                .filter(t -> t.getStudentId().equals(studentId) && !t.isCompleted())
                .count();
    }
    
    public void addTask(StudentTask task) {
        taskCache.add(task);
    }
}