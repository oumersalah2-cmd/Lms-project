package main.java.com.lms.ui.controller;

import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import main.java.com.lms.model.course.Course;
import main.java.com.lms.model.user.Instructor;
import main.java.com.lms.repository.impl.FileCourseRepository;
import main.java.com.lms.repository.impl.FileEnrollmentRepository;
import main.java.com.lms.ui.util.AlertUtil;
import main.java.com.lms.util.SessionManager;

public class InstructorCoursesController {

    @FXML private TableView<Course> myCoursesTable; 
    @FXML private TableColumn<Course, String> colId;
    @FXML private TableColumn<Course, String> colTitle;
    @FXML private TableColumn<Course, Integer> colEnrollment;
    @FXML private TableColumn<Course, String> colStatus;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        colEnrollment.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(
                FileEnrollmentRepository.getInstance().getEnrollmentCount(cellData.getValue().getId())
            )
        );
        colStatus.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus())
        );
        
        myCoursesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        loadInstructorCourses();
    }

    private void loadInstructorCourses() {
        if (!(SessionManager.getCurrentUser() instanceof Instructor instructor)) return;

        List<Course> myCourses = FileCourseRepository.getInstance().findAll().stream()
                .filter(c -> c.getInstructorId().equals(instructor.getId()))
                .collect(Collectors.toList());

        myCoursesTable.setItems(FXCollections.observableArrayList(myCourses));
        myCoursesTable.refresh();
    }

    @FXML
    private void handleCreateCourse() {
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Create New Course");
        dialog.initOwner(myCoursesTable.getScene().getWindow());

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        TextArea descField = new TextArea();
        descField.setPrefRowCount(3);

        grid.add(new Label("Course Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new Course("C-" + System.currentTimeMillis(), 
                                  titleField.getText(), 
                                  descField.getText(), 
                                  SessionManager.getCurrentUser().getId(), 
                                  "Active");
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newCourse -> {
            if (newCourse.getTitle().isEmpty()) {
                AlertUtil.show(Alert.AlertType.ERROR, "Input Error", "Title cannot be empty.");
                return;
            }
            FileCourseRepository.getInstance().save(newCourse);
            loadInstructorCourses();
        });
    }

    @FXML
    private void handleEditDetails() {
        Course selected = myCoursesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.show(Alert.AlertType.WARNING, "No Selection", "Please select a course to edit.");
            return;
        }

        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Edit Course Details");
        dialog.initOwner(myCoursesTable.getScene().getWindow());

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField titleField = new TextField(selected.getTitle());
        TextArea descField = new TextArea(selected.getDescription());
        ComboBox<String> statusBox = new ComboBox<>(FXCollections.observableArrayList("Active", "Inactive", "Archived"));
        statusBox.setValue(selected.getStatus());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Status:"), 0, 2);
        grid.add(statusBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveButtonType) {
                selected.setTitle(titleField.getText());
                selected.setDescription(descField.getText());
                selected.setStatus(statusBox.getValue());
                return selected;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedCourse -> {
            FileCourseRepository.getInstance().update(updatedCourse); 
            loadInstructorCourses();
        });
    }

      @FXML
    private void handleViewStudents() {
        Course selected = myCoursesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.show(Alert.AlertType.WARNING, "No Selection", "Please select a course to view students.");
            return;
        }
        List<String> studentNames = FileEnrollmentRepository.getInstance()
                                        .getStudentNamesForCourse(selected.getId());

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Enrolled Students");
        dialog.setHeaderText("Students currently in: " + selected.getTitle());
        dialog.initOwner(myCoursesTable.getScene().getWindow());
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        ListView<String> listView = new ListView<>();
        listView.setPrefHeight(200);
        
        if (studentNames.isEmpty()) {
            listView.setPlaceholder(new Label("No students are currently enrolled."));
        } else {
            listView.setItems(FXCollections.observableArrayList(studentNames));
        }
        VBox container = new VBox(10, new Label("Student List:"), listView);
        container.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(container);

        dialog.showAndWait();
    }

 
}