package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.dao.UserDAO;
import org.example.model.User;

public class MainController {
    @FXML private TableView<User> table;
    @FXML private TableColumn<User, Number> colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TextField tfName;
    @FXML private TextField tfEmail;
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    private UserDAO dao = new UserDAO();
    private ObservableList<User> users = FXCollections.observableArrayList();
    @FXML
    public void initialize() {
        colId.setCellValueFactory(cell -> cell.getValue().idProperty());
        colName.setCellValueFactory(cell -> cell.getValue().nameProperty());
        colEmail.setCellValueFactory(cell -> cell.getValue().emailProperty());
        loadUsers();
        table.getSelectionModel().selectedItemProperty().addListener((obs,
                                                                      oldSel, newSel) -> {
            if (newSel != null) {
                tfName.setText(newSel.getName());
                tfEmail.setText(newSel.getEmail());
            }
        });
        btnAdd.setOnAction(e -> {
            String name = tfName.getText().trim();
            String email = tfEmail.getText().trim();
            if (!name.isEmpty() && !email.isEmpty()) {
                dao.addUser(name, email);
                loadUsers();
                clearForm();
            }
        });
        btnUpdate.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                dao.updateUser(sel.getId(), tfName.getText().trim(),
                        tfEmail.getText().trim());
                loadUsers();
            }
        });
        btnDelete.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                dao.deleteUser(sel.getId());
                loadUsers();
                clearForm();
            }
        });
    }
    private void loadUsers() {
        users.setAll(dao.getAllUsers());
        table.setItems(users);
    }
    private void clearForm() {
        tfName.clear();
        tfEmail.clear();
        table.getSelectionModel().clearSelection();
    }
}
