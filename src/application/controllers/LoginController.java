package application.controllers;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import application.models.ConnectionUtil;
import application.models.UserModel;
import application.views.LibrarianDashboardView;
import application.views.RegisterView;
import application.views.StudentDashboardView;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    private TextField txtEmail;
    private PasswordField txtPassword;
    private Label lblStatus;
    private Button btnLogin;

    // A static variable to hold the logged in user across the application
    public static UserModel loggedInUser;

    /**
     * Set fields manually since we're not using FXML injection.
     */
    public void setFields(TextField txtEmail, PasswordField txtPassword, Label lblStatus, Button btnLogin) {
        this.txtEmail = txtEmail;
        this.txtPassword = txtPassword;
        this.lblStatus = lblStatus;
        this.btnLogin = btnLogin;
    }

    public void btnLoginAction(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Please enter both email and password.");
            return;
        }

        Connection conn = ConnectionUtil.getConnection();
        if (conn == null) {
            lblStatus.setText("DB Connection failed. Check console for JDBC errors.");
            return;
        }

        try {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("user_id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String role = rs.getString("role");

                loggedInUser = new UserModel(id, name, email, password, phone, address, role);

                // Fetch specific role ID (Student ID or Librarian ID)
                if (role.equals("STUDENT")) {
                    PreparedStatement pstStud = conn.prepareStatement("SELECT student_id FROM students WHERE user_id = ?");
                    pstStud.setInt(1, id);
                    ResultSet rsStud = pstStud.executeQuery();
                    if(rsStud.next()) {
						loggedInUser.setSpecId(rsStud.getInt("student_id"));
					}
                    loadStudentDashboard();
                } else {
                    PreparedStatement pstLib = conn.prepareStatement("SELECT librarian_id FROM librarians WHERE user_id = ?");
                    pstLib.setInt(1, id);
                    ResultSet rsLib = pstLib.executeQuery();
                    if(rsLib.next()) {
						loggedInUser.setSpecId(rsLib.getInt("librarian_id"));
					}
                    loadLibrarianDashboard();
                }

            } else {
                lblStatus.setText("Invalid credentials!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            lblStatus.setText("DB Error: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception e) {}
            }
        }
    }

    public void switchToRegister(ActionEvent event) {
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        stage.setScene(new Scene(new RegisterView()));
        stage.setTitle("Library Management System - Register");
    }

    private void loadLibrarianDashboard() {
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        stage.setScene(new Scene(new LibrarianDashboardView(), 1100, 750));
        stage.centerOnScreen();
        stage.setTitle("Library Management System - Librarian Dashboard");
    }

    private void loadStudentDashboard() {
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        stage.setScene(new Scene(new StudentDashboardView(), 1100, 750));
        stage.centerOnScreen();
        stage.setTitle("Library Management System - Student Dashboard");
    }
}
