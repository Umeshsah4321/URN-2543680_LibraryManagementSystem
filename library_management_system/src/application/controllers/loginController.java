package application.controllers;

import application.connection;
import application.models.UserModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class loginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblStatus;
    @FXML private Button btnLogin;

    // A static variable to hold the logged in user across the application
    public static UserModel loggedInUser;

    @FXML
    private void btnLoginAction(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Please enter both email and password.");
            return;
        }

        Connection conn = connection.getConnection();
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
                    if(rsStud.next()) loggedInUser.setSpecId(rsStud.getInt("student_id"));
                    loadDashboard("/application/views/student_dashboard.fxml", "Student Dashboard");
                } else {
                    PreparedStatement pstLib = conn.prepareStatement("SELECT librarian_id FROM librarians WHERE user_id = ?");
                    pstLib.setInt(1, id);
                    ResultSet rsLib = pstLib.executeQuery();
                    if(rsLib.next()) loggedInUser.setSpecId(rsLib.getInt("librarian_id"));
                    loadDashboard("/application/views/librarian_dashboard.fxml", "Librarian Dashboard");
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

    @FXML
    private void switchToRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Library Management System - Register");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadDashboard(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600)); // Larger window for dashboards
            stage.centerOnScreen();
            stage.setTitle("Library Management System - " + title);
        } catch (IOException e) {
            e.printStackTrace();
            lblStatus.setText("Error loading dashboard view: " + e.getMessage());
        }
    }
}
