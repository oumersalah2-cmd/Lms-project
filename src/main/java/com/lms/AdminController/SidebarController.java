package main.java.com.yourorg.lms.ui.controller;

import java.util.Arrays;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import main.java.com.yourorg.lms.model.user.User;
import main.java.com.yourorg.lms.ui.util.ViewFactory;
import main.java.com.yourorg.lms.util.SessionManager;

public class SidebarController {

    @FXML private Button dashboardBtn;
    @FXML private Button coursesBtn;
    @FXML private Button teachingBtn;
    @FXML private Button adminBtn;
    @FXML private Button settingsBtn;
    @FXML private Label userNameLabel;
    @FXML private javafx.scene.layout.VBox sidebarVBox;



    public void initialize() {
        User current = SessionManager.getCurrentUser();

       
        
        if (current != null) {
            userNameLabel.setText("Welcome, " + current.getName());
            configureVisibility(current.getRole().toUpperCase());
        
            String userRole = current.getRole();
            configureVisibility(userRole);
        }
    }

    private void configureVisibility(String role) {
        boolean isAdmin = role.equals("ADMIN");
        boolean isInstructor = role.equals("INSTRUCTOR");
        boolean isStudent = role.equals("STUDENT");

        // Admin Tools only for Admin
        adminBtn.setVisible(isAdmin);
        adminBtn.setManaged(isAdmin);

        // Instructor-specific button
        teachingBtn.setVisible(isInstructor);
        teachingBtn.setManaged(isInstructor);

        // Student-specific button (Courses view)
        coursesBtn.setVisible(isStudent);
        coursesBtn.setManaged(isStudent);
    }

    @FXML
    private void handleDashboard() {
        highlightActiveButton(dashboardBtn);
        User current = SessionManager.getCurrentUser();
        if (current == null) return;

        // Routing logic: Dashboard is the "Home" for each role
        switch (current.getRole().toUpperCase()) {
            case "ADMIN" -> ViewFactory.showAdminDashboard(); // The User Management page you have
            case "INSTRUCTOR" -> ViewFactory.showInstructorDashboard();
            default -> ViewFactory.showStudentDashboard();
        }
    }

    /**
     * Specifically for the "Admin Tools" button.
     * Separates management from the main overview.
     */
    @FXML
    private void handleAdminTools() {
        highlightActiveButton(adminBtn);
        ViewFactory.showAdminCourseCatalog(); 
    }

    @FXML
    private void handleCourses() {
        highlightActiveButton(coursesBtn);
        User current = SessionManager.getCurrentUser();
        if (current == null) return;

        // Role-based course view
        switch (current.getRole().toUpperCase()) {
            case "STUDENT" -> ViewFactory.showStudentCourses();
            case "INSTRUCTOR" -> ViewFactory.showInstructorCourses();
            case "ADMIN" -> ViewFactory.showAdminCourseCatalog(); // Safety fallback
        }
    }

    @FXML
    private void handleSettings() {
        highlightActiveButton(settingsBtn);
        ViewFactory.showSettings();
    }

    @FXML
    private void handleLogout() {
        SessionManager.logout(); 
        ViewFactory.showLoginWindow(); 
    }

    private void highlightActiveButton(Button activeButton) {
        List<Button> allButtons = Arrays.asList(dashboardBtn, coursesBtn, teachingBtn, adminBtn, settingsBtn);
        
        for (Button btn : allButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("active-sidebar-btn");
            }
        }
        
        if (activeButton != null) {
            activeButton.getStyleClass().add("active-sidebar-btn");
        }
    }

    @FXML
    private void toggleSidebar() {
        boolean collapsing = sidebarVBox.getPrefWidth() > 70;
        double targetWidth = collapsing ? 60 : 220;

        // 1. Set the new width
        sidebarVBox.setPrefWidth(targetWidth);

        // 2. Handle the text/labels
        if (collapsing) {
            dashboardBtn.setText("🏠");
            coursesBtn.setText("📚");
            teachingBtn.setText("👨‍🏫");
            settingsBtn.setText("⚙️");
            userNameLabel.setVisible(false);
        } else {
            dashboardBtn.setText("🏠  Dashboard");
            coursesBtn.setText("📚  My Courses");
            teachingBtn.setText("👨‍🏫  Teaching");
            settingsBtn.setText("⚙️  Settings");
            userNameLabel.setVisible(true);
        }
    }
}