package main.java.com.yourorg.lms.ui.controller;

import main.java.com.yourorg.lms.ui.util.AlertUtil;
// import javax.swing.text.ViewFactory; // Removed incorrect import
import main.java.com.yourorg.lms.ui.util.ViewFactory; // Add your custom ViewFactory import (adjust the package as needed)

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import main.java.com.yourorg.lms.model.user.User;
import main.java.com.yourorg.lms.repository.impl.FileUserRepository;
import main.java.com.yourorg.lms.util.SessionManager;

public class AdminDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private VBox mainContainer; 
    @FXML private Label totalUsersLabel;
    @FXML private TextField searchField; // Connect this to your FXML search bar

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;

    private final ObservableList<User> masterUserData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        User admin = SessionManager.getCurrentUser();
        if (admin != null) {
            welcomeLabel.setText("Admin: " + admin.getName());
        }

        setupTableColumns();
        loadUserData();
        setupSearchFilter();
    }

    private void setupTableColumns() {
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadUserData() {
        masterUserData.clear();
        masterUserData.addAll(FileUserRepository.getInstance().findAll());
        
        userTable.setItems(masterUserData);
        totalUsersLabel.setText(String.valueOf(masterUserData.size()));
    }

    /**
     * Senior Logic: Real-time Search Filter
     * Filters by Name, Email, or Role as the user types.
     */
    private void setupSearchFilter() {
        FilteredList<User> filteredData = new FilteredList<>(masterUserData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                if (user.getName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (user.getEmail().toLowerCase().contains(lowerCaseFilter)) return true;
                return user.getRole().toLowerCase().contains(lowerCaseFilter);
            });
        });
        SortedList<User> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(userTable.comparatorProperty());
        userTable.setItems(sortedData);
    }

    @FXML
    private void handleAddUser() {
        System.out.println("[ADMIN] Opening Add User Dialog...");
        ViewFactory.showUserCreationModal(); 
        loadUserData();
    }

    @FXML 
    private void handleRootClick() {
      if (mainContainer != null) {
            mainContainer.requestFocus();
            mainContainer.getScene().getRoot().requestFocus();
            System.out.println("Focus cleared from search bar.");
        } 
    }

    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            FileUserRepository.getInstance().delete(selected.getId());
            loadUserData(); 
            
            AlertUtil.show(Alert.AlertType.INFORMATION, "Deleted", "User has been removed from the system.");
        }
    }
}