package main.java.com.yourorg.lms.ui.controller;


import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.com.yourorg.lms.model.user.User;
import main.java.com.yourorg.lms.model.user.UserFactory;
import main.java.com.yourorg.lms.repository.impl.FileUserRepository;
import main.java.com.yourorg.lms.util.SecurityUtil;

public class UserCreationController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleBox;
    @FXML private PasswordField passwordField;

    @FXML
    public void initialize() {
        roleBox.getItems().addAll("STUDENT", "INSTRUCTOR", "ADMIN");
    }

   

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String role = roleBox.getValue();
        String rawPassword = passwordField.getText();

        if (name.isEmpty() || email.isEmpty() || role == null || rawPassword.isEmpty()) {
            // Show an alert if fields are empty
            return;
        }

        // 1. Hash the password before saving
        String hashedPassword = SecurityUtil.hash(rawPassword);
        String id = "U-" + System.currentTimeMillis();

        // 2. Create and Save
        User newUser = UserFactory.loadUser(role, id, name, email, hashedPassword, "-");
        FileUserRepository.getInstance().save(newUser);

        // 3. IMPORTANT: Refresh the Dashboard Cache
        FileUserRepository.getInstance().refreshCache();

        // 4. Close the window
        handleCancel();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}