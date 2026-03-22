package application.views;

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

    private TextField txtName;
    private TextField txtPhone;
    private TextField txtEmail;
    private PasswordField txtPassword;
    private TextField txtAddress;
    private ComboBox<String> cmbRole;
    private Button btnSubmit;
    private Button btnLogin;
    private Label lblStatus;

    public RegisterView() {
        this.setPrefSize(760, 700);
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

        // CENTER: Registration Form
        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(20, 40, 20, 40));

        VBox welcomeHeader = new VBox(5);
        welcomeHeader.setAlignment(Pos.CENTER_LEFT);
        Text welcomeTxt = new Text("Create Account");
        welcomeTxt.getStyleClass().add("title-text");
        Label subTxt = new Label("Register as a new library member.");
        welcomeHeader.getChildren().addAll(welcomeTxt, subTxt);

        // Registration Card
        VBox registerCard = new VBox(15);
        registerCard.getStyleClass().add("card");
        registerCard.setPadding(new Insets(25, 30, 25, 30));

        // Name and Phone Row
        HBox row1 = new HBox(15);
        VBox nameBox = new VBox(5);
        Label nameLabel = new Label("Full Name");
        txtName = new TextField();
        txtName.setPromptText("Enter full name");
        nameBox.getChildren().addAll(nameLabel, txtName);
        HBox.setHgrow(nameBox, Priority.ALWAYS);

        VBox phoneBox = new VBox(5);
        Label phoneLabel = new Label("Phone Number");
        txtPhone = new TextField();
        txtPhone.setPromptText("Enter phone number");
        phoneBox.getChildren().addAll(phoneLabel, txtPhone);
        HBox.setHgrow(phoneBox, Priority.ALWAYS);
        row1.getChildren().addAll(nameBox, phoneBox);

        // Email Section
        VBox emailBox = new VBox(5);
        Label emailLabel = new Label("Email Address");
        txtEmail = new TextField();
        txtEmail.setPromptText("Enter email address");
        emailBox.getChildren().addAll(emailLabel, txtEmail);

        // Password Section
        VBox passwordBox = new VBox(5);
        Label passwordLabel = new Label("Password");
        txtPassword = new PasswordField();
        txtPassword.setPromptText("Create password");
        passwordBox.getChildren().addAll(passwordLabel, txtPassword);

        // Address and Role Row
        HBox row4 = new HBox(15);
        VBox addressBox = new VBox(5);
        Label addressLabel = new Label("Address");
        txtAddress = new TextField();
        txtAddress.setPromptText("Enter full address");
        addressBox.getChildren().addAll(addressLabel, txtAddress);
        HBox.setHgrow(addressBox, Priority.ALWAYS);

        VBox roleBox = new VBox(5);
        roleBox.setPrefWidth(150);
        Label roleLabel = new Label("Account Role");
        cmbRole = new ComboBox<>(FXCollections.observableArrayList("STUDENT", "LIBRARIAN"));
        cmbRole.setMaxWidth(Double.MAX_VALUE);
        cmbRole.getSelectionModel().selectFirst();
        roleBox.getChildren().addAll(roleLabel, cmbRole);
        row4.getChildren().addAll(addressBox, roleBox);

        // Submit and Link section
        VBox actionBox = new VBox(10);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(10, 0, 0, 0));

        btnSubmit = new Button("Register Account");
        btnSubmit.setPrefWidth(300);
        btnSubmit.getStyleClass().add("btn-primary");

        HBox loginLinkBox = new HBox(5);
        loginLinkBox.setAlignment(Pos.CENTER);
        Label loginPrompt = new Label("Already have an account?");
        btnLogin = new Button("Login here");
        btnLogin.getStyleClass().add("btn-secondary");
        btnLogin.setStyle("-fx-padding: 2 5;");
        loginLinkBox.getChildren().addAll(loginPrompt, btnLogin);

        lblStatus = new Label();
        lblStatus.getStyleClass().add("status-label-error");

        actionBox.getChildren().addAll(btnSubmit, loginLinkBox, lblStatus);

        registerCard.getChildren().addAll(row1, emailBox, passwordBox, row4, actionBox);
        centerContent.getChildren().addAll(welcomeHeader, registerCard);
        this.setCenter(centerContent);
    }

    // Getters
    public TextField getTxtName() { return txtName; }
    public TextField getTxtPhone() { return txtPhone; }
    public TextField getTxtEmail() { return txtEmail; }
    public PasswordField getTxtPassword() { return txtPassword; }
    public TextField getTxtAddress() { return txtAddress; }
    public ComboBox<String> getCmbRole() { return cmbRole; }
    public Button getBtnSubmit() { return btnSubmit; }
    public Button getBtnLogin() { return btnLogin; }
    public Label getLblStatus() { return lblStatus; }
}
