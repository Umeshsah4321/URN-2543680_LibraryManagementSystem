package application.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import application.connection;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import application.views.RegisterView;
import application.views.LoginView;

public class registerController {

    private RegisterView view;

    public registerController(RegisterView view) {
        this.view = view;
        initHandlers();
        initialize();
    }

    private void initHandlers() {
        view.getBtnSubmit().setOnAction(e -> btnSubmitAction());
        view.getBtnLogin().setOnAction(e -> switchToLogin());
    }

    private void initialize() {
        if (view.getCmbRole() != null) {
            view.getCmbRole().getSelectionModel().selectFirst();
        }
    }

    private void btnSubmitAction() {
        try {
            String name = view.getTxtName().getText().trim();
            String email = view.getTxtEmail().getText().trim();
            String pass = view.getTxtPassword().getText().trim();
            String phone = view.getTxtPhone().getText().trim();
            String address = view.getTxtAddress().getText().trim();
            String role = view.getCmbRole().getValue();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || role == null) {
                view.getLblStatus().setText("Name, Email, Password, and Role are mandatory.");
                return;
            }

            Connection conn = connection.getConnection();
            if (conn == null) {
                view.getLblStatus().setText("DB Connection failed. Check console for JDBC errors.");
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
                    view.getLblStatus().setStyle("-fx-text-fill: green;");
                    view.getLblStatus().setText("User Registered Successfully!");

                    // Clear fields
                    view.getTxtName().clear();
                    view.getTxtEmail().clear();
                    view.getTxtPassword().clear();
                    view.getTxtPhone().clear();
                    view.getTxtAddress().clear();
                } else {
                    // Original code had an empty else block here, keeping it for faithfulness
                }
            } catch (Exception dbErr) {
                try {
                    if (conn != null) {
						conn.rollback();
					}
                } catch (Exception rollbackErr) {}
                view.getLblStatus().setText("DB Error: " + dbErr.getMessage());
            } finally {
                if (conn != null) {
                    try { conn.close(); } catch (Exception e) {}
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            view.getLblStatus().setText("Error: " + e.getMessage());
        }
    }

    private void switchToLogin() {
        LoginView loginView = new LoginView();
        new loginController(loginView); // Assuming loginController exists and takes LoginView
        Stage stage = (Stage) view.getBtnSubmit().getScene().getWindow();
        stage.setScene(new Scene(loginView)); // LoginView should be a Parent or extend Parent
        stage.setTitle("Library Management System - Login");
    }
}