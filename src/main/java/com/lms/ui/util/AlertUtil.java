package main.java.com.lms.ui.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * AlertUtil
 * * A simple wrapper around JavaFX Alerts to reduce boilerplate code
 * in controllers.
 */
public final class AlertUtil {

    private AlertUtil() {
        // Prevent instantiation
    }

    /**
     * Shows a simple alert dialog.
     * * @param type    The type of alert (INFORMATION, ERROR, WARNING, etc.)
     * @param title   The title of the window
     * @param content The message to display to the user
     */
    public static void show(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header looks cleaner in modern UIs
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Shows a confirmation dialog with OK/Cancel buttons.
     * * @param title   The title of the window
     * @param content The question to ask the user
     * @return true if the user clicked OK, false otherwise
     */
    public static boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}