package main.java.com.yourorg.lms.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.com.yourorg.lms.model.course.Course;

public class AdminCatalogController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterBox;
    
    @FXML private TableView<Course> globalTable;
    @FXML private TableColumn<Course, String> colId;
    @FXML private TableColumn<Course, String> colName;
    @FXML private TableColumn<Course, String> colInstructor;
    
    // Master list for all courses in the system
    private final ObservableList<Course> masterCourseData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilterBox();
        loadCourseData();
        setupSearchFilter();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("title"));
        colInstructor.setCellValueFactory(new PropertyValueFactory<>("instructorId"));
        
        // Ensure the table fills the available width
        globalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupFilterBox() {
        filterBox.getItems().addAll("All Departments", "Computer Science", "Mathematics", "Physics", "Business");
        filterBox.getSelectionModel().selectFirst();
    }

    private void loadCourseData() {
        // Mock data to test the Admin Tools view
        masterCourseData.addAll(
            new Course("CS101", "Introduction to Java", "Basic programming concepts", "INST_001", "active"),
            new Course("MATH202", "Calculus II", "Integration and series", "INST_042", "active"),
            new Course("PY101", "General Physics", "Mechanics and heat", "INST_009","archived"),
            new Course("CS301", "Database Systems", "SQL and relational design", "INST_001", "archived")
        );
        
        globalTable.setItems(masterCourseData);
    }

    private void setupSearchFilter() {
        FilteredList<Course> filteredData = new FilteredList<>(masterCourseData, p -> true);

        // Listen for text changes in the search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(course -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                if (course.getTitle().toLowerCase().contains(lowerCaseFilter)) return true;
                if (course.getId().toLowerCase().contains(lowerCaseFilter)) return true;
                return course.getInstructorId().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Course> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(globalTable.comparatorProperty());
        globalTable.setItems(sortedData);
    }

    @FXML
    private void handleDeleteCourse() {
        Course selected = globalTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            masterCourseData.remove(selected);
            System.out.println("[ADMIN] Permanently deleted course: " + selected.getId());
        } else {
            showWarning("Selection Required", "Please select a course to delete.");
        }
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}