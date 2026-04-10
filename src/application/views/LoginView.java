package application.views;

import application.controllers.LoginController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LoginView extends BorderPane {

    private LoginController controller;

    public LoginView() {
        this.controller = new LoginController();
        initUI();
    }

    private void initUI() {
        setPrefSize(760, 550);
        getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
        getStyleClass().add("root");

        // --- Left Side Graphics ---
        VBox leftPane = new VBox();
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPrefWidth(300);
        leftPane.getStyleClass().add("auth-left-pane");
        leftPane.setPadding(new Insets(40, 20, 40, 20));

        Label title1 = new Label("Library");
        title1.getStyleClass().add("auth-title");

        Label title2 = new Label("Management System");
        title2.getStyleClass().add("auth-subtitle");

        Label iconLabel = new Label("📚");
        iconLabel.getStyleClass().add("auth-icon");

        leftPane.getChildren().addAll(title1, title2, iconLabel);
        setLeft(leftPane);

        // --- Right Side Form ---
        VBox rightPane = new VBox();
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setSpacing(30);
        rightPane.setPadding(new Insets(40, 40, 40, 40));

        VBox welcomeBox = new VBox(5);
        welcomeBox.setAlignment(Pos.CENTER_LEFT);
        Text welcomeText = new Text("Welcome Back!");
        welcomeText.getStyleClass().add("title-text");
        Label welcomeLabel = new Label("Please log in to your account.");
        welcomeBox.getChildren().addAll(welcomeText, welcomeLabel);

        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(30, 30, 30, 30));

        // Email Field
        VBox emailBox = new VBox(5);
        Label emailLabel = new Label("Email Address");
        TextField txtEmail = new TextField();
        txtEmail.setPrefWidth(250);
        txtEmail.setPromptText("Enter registered email");
        emailBox.getChildren().addAll(emailLabel, txtEmail);

        // Password Field
        VBox passBox = new VBox(5);
        Label passLabel = new Label("Password");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPrefWidth(250);
        txtPassword.setPromptText("Enter password");
        passBox.getChildren().addAll(passLabel, txtPassword);

        // Login Button
        Button btnLogin = new Button("Login");
        btnLogin.setPrefWidth(250);
        btnLogin.getStyleClass().add("btn-primary");

        // Register Link
        HBox registerBox = new HBox(5);
        registerBox.setAlignment(Pos.CENTER);
        Label registerLabel = new Label("Don't have an account?");
        Button btnRegister = new Button("Register");
        btnRegister.getStyleClass().add("btn-secondary");
        btnRegister.setStyle("-fx-padding: 2 5;");
        registerBox.getChildren().addAll(registerLabel, btnRegister);

        // Status Label
        Label lblStatus = new Label();
        lblStatus.getStyleClass().add("status-label-error");
        lblStatus.setWrapText(true);

        card.getChildren().addAll(emailBox, passBox, btnLogin, registerBox, lblStatus);
        rightPane.getChildren().addAll(welcomeBox, card);
        setCenter(rightPane);

        // --- Wire Controller ---
        controller.setFields(txtEmail, txtPassword, lblStatus, btnLogin);
        btnLogin.setOnAction(controller::btnLoginAction);
        btnRegister.setOnAction(controller::switchToRegister);
    }
}
