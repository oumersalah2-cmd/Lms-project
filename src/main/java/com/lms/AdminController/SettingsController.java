package main.java.com.yourorg.lms.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.java.com.yourorg.lms.model.user.User;
import main.java.com.yourorg.lms.repository.impl.FileUserRepository;
import main.java.com.yourorg.lms.util.SecurityUtil;
import main.java.com.yourorg.lms.util.SessionManager;

public class SettingsController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField roleField;
    
    @FXML private PasswordField currentPassField;
    @FXML private PasswordField newPassField;

    @FXML
    public void initialize() {
        User current = SessionManager.getCurrentUser();
        if (current != null) {
            nameField.setText(current.getName());
            emailField.setText(current.getEmail());
            roleField.setText(current.getRole().toUpperCase());
            
            // Pro Tip: Email and Role should be read-only in a standard LMS
            emailField.setEditable(false);
            roleField.setEditable(false);
            emailField.setStyle("-fx-opacity: 0.7; -fx-background-color: #f4f4f4;");
            roleField.setStyle("-fx-opacity: 0.7; -fx-background-color: #f4f4f4;");
        }
    }

    @FXML
    private void handleSave() {
        User current = SessionManager.getCurrentUser();
        String newName = nameField.getText().trim();
        String newPassInput = newPassField.getText();

        if (newName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Name cannot be empty!");
            return;
        }

       try {
        current.setFullName(newName);
        
        if (!newPassInput.isEmpty()) {
            // IMPORTANT: Update the password hash field in your model
            current.setPasswordHash(SecurityUtil.hash(newPassInput)); 
        }

        // PERSIST: This triggers the rewriteFile() logic we added to the Repo
        FileUserRepository.getInstance().update(current);
        
        showAlert(Alert.AlertType.INFORMATION, "Success", "Profile and Password updated!");
    } catch (Exception e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Save failed.");
    } 
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void handleCancel() {
        // Re-run the initialization logic to restore original data
        User current = SessionManager.getCurrentUser();
        if (current != null) {
            nameField.setText(current.getName());
            currentPassField.clear();
            newPassField.clear();
            
            // Optional: Show a brief notification in the console or a small label
            System.out.println("[SETTINGS] Changes discarded.");
        }
    }
}