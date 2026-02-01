package main.java.com.yourorg.lms.ui.controller;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import main.java.com.yourorg.lms.model.course.Course;
import main.java.com.yourorg.lms.repository.impl.FileCourseRepository;
import main.java.com.yourorg.lms.service.EnrollmentService;
import main.java.com.yourorg.lms.ui.util.AlertUtil; // <--- IMPORT THIS
import main.java.com.yourorg.lms.util.SessionManager;

public class StudentCoursesController {

    @FXML private VBox mainContainer;
    
    // Tab 1: Enrolled
    @FXML private TableView<Course> enrolledTable;
    @FXML private TableColumn<Course, String> colCourseCode, colCourseName, colInstructor, colGrade;

    // Tab 2: Catalog
    @FXML private TextField searchField;
    @FXML private TableView<Course> catalogTable;
    @FXML private TableColumn<Course, String> colCatName, colCatDesc, colCatSeats;

    private FilteredList<Course> filteredCatalog;

    @FXML
    public void initialize() {
        setupTableFactories();
        loadData();
        setupSearchLogic();
    }

    private void setupTableFactories() {
        // Enrolled Table
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("title"));
        colInstructor.setCellValueFactory(new PropertyValueFactory<>("instructorId"));

        // Catalog Table
        colCatName.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCatDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private void loadData() {
        String studentId = SessionManager.getCurrentUser().getId();
        
        // Load Enrolled
        List<Course> enrolled = EnrollmentService.getInstance().getStudentEnrolledCourses(studentId);
        enrolledTable.setItems(FXCollections.observableArrayList(enrolled));

        // Load Catalog
        List<Course> allCourses = FileCourseRepository.getInstance().findAll();
        filteredCatalog = new FilteredList<>(FXCollections.observableArrayList(allCourses), p -> true);
        catalogTable.setItems(filteredCatalog);
    }

    private void setupSearchLogic() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredCatalog.setPredicate(course -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return course.getTitle().toLowerCase().contains(lowerCaseFilter) || 
                       course.getId().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    @FXML
    private void handleEnroll() {
        Course selected = catalogTable.getSelectionModel().getSelectedItem();
        
        // 1. Check for Selection
        if (selected == null) {
            AlertUtil.show(Alert.AlertType.WARNING, "No Selection", "Please select a course from the catalog first.");
            return;
        }

        // 2. Try to Enroll
        try {
            EnrollmentService.getInstance().enrollCurrentStudent(selected.getId());
            
            // Success Alert
            AlertUtil.show(Alert.AlertType.INFORMATION, "Success", "You have successfully enrolled in " + selected.getTitle());
            
            loadData(); // Refresh tables

        } catch (IllegalStateException e) {
            // Known Logic Errors (Already enrolled, etc.)
            AlertUtil.show(Alert.AlertType.ERROR, "Enrollment Failed", e.getMessage());
            
        } catch (Exception e) {
            // Unexpected System Errors
            e.printStackTrace();
            AlertUtil.show(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void handleRootClick() {
        if (mainContainer != null) mainContainer.requestFocus();
    }
}