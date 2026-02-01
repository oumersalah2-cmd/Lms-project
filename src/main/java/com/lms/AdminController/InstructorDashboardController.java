package main.java.com.yourorg.lms.ui.controller;

import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.com.yourorg.lms.model.course.Course;
import main.java.com.yourorg.lms.model.user.Instructor;
import main.java.com.yourorg.lms.repository.impl.FileCourseRepository;
import main.java.com.yourorg.lms.repository.impl.FileEnrollmentRepository;
import main.java.com.yourorg.lms.util.SessionManager;

public class InstructorDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label taughtCountLabel;
    
    @FXML private TableView<Course> instructorCourseTable;
    @FXML private TableColumn<Course, String> colCourseName;
    @FXML private TableColumn<Course, Integer> colEnrollment;
    @FXML private Label lblTotalStudents;
    @FXML private TableView<Course> myCoursesTable;

    @FXML
    public void initialize() {
        setupTable();
        loadDashboardData();
    }

    private void setupTable() {
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        // Dynamic Calculation: Count enrollments for each course ID
        colEnrollment.setCellValueFactory(cellData -> {
            String courseId = cellData.getValue().getId();
            int count = FileEnrollmentRepository.getInstance().getEnrollmentCount(courseId);
            return new javafx.beans.property.SimpleObjectProperty<>(count);
        });
        
        // Make columns resize with the window
        instructorCourseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadDashboardData() {
        if (!(SessionManager.getCurrentUser() instanceof Instructor instructor)) return;

        welcomeLabel.setText("Welcome, Prof. " + instructor.getName());

        // Get only courses belonging to this instructor
        List<Course> myCourses = FileCourseRepository.getInstance().findAll().stream()
                .filter(c -> c.getInstructorId().equals(instructor.getId()))
                .toList();

        taughtCountLabel.setText(String.valueOf(myCourses.size()));
        instructorCourseTable.setItems(FXCollections.observableArrayList(myCourses));
    }

    private void loadInstructorCourses() {
        // 1. Safety check for the logged-in instructor
        if (!(SessionManager.getCurrentUser() instanceof Instructor instructor)) return;

        // 2. DEFINE the variable 'myCourses' here
        List<Course> myCourses = FileCourseRepository.getInstance().findAll().stream()
                .filter(c -> c.getInstructorId().equals(instructor.getId()))
                .collect(Collectors.toList());

        // 3. Use 'myCourses' to populate the table
        myCoursesTable.setItems(FXCollections.observableArrayList(myCourses));

        // 4. Calculate Total Students using 'myCourses'
        int totalEnrolled = myCourses.stream()
                .mapToInt(course -> FileEnrollmentRepository.getInstance()
                                        .getEnrollmentCount(course.getId()))
                .sum();

        // 5. Update the label we created earlier
        if (lblTotalStudents != null) {
            lblTotalStudents.setText(String.valueOf(totalEnrolled));
        }
    }

    @FXML
    private void handleDeleteCourse() {
        Course selected = instructorCourseTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            // Using a standard alert if you don't have AlertUtil yet
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a course to delete.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Course");
        alert.setHeaderText("Are you sure you want to delete: " + selected.getTitle() + "?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            FileCourseRepository.getInstance().delete(selected);
            loadDashboardData(); // Refresh the table
        }
    }
}