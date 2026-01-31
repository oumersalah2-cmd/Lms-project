package main.java.com.lms.ui.util;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class ViewFactory {

    private static Stage primaryStage;
    private static final String VIEW_PATH = "/resource/view/";

    private ViewFactory() {}

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        // 1. Set a professional default size
        primaryStage.setWidth(1280);
        primaryStage.setHeight(800);
        
        // 2. Optional: Set a minimum size so the UI doesn't break
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        
        // 3. Optional: Start maximized if you prefer
        // primaryStage.setMaximized(true); 
    }

    public static void showLoginWindow() {
        loadScene(VIEW_PATH + "LoginView.fxml", "LMS - Login");
    }

    public static void showStudentDashboard() {
        loadScene(VIEW_PATH + "StudentDashboardView.fxml", "Student Dashboard");
    }

    public static void showRegisterWindow() {
        loadScene(VIEW_PATH + "RegisterView.fxml", "LMS - Register");
    }

    public static void showInstructorDashboard() {
        loadScene(VIEW_PATH + "InstructorDashboard.fxml", "Instructors Portal");
    }
        public static void showAdminDashboard() {
        loadScene(VIEW_PATH + "AdminDashboard.fxml", "Admins Control Center");
    }


    public static void showStudentCourses() {
        loadScene("/resource/view/StudentCoursesView.fxml", "My Courses");
    }

    public static void showInstructorCourses() {
        loadScene("/resource/view/InstructorManagementView.fxml", "Manage My Classes");
    }

    public static void showAdminCourseCatalog() {
        loadScene("/resource/view/AdminCatalogView.fxml", "System Course Catalog");
    }

    public static void showSettings() {
        loadScene("/resource/view/SettingsView.fxml", "Account Settings");
    }

    public static void showUserCreationModal() {
    try {
        // FIX: Use the VIEW_PATH constant so it looks in the same folder as your other views
        // Make sure UserCreationModal.fxml is actually inside /resource/view/
        URL xmlLocation = ViewFactory.class.getResource(VIEW_PATH + "UserCreationModal.fxml");

        if (xmlLocation == null) {
            throw new IllegalStateException("Location is not set. Could not find UserCreationModal.fxml in " + VIEW_PATH);
        }

        FXMLLoader loader = new FXMLLoader(xmlLocation);
        
        Stage stage = new Stage();
        stage.setTitle("Create New User");
        stage.initModality(Modality.APPLICATION_MODAL);
        
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.showAndWait();
    } catch (IOException e) {
        System.err.println("Could not open User Creation Modal: " + e.getMessage());
        e.printStackTrace();
    }
}
    /**
     * The Engine: Loads the FXML and sets it to the stage.
     */
    private static void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewFactory.class.getResource(fxmlPath));
            Parent root = loader.load();

            if (primaryStage.getScene() == null) {
                // First load (Login)
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                // Switching scenes (Dashboard, etc.)
                // By setting the root, we keep the window's current size/maximized state
                primaryStage.getScene().setRoot(root);
            }

            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}