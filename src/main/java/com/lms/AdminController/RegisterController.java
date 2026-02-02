package main.java.com.yourorg.lms.ui.controller;

import main.java.com.yourorg.lms.service.AuthService;
import main.java.com.yourorg.lms.ui.util.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private TextField extraField;

    private final AuthService authService = AuthService.getInstance();

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("STUDENT", "INSTRUCTOR");
        roleComboBox.setValue("STUDENT");
        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("STUDENT".equals(newVal)) {
                extraField.setPromptText("Student Number");
            } else if ("INSTRUCTOR".equals(newVal)) {
                extraField.setPromptText("Department");
            }
        });
    }

    @FXML
    private void handleRegister() {
        try {
            authService.register(
                roleComboBox.getValue(),
                java.util.UUID.randomUUID().toString().substring(0, 8),
                fullNameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                extraField.getText()
            );
            showAlert(Alert.AlertType.INFORMATION,
                    "Registration Successful",
                    "Account created successfully. Please log in.");

            ViewFactory.showLoginWindow();

        } catch (IllegalStateException e) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                    "System Error",
                    "An unexpected error occurred.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackToLogin() {
        ViewFactory.showLoginWindow();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
