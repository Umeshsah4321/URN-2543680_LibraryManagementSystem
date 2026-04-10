package application.views;

import application.controllers.RegisterController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RegisterView extends BorderPane {

    private RegisterController controller;

    public RegisterView() {
        this.controller = new RegisterController();
        initUI();
    }

    private void initUI() {
        setPrefSize(760, 700);
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

        // --- Center Content ---
        VBox centerPane = new VBox(20);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setPadding(new Insets(20, 40, 20, 40));

        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Text headText = new Text("Create Account");
        headText.getStyleClass().add("title-text");
        Label subHead = new Label("Register as a new library member.");
        titleBox.getChildren().addAll(headText, subHead);

        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(25, 30, 25, 30));

        // Row 1: Name and Phone
        HBox row1 = new HBox(15);
        VBox nameBox = new VBox(5);
        Label nameLabel = new Label("Full Name");
        TextField txtName = new TextField();
        txtName.setPromptText("Enter full name");
        VBox.setVgrow(nameBox, Priority.ALWAYS);
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        nameBox.getChildren().addAll(nameLabel, txtName);

        VBox phoneBox = new VBox(5);
        Label phoneLabel = new Label("Phone Number");
        TextField txtPhone = new TextField();
        txtPhone.setPromptText("Enter phone number");
        HBox.setHgrow(phoneBox, Priority.ALWAYS);
        phoneBox.getChildren().addAll(phoneLabel, txtPhone);
        row1.getChildren().addAll(nameBox, phoneBox);

        // Row 2: Email
        VBox emailBox = new VBox(5);
        Label emailLabel = new Label("Email Address");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Enter email address");
        emailBox.getChildren().addAll(emailLabel, txtEmail);

        // Row 3: Password
        VBox passBox = new VBox(5);
        Label passLabel = new Label("Password");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Create password");
        passBox.getChildren().addAll(passLabel, txtPassword);

        // Row 4: Address and Role
        HBox row4 = new HBox(15);
        VBox addrBox = new VBox(5);
        Label addrLabel = new Label("Address");
        TextField txtAddress = new TextField();
        txtAddress.setPromptText("Enter full address");
        HBox.setHgrow(addrBox, Priority.ALWAYS);
        addrBox.getChildren().addAll(addrLabel, txtAddress);

        VBox roleBox = new VBox(5);
        roleBox.setPrefWidth(150);
        Label roleLabel = new Label("Account Role");
        ComboBox<String> cmbRole = new ComboBox<>(FXCollections.observableArrayList("STUDENT", "LIBRARIAN"));
        cmbRole.setMaxWidth(Double.MAX_VALUE);
        roleBox.getChildren().addAll(roleLabel, cmbRole);
        row4.getChildren().addAll(addrBox, roleBox);

        // Submit Button and toggle
        VBox actionBox = new VBox(10);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(10, 0, 0, 0));
        Button btnSubmit = new Button("Register Account");
        btnSubmit.setPrefWidth(300);
        btnSubmit.getStyleClass().add("btn-primary");

        HBox loginLinkBox = new HBox(5);
        loginLinkBox.setAlignment(Pos.CENTER);
        Label hasAccLabel = new Label("Already have an account?");
        Button btnLoginLink = new Button("Login here");
        btnLoginLink.getStyleClass().add("btn-secondary");
        btnLoginLink.setStyle("-fx-padding: 2 5;");
        loginLinkBox.getChildren().addAll(hasAccLabel, btnLoginLink);

        Label lblStatus = new Label();
        lblStatus.getStyleClass().add("status-label-error");

        actionBox.getChildren().addAll(btnSubmit, loginLinkBox, lblStatus);

        card.getChildren().addAll(row1, emailBox, passBox, row4, actionBox);
        centerPane.getChildren().addAll(titleBox, card);
        setCenter(centerPane);

        // --- Wire Controller ---
        controller.setFields(txtName, txtEmail, txtPassword, txtPhone, txtAddress, cmbRole, lblStatus, btnSubmit);
        btnSubmit.setOnAction(e -> controller.btnSubmitAction());
        btnLoginLink.setOnAction(e -> controller.switchToLogin());


        controller.initialize();
    }
}
