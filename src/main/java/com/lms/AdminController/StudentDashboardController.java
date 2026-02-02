package main.java.com.yourorg.lms.ui.controller;

import main.java.com.yourorg.lms.model.course.Course;
import main.java.com.yourorg.lms.model.course.StudentTask;
import main.java.com.yourorg.lms.model.user.Student;
import main.java.com.yourorg.lms.model.user.User;
import main.java.com.yourorg.lms.service.EnrollmentService;
import main.java.com.yourorg.lms.service.TaskService;
import main.java.com.yourorg.lms.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

/**
 * StudentDashboardController
 *
 * SOLID:
 * - SRP: Handles UI coordination only (no business logic).
 * - DIP: Depends on EnrollmentService abstraction via singleton access.
 *
 * Defensive Design:
 * - Prevents invalid role access.
 * - Prevents ClassCastException.
 */
public class StudentDashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML private Label activeCoursesCount;
    @FXML private Label tasksDueCount;
    @FXML private javafx.scene.layout.VBox mainContainer; 

    @FXML
    private TableView<Course> courseTable;

    private final EnrollmentService enrollmentService =
            EnrollmentService.getInstance();

    @FXML
    private void handleLogout() {
        main.java.com.yourorg.lms.util.SessionManager.logout();
        main.java.com.yourorg.lms.ui.util.ViewFactory.showLoginWindow();
    }
    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser instanceof Student student) {
            welcomeLabel.setText("Welcome, " + student.getName());
            setupTableColumns();
            loadDashboardData(student);

            if (mainContainer != null) {
                mainContainer.setFocusTraversable(true);
                mainContainer.setOnMouseClicked(e -> mainContainer.requestFocus());
            }
        }
    }

    private void loadDashboardData(Student student) {
        List<Course> enrolled = enrollmentService.getStudentEnrolledCourses(student.getId());
        courseTable.setItems(FXCollections.observableArrayList(enrolled));
        activeCoursesCount.setText(String.valueOf(enrolled.size()));
        long pendingTasks = TaskService.getInstance().getPendingTaskCount(student.getId());
        
        if (tasksDueCount != null) {
            tasksDueCount.setText(String.valueOf(pendingTasks));

            if (pendingTasks > 0) {
                tasksDueCount.setStyle("-fx-text-fill: #e67e22; -fx-font-size: 28px; -fx-font-weight: bold;");
            } else {
                tasksDueCount.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 28px; -fx-font-weight: bold;");
            }
        }
    }
    /**
     * Defines table columns manually to avoid FXML binding errors.
     */
    @SuppressWarnings("unchecked")
    private void setupTableColumns() {
        TableColumn<Course, String> idColumn = new TableColumn<>("Course ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(150);

        TableColumn<Course, String> titleColumn = new TableColumn<>("Course Name");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.prefWidthProperty().bind(courseTable.widthProperty().subtract(155));
        courseTable.getColumns().clear();
        courseTable.getColumns().addAll(idColumn, titleColumn);
        courseTable.setPlaceholder(new Label("You are not enrolled in any courses yet."));
    }

    @FXML
    private void handleRefresh() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser instanceof Student student) {
            System.out.println("[UI] Refreshing student dashboard data...");
            loadDashboardData(student);
        }
    }

    @FXML
    private void showTaskDialog() {
        Stage dialog = new Stage();
        String currentStudentId = SessionManager.getCurrentUser().getId();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Your Tasks");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Label header = new Label("To-Do List");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ListView<HBox> taskListView = new ListView<>();
        ObservableList<StudentTask> tasks = FXCollections.observableArrayList(
            new StudentTask("T-INIT-1", currentStudentId, "Submit Java Assignment", false),
            new StudentTask("T-INIT-2", currentStudentId, "Review Quiz 1", false)
        );

        final Runnable[] refreshList = new Runnable[1];
        refreshList[0] = () -> {
            taskListView.getItems().clear();
            for (StudentTask t : tasks) {
                CheckBox cb = new CheckBox(t.getTitle());
                cb.setSelected(t.isCompleted());

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button deleteBtn = new Button("🗑");
                deleteBtn.setStyle("-fx-text-fill: red; -fx-background-color: transparent; -fx-cursor: hand;");
                deleteBtn.setOnAction(e -> {
                    tasks.remove(t);
                    refreshList[0].run();
                    updateTaskCount(tasks.size());
                });

                HBox row = new HBox(10, cb, spacer, deleteBtn);
                row.setAlignment(Pos.CENTER_LEFT);
                taskListView.getItems().add(row);
            }
        };

        refreshList[0].run();

        HBox inputArea = new HBox(10);
        TextField taskInput = new TextField();
        taskInput.setPromptText("New task...");
        Button addBtn = new Button("Add");
        addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        
        addBtn.setOnAction(e -> {
            String input = taskInput.getText().trim();
            if (!input.isEmpty()) {
                String taskId = "T-" + System.currentTimeMillis();
                StudentTask newTask = new StudentTask(taskId, currentStudentId, input, false);
                tasks.add(newTask);
                TaskService.getInstance().addTask(newTask);
                
                taskInput.clear();
                refreshList[0].run();
                updateTaskCount((int) TaskService.getInstance().getPendingTaskCount(currentStudentId));
            }
        });

        inputArea.getChildren().addAll(taskInput, addBtn);
        layout.getChildren().addAll(header, taskListView, inputArea);

        Scene scene = new Scene(layout, 350, 450);
        dialog.setScene(scene);
        dialog.show();
    }

    private void updateTaskCount(int count) {
        tasksDueCount.setText(String.valueOf(count));
    }
}
