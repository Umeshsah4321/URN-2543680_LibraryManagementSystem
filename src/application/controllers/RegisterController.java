package application.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import application.models.ConnectionUtil;
import application.views.LoginView;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    private TextField txtName;
    private TextField txtEmail;
    private PasswordField txtPassword;
    private TextField txtPhone;
    private TextField txtAddress;
    private ComboBox<String> cmbRole;
    private Label lblStatus;
    private Button btnSubmit;

    /**
     * Set fields manually since we're not using FXML injection.
     */
    public void setFields(TextField txtName, TextField txtEmail, PasswordField txtPassword, TextField txtPhone,
            TextField txtAddress, ComboBox<String> cmbRole, Label lblStatus, Button btnSubmit) {
        this.txtName = txtName;
        this.txtEmail = txtEmail;
        this.txtPassword = txtPassword;
        this.txtPhone = txtPhone;
        this.txtAddress = txtAddress;
        this.cmbRole = cmbRole;
        this.lblStatus = lblStatus;
        this.btnSubmit = btnSubmit;
    }

    public void initialize() {
        if (cmbRole != null) {
            cmbRole.getSelectionModel().selectFirst();
        }
    }

    public void btnSubmitAction() {
        try {
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String pass = txtPassword.getText().trim();
            String phone = txtPhone.getText().trim();
            String address = txtAddress.getText().trim();
            String role = cmbRole.getValue();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || role == null) {
                lblStatus.setText("Name, Email, Password, and Role are mandatory.");
                return;
            }

            Connection conn = ConnectionUtil.getConnection();
            if (conn == null) {
                lblStatus.setText("DB Connection failed. Check console for JDBC errors.");
                return;
            }

            try {
                conn.setAutoCommit(false); // Transactions to avoid partial saves

                String sqlUser = "INSERT INTO users(name, email, password, phone, address, role) VALUES (?,?,?,?,?,?)";
                PreparedStatement pstUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
                pstUser.setString(1, name);
                pstUser.setString(2, email);
                pstUser.setString(3, pass); // In a real app, hash the password
                pstUser.setString(4, phone);
                pstUser.setString(5, address);
                pstUser.setString(6, role);

                int affected = pstUser.executeUpdate();

                if (affected > 0) {
                    ResultSet rs = pstUser.getGeneratedKeys();
                    if(rs.next()){
                        int newUserId = rs.getInt(1);

                        if (role.equals("STUDENT")) {
                            String sqlStudent = "INSERT INTO students(user_id) VALUES (?)";
                            PreparedStatement pstStudent = conn.prepareStatement(sqlStudent);
                            pstStudent.setInt(1, newUserId);
                            pstStudent.executeUpdate();
                        } else if (role.equals("LIBRARIAN")) {
                            String sqlLib = "INSERT INTO librarians(user_id) VALUES (?)";
                            PreparedStatement pstLib = conn.prepareStatement(sqlLib);
                            pstLib.setInt(1, newUserId);
                            pstLib.executeUpdate();
                        }
                    }
                    conn.commit();
                    lblStatus.setStyle("-fx-text-fill: green;");
                    lblStatus.setText("User Registered Successfully!");

                    // Clear fields
                    txtName.clear();
                    txtEmail.clear();
                    txtPassword.clear();
                    txtPhone.clear();
                    txtAddress.clear();
                }
            } catch (Exception dbErr) {
                try {
                    if (conn != null) {
						conn.rollback();
					}
                } catch (Exception rollbackErr) {}
                lblStatus.setText("DB Error: " + dbErr.getMessage());
            } finally {
                if (conn != null) {
                    try { conn.close(); } catch (Exception e) {}
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblStatus.setText("Error: " + e.getMessage());
        }
    }

    public void switchToLogin() {
        Stage stage = (Stage) btnSubmit.getScene().getWindow();
        stage.setScene(new Scene(new LoginView()));
        stage.setTitle("Library Management System - Login");
    }
}