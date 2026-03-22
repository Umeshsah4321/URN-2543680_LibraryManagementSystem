package application.controllers;

import application.connection;
import application.models.UserModel;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import application.views.LoginView;
import application.views.RegisterView;
import application.views.StudentDashboardView;
import application.views.LibrarianDashboardView;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class loginController {

    private LoginView view;

    // A static variable to hold the logged in user across the application
    public static UserModel loggedInUser;

    public loginController(LoginView view) {
        this.view = view;
        initHandlers();
    }

    private void initHandlers() {
        view.getBtnLogin().setOnAction(this::btnLoginAction);
        view.getBtnRegister().setOnAction(this::switchToRegister);
    }

    private void btnLoginAction(ActionEvent event) {
        String email = view.getTxtEmail().getText().trim();
        String password = view.getTxtPassword().getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            view.getLblStatus().setText("Please enter both email and password.");
            return;
        }

        Connection conn = connection.getConnection();
        if (conn == null) {
            view.getLblStatus().setText("DB Connection failed. Check console for JDBC errors.");
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

                    StudentDashboardView dashboardView = new StudentDashboardView();
                    new studentDashboardController(dashboardView);
                    Stage stage = (Stage) view.getScene().getWindow();
                    stage.setScene(new Scene(dashboardView, 900, 600));
                    stage.centerOnScreen();
                    stage.setTitle("Library Management System - Student Dashboard");
                } else {
                    PreparedStatement pstLib = conn.prepareStatement("SELECT librarian_id FROM librarians WHERE user_id = ?");
                    pstLib.setInt(1, id);
                    ResultSet rsLib = pstLib.executeQuery();
                    if(rsLib.next()) loggedInUser.setSpecId(rsLib.getInt("librarian_id"));

                    LibrarianDashboardView dashboardView = new LibrarianDashboardView();
                    new librarianDashboardController(dashboardView);
                    Stage stage = (Stage) view.getScene().getWindow();
                    stage.setScene(new Scene(dashboardView, 900, 600));
                    stage.centerOnScreen();
                    stage.setTitle("Library Management System - Librarian Dashboard");
                }

            } else {
                view.getLblStatus().setText("Invalid credentials!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            view.getLblStatus().setText("DB Error: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception e) {}
            }
        }
    }

    private void switchToRegister(ActionEvent event) {
        RegisterView regView = new RegisterView();
        new registerController(regView);
        Stage stage = (Stage) view.getScene().getWindow();
        stage.setScene(new Scene(regView));
        stage.setTitle("Library Management System - Register");
    }
}
