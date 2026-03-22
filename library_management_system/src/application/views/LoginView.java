package application.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class LoginView extends BorderPane {

    private TextField txtEmail;
    private PasswordField txtPassword;
    private Button btnLogin;
    private Button btnRegister;
    private Label lblStatus;

    public LoginView() {
        // Root BorderPane setup
        this.setPrefSize(760, 550);
        this.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
        this.getStyleClass().add("root");

        // LEFT SIDE: Branding Sidebar
        VBox leftSidebar = new VBox();
        leftSidebar.setAlignment(Pos.CENTER);
        leftSidebar.setPrefWidth(300);
        leftSidebar.setStyle("-fx-background-color: linear-gradient(to bottom right, #3498db, #2c3e50);");
        leftSidebar.setPadding(new Insets(40, 20, 40, 20));

        Text titlePart1 = new Text("Library");
        titlePart1.setStyle("-fx-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");

        Text titlePart2 = new Text("Management System");
        titlePart2.setStyle("-fx-fill: #ecf0f1; -fx-font-size: 20px;");

        Label bookIcon = new Label("📚");
        bookIcon.setStyle("-fx-font-size: 72px; -fx-text-fill: white; -fx-padding: 30 0 0 0;");

        leftSidebar.getChildren().addAll(titlePart1, titlePart2, bookIcon);
        this.setLeft(leftSidebar);

        // CENTER: Login Form
        VBox centerContent = new VBox(30);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(40, 40, 40, 40));

        // Welcome text section
        VBox welcomeHeader = new VBox(5);
        welcomeHeader.setAlignment(Pos.CENTER_LEFT);
        Text welcomeTxt = new Text("Welcome Back!");
        welcomeTxt.getStyleClass().add("title-text");
        Label subTxt = new Label("Please log in to your account.");
        welcomeHeader.getChildren().addAll(welcomeTxt, subTxt);

        // Login Card
        VBox loginCard = new VBox(15);
        loginCard.getStyleClass().add("card");
        loginCard.setPadding(new Insets(30, 30, 30, 30));

        // Email field section
        VBox emailBox = new VBox(5);
        Label emailLabel = new Label("Email Address");
        txtEmail = new TextField();
        txtEmail.setPrefWidth(250);
        txtEmail.setPromptText("Enter registered email");
        emailBox.getChildren().addAll(emailLabel, txtEmail);

        // Password field section
        VBox passwordBox = new VBox(5);
        Label passwordLabel = new Label("Password");
        txtPassword = new PasswordField();
        txtPassword.setPrefWidth(250);
        txtPassword.setPromptText("Enter password");
        passwordBox.getChildren().addAll(passwordLabel, txtPassword);

        // Login Button
        btnLogin = new Button("Login");
        btnLogin.setPrefWidth(250);
        btnLogin.getStyleClass().add("btn-primary");

        // Register Link
        HBox registerLinkBox = new HBox(5);
        registerLinkBox.setAlignment(Pos.CENTER);
        Label registerPrompt = new Label("Don't have an account?");
        btnRegister = new Button("Register");
        btnRegister.getStyleClass().add("btn-secondary");
        btnRegister.setStyle("-fx-padding: 2 5;");
        registerLinkBox.getChildren().addAll(registerPrompt, btnRegister);

        // Status Label
        lblStatus = new Label();
        lblStatus.getStyleClass().add("status-label-error");
        lblStatus.setWrapText(true);

        loginCard.getChildren().addAll(emailBox, passwordBox, btnLogin, registerLinkBox, lblStatus);
        centerContent.getChildren().addAll(welcomeHeader, loginCard);
        this.setCenter(centerContent);
    }

    // Getters for controller interaction
    public TextField getTxtEmail() { return txtEmail; }
    public PasswordField getTxtPassword() { return txtPassword; }
    public Button getBtnLogin() { return btnLogin; }
    public Button getBtnRegister() { return btnRegister; }
    public Label getLblStatus() { return lblStatus; }
}
