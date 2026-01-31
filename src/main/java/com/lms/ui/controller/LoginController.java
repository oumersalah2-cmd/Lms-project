package main.java.com.lms.ui.controller;

import main.java.com.lms.model.user.User;
import main.java.com.lms.service.AuthService;
import main.java.com.lms.ui.util.ViewFactory;
import main.java.com.lms.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private final AuthService authService = AuthService.getInstance();

    @FXML
    private void onLoginClicked() {
        try {
            authService.login(
                    emailField.getText(),
                    passwordField.getText()
            );

            User user = SessionManager.getCurrentUser();
            System.out.println("[DEBUG] Logged in as role: " + user.getRole());

            switch (user.getRole()) {
                case "STUDENT" -> ViewFactory.showStudentDashboard();
                case "ADMIN" -> ViewFactory.showAdminDashboard();
                case "INSTRUCTOR" -> ViewFactory.showInstructorDashboard();
                default -> throw new IllegalStateException("Unsupported role");
            }

        } catch (AuthService.UserNotFoundException |
                 AuthService.InvalidCredentialsException e) {

            showError(e.getMessage());
        }
    }

    @FXML
    private void onRegisterLinkClicked() {
        ViewFactory.showRegisterWindow(); 
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
