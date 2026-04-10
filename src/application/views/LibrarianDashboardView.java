
package application.views;

import application.controllers.LibrarianDashboardController;
import application.models.BookModel;
import application.models.NotificationModel;
import application.models.UserModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class LibrarianDashboardView extends BorderPane {

    private LibrarianDashboardController controller;

    // FXML-equivalent fields that need to be wired to controller
    private Label lblPageTitle, lblStatTotalBooks, lblStatActiveIssues, lblStatMembers, lblStatUnpaidFines;
    private Button btnSideHome, btnSideIssue, btnSideBooks, btnSideMembers, btnSideNotif;
    private VBox viewHome, viewIssueBook, viewBookManage, viewMemberManage, viewNotifications;
    private PieChart chartBookAvailability, chartFineStatus;
    private BarChart<String, Number> chartMemberRoles, chartTopBooks;

    // Issue Book fields
    private TextField txtIssueStudentEmail;
    private Label lblIssueStudentName, lblIssueStatus;
    private TableView<BookModel> tblIssueBooks;

    // Book Management fields
    private Label lblBookFormTitle, lblBookStatus;
    private TextField txtBookTitle, txtBookAuthor, txtBookPub, txtBookYear, txtBookCopies;
    private Button btnBookSubmit;
    private TableView<BookModel> tblBooks;

    // Member Management fields
    private TableView<UserModel> tblMembers;
    private Label lblMemberStatus;

    // Notifications fields
    private ComboBox<String> cmbNotifType;
    private TextField txtNotifMessage;
    private Label lblNotifStatus;
    private TableView<NotificationModel> tblNotifications;

    public LibrarianDashboardView() {
        this.controller = new LibrarianDashboardController();
        initUI();
    }

    private void initUI() {
        getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
        getStyleClass().add("root");

        // --- Sidebar ---
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(32, 20, 24, 20));

        VBox logoBox = new VBox(3);
        logoBox.setStyle("-fx-padding: 0 0 22 4;");
        Label logoLabel = new Label("📖 LibraryPro");
        logoLabel.getStyleClass().add("sidebar-logo");
        Label subLogoLabel = new Label("Librarian Portal");
        subLogoLabel.getStyleClass().add("sidebar-subtitle");
        logoBox.getChildren().addAll(logoLabel, subLogoLabel);

        Region divider1 = new Region();
        divider1.getStyleClass().add("sidebar-divider");
        divider1.setPrefHeight(1);
        divider1.setMaxWidth(Double.MAX_VALUE);

        Label mainMenuLabel = new Label("MAIN MENU");
        mainMenuLabel.getStyleClass().add("sidebar-section-label");

        btnSideHome = createSidebarBtn("⊞   Dashboard");
        btnSideIssue = createSidebarBtn("📋   Issue Book");
        btnSideBooks = createSidebarBtn("📚   Manage Books");
        btnSideMembers = createSidebarBtn("👥   Manage Members");
        btnSideNotif = createSidebarBtn("🔔   Notifications");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Region divider2 = new Region();
        divider2.getStyleClass().add("sidebar-divider");
        divider2.setPrefHeight(1);
        divider2.setMaxWidth(Double.MAX_VALUE);

        VBox logoutBox = new VBox();
        logoutBox.setStyle("-fx-padding: 10 0 0 0;");
        Button btnLogout = new Button("🚪   Logout");
        btnLogout.setPrefWidth(220);
        btnLogout.getStyleClass().add("btn-logout");
        logoutBox.getChildren().add(btnLogout);

        sidebar.getChildren().addAll(logoBox, divider1, mainMenuLabel, btnSideHome, btnSideIssue, btnSideBooks, btnSideMembers, btnSideNotif, spacer, divider2, logoutBox);
        setLeft(sidebar);

        // --- Main Content Area ---
        VBox mainPane = new VBox();
        VBox.setVgrow(mainPane, Priority.ALWAYS);

        // Top Bar
        HBox topBar = new HBox(12);
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        lblPageTitle = new Label("Dashboard");
        lblPageTitle.getStyleClass().add("page-title-label");
        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        Label userBadge = new Label("👤 Librarian");
        userBadge.getStyleClass().add("user-badge");
        topBar.getChildren().addAll(lblPageTitle, topSpacer, userBadge);

        // Scrollable Content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox contentArea = new VBox(24);
        contentArea.getStyleClass().add("content-area");
        scrollPane.setContent(contentArea);

        mainPane.getChildren().addAll(topBar, scrollPane);
        setCenter(mainPane);

        // --- Content Views ---
        initHomeView();
        initIssueBookView();
        initBookManageView();
        initMemberManageView();
        initNotificationsView();

        contentArea.getChildren().addAll(viewHome, viewIssueBook, viewBookManage, viewMemberManage, viewNotifications);

        // --- Wire Controller ---
        wireController();

        // Event Handlers
        btnSideHome.setOnAction(e -> controller.showHome());
        btnSideIssue.setOnAction(e -> controller.showIssueBook());
        btnSideBooks.setOnAction(e -> controller.showBookManage());
        btnSideMembers.setOnAction(e -> controller.showMemberManage());
        btnSideNotif.setOnAction(e -> controller.showNotifications());
        btnLogout.setOnAction(controller::logout);

        controller.initialize();
    }

    private Button createSidebarBtn(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(220);
        btn.getStyleClass().add("sidebar-btn");
        return btn;
    }

    private void initHomeView() {
        viewHome = new VBox(22);

        // Welcome Banner
        VBox banner = new VBox(5);
        banner.getStyleClass().add("welcome-banner");
        Label bTitle = new Label("Welcome back, Librarian! 👋");
        bTitle.getStyleClass().add("welcome-banner-title");
        Label bSub = new Label("Here is your library overview for today.");
        bSub.getStyleClass().add("welcome-banner-sub");
        banner.getChildren().addAll(bTitle, bSub);

        // Stat Cards
        HBox statRow = new HBox(18);
        lblStatTotalBooks = createStatCard(statRow, "Total Books", "stat-card-blue");
        lblStatActiveIssues = createStatCard(statRow, "Active Issues", "stat-card-green");
        lblStatMembers = createStatCard(statRow, "Members", "stat-card-orange");
        lblStatUnpaidFines = createStatCard(statRow, "Unpaid Fines", "stat-card-red");

        // Chart Rows
        HBox chartRow1 = new HBox(18);
        chartBookAvailability = new PieChart();
        setupChartCard(chartRow1, "📊 Book Availability Status", "Distribution of available vs issued books", chartBookAvailability);

        chartMemberRoles = new BarChart<>(new CategoryAxis(), new NumberAxis());
        setupChartCard(chartRow1, "📊 Member Distribution", "Number of students vs librarians registered", chartMemberRoles);

        HBox chartRow2 = new HBox(18);
        chartTopBooks = new BarChart<>(new CategoryAxis(), new NumberAxis());
        setupChartCard(chartRow2, "📈 Top Borrowed Books", "Books with the most borrow records", chartTopBooks);

        chartFineStatus = new PieChart();
        setupChartCard(chartRow2, "💳 Fine Status Overview", "Paid vs unpaid fines across all members", chartFineStatus);

        viewHome.getChildren().addAll(banner, statRow, chartRow1, chartRow2);
    }

    private Label createStatCard(HBox parent, String label, String styleClass) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("stat-card", styleClass);
        card.setAlignment(Pos.CENTER);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label num = new Label("—");
        num.getStyleClass().add("stat-number");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-label");

        card.getChildren().addAll(num, lbl);
        parent.getChildren().add(card);
        return num;
    }

    private void setupChartCard(HBox parent, String title, String sub, Region chart) {
        VBox card = new VBox(4);
        card.getStyleClass().add("chart-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        Label lblT = new Label(title);
        lblT.getStyleClass().add("chart-title-label");
        Label lblS = new Label(sub);
        lblS.getStyleClass().add("card-subheader-label");

        chart.setPrefHeight(280);
        chart.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(lblT, lblS, chart);
        parent.getChildren().add(card);
    }

    private void initIssueBookView() {
        viewIssueBook = new VBox(0);
        viewIssueBook.getStyleClass().add("card");
        viewIssueBook.setManaged(false);
        viewIssueBook.setVisible(false);

        HBox header = new HBox();
        header.setStyle("-fx-padding:20 24 16 24;-fx-border-color:transparent transparent #e2e8f0 transparent;-fx-border-width:1;");
        VBox hText = new VBox(2);
        hText.getChildren().addAll(new Label("📋 Issue Book") {{ getStyleClass().add("card-header-label"); }},
                                   new Label("Find a student by email, then select a book to issue") {{ getStyleClass().add("card-subheader-label"); }});
        header.getChildren().add(hText);

        VBox body = new VBox(18);
        body.setStyle("-fx-padding:16 24 24 24;");

        VBox findSection = new VBox(14);
        findSection.getStyleClass().add("form-section");
        findSection.getChildren().add(new Label("FIND STUDENT") {{ getStyleClass().add("sidebar-section-label"); setStyle("-fx-padding:0;"); }});

        HBox findRow = new HBox(14);
        findRow.setAlignment(Pos.BOTTOM_LEFT);
        VBox emailBox = new VBox(4);
        emailBox.setPrefWidth(340);
        emailBox.getChildren().add(new Label("Student Email") {{ getStyleClass().add("form-label"); }});
        txtIssueStudentEmail = new TextField();
        txtIssueStudentEmail.setPromptText("student@email.com");
        emailBox.getChildren().add(txtIssueStudentEmail);

        Button btnFind = new Button("🔍 Find");
        btnFind.getStyleClass().add("btn-secondary");
        btnFind.setStyle("-fx-padding:10 22;");
        btnFind.setOnAction(e -> controller.findStudentForIssue());

        VBox foundBox = new VBox(4);
        HBox.setHgrow(foundBox, Priority.ALWAYS);
        foundBox.getChildren().add(new Label("Student Found") {{ getStyleClass().add("form-label"); }});
        lblIssueStudentName = new Label("—");
        lblIssueStudentName.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#059669;");
        foundBox.getChildren().add(lblIssueStudentName);

        findRow.getChildren().addAll(emailBox, btnFind, foundBox);
        findSection.getChildren().add(findRow);

        tblIssueBooks = new TableView<>();
        tblIssueBooks.setPrefHeight(380);
        TableColumn<BookModel, Integer> colId = new TableColumn<>("ID");
        colId.setPrefWidth(70);
        colId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        TableColumn<BookModel, String> colTitle = new TableColumn<>("Title");
        colTitle.setPrefWidth(280);
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<BookModel, String> colAuthor = new TableColumn<>("Author");
        colAuthor.setPrefWidth(200);
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<BookModel, String> colPub = new TableColumn<>("Publisher");
        colPub.setPrefWidth(170);
        colPub.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        TableColumn<BookModel, Integer> colAvail = new TableColumn<>("Available");
        colAvail.setPrefWidth(100);
        colAvail.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        tblIssueBooks.getColumns().addAll(colId, colTitle, colAuthor, colPub, colAvail);

        HBox footer = new HBox(14);
        footer.setAlignment(Pos.CENTER_RIGHT);
        lblIssueStatus = new Label();
        lblIssueStatus.getStyleClass().add("status-label-success");
        Button btnIssue = new Button("📋 Issue Selected Book");
        btnIssue.getStyleClass().add("btn-success");
        btnIssue.setOnAction(e -> controller.issueBook());
        footer.getChildren().addAll(lblIssueStatus, btnIssue);

        body.getChildren().addAll(findSection, tblIssueBooks, footer);
        viewIssueBook.getChildren().addAll(header, body);
    }

    private void initBookManageView() {
        viewBookManage = new VBox(0);
        viewBookManage.getStyleClass().add("card");
        viewBookManage.setManaged(false);
        viewBookManage.setVisible(false);

        HBox header = new HBox();
        header.setStyle("-fx-padding:20 24 16 24;-fx-border-color:transparent transparent #e2e8f0 transparent;-fx-border-width:1;");
        VBox hText = new VBox(2);
        hText.getChildren().addAll(new Label("📚 Book Management") {{ getStyleClass().add("card-header-label"); }},
                                   new Label("Add, edit, or remove books from the library catalog") {{ getStyleClass().add("card-subheader-label"); }});
        header.getChildren().add(hText);

        VBox body = new VBox(18);
        body.setStyle("-fx-padding:16 24 24 24;");

        VBox formSection = new VBox(14);
        formSection.getStyleClass().add("form-section");
        HBox formHeader = new HBox();
        formHeader.setAlignment(Pos.CENTER_LEFT);
        lblBookFormTitle = new Label("ADD NEW BOOK");
        lblBookFormTitle.getStyleClass().add("sidebar-section-label");
        lblBookFormTitle.setStyle("-fx-padding:0;");
        Region fSpacer = new Region();
        HBox.setHgrow(fSpacer, Priority.ALWAYS);
        Button btnClear = new Button("✕ Clear");
        btnClear.getStyleClass().add("btn-secondary");
        btnClear.setStyle("-fx-font-size:11px;-fx-padding:5 14;");
        btnClear.setOnAction(e -> controller.clearBookForm());
        formHeader.getChildren().addAll(lblBookFormTitle, fSpacer, btnClear);

        HBox formFields = new HBox(12);
        formFields.setAlignment(Pos.BOTTOM_LEFT);

        txtBookTitle = createFormField(formFields, "Title *", "Book title", -1, true);
        txtBookAuthor = createFormField(formFields, "Author *", "Author name", -1, true);
        txtBookPub = createFormField(formFields, "Publisher", "Publisher", 160, false);
        txtBookYear = createFormField(formFields, "Year *", "YYYY", 90, false);
        txtBookCopies = createFormField(formFields, "Copies *", "0", 90, false);

        btnBookSubmit = new Button("➕ Add Book");
        btnBookSubmit.getStyleClass().add("btn-success");
        btnBookSubmit.setStyle("-fx-padding:10 22;");
        btnBookSubmit.setOnAction(e -> controller.addOrUpdateBook());
        formFields.getChildren().add(btnBookSubmit);

        lblBookStatus = new Label();
        lblBookStatus.getStyleClass().add("status-label-success");
        formSection.getChildren().addAll(formHeader, formFields, lblBookStatus);

        tblBooks = new TableView<>();
        tblBooks.setPrefHeight(400);
        // ... (Similar columns to Issue, but with more detail)
        TableColumn<BookModel, Integer> colId = new TableColumn<>("ID");
        colId.setPrefWidth(65); colId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        TableColumn<BookModel, String> colTitle = new TableColumn<>("Title");
        colTitle.setPrefWidth(240); colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<BookModel, String> colAuthor = new TableColumn<>("Author");
        colAuthor.setPrefWidth(180); colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<BookModel, String> colPub = new TableColumn<>("Publisher");
        colPub.setPrefWidth(160); colPub.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        TableColumn<BookModel, Integer> colYear = new TableColumn<>("Year");
        colYear.setPrefWidth(75); colYear.setCellValueFactory(new PropertyValueFactory<>("publicationYear"));
        TableColumn<BookModel, Integer> colTotal = new TableColumn<>("Total");
        colTotal.setPrefWidth(75); colTotal.setCellValueFactory(new PropertyValueFactory<>("totalCopies"));
        TableColumn<BookModel, Integer> colAvail = new TableColumn<>("Available");
        colAvail.setPrefWidth(90); colAvail.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        tblBooks.getColumns().addAll(colId, colTitle, colAuthor, colPub, colYear, colTotal, colAvail);

        HBox footer = new HBox(12);
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button btnEdit = new Button("✏️ Edit Selected");
        btnEdit.getStyleClass().add("btn-secondary");
        btnEdit.setOnAction(e -> controller.editSelectedBook());
        Button btnDel = new Button("🗑 Delete Selected");
        btnDel.getStyleClass().add("btn-danger");
        btnDel.setOnAction(e -> controller.deleteBook());
        footer.getChildren().addAll(btnEdit, btnDel);

        body.getChildren().addAll(formSection, tblBooks, footer);
        viewBookManage.getChildren().addAll(header, body);
    }

    private TextField createFormField(HBox parent, String label, String prompt, double width, boolean grow) {
        VBox box = new VBox(4);
        if (grow) {
			HBox.setHgrow(box, Priority.ALWAYS);
		}
        if (width > 0) {
			box.setPrefWidth(width);
		}
        box.getChildren().add(new Label(label) {{ getStyleClass().add("form-label"); }});
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        box.getChildren().add(tf);
        parent.getChildren().add(box);
        return tf;
    }

    private void initMemberManageView() {
        viewMemberManage = new VBox(0);
        viewMemberManage.getStyleClass().add("card");
        viewMemberManage.setManaged(false);
        viewMemberManage.setVisible(false);

        HBox header = new HBox();
        header.setStyle("-fx-padding:20 24 16 24;-fx-border-color:transparent transparent #e2e8f0 transparent;-fx-border-width:1;");
        VBox hText = new VBox(2);
        hText.getChildren().addAll(new Label("👥 Member Management") {{ getStyleClass().add("card-header-label"); }},
                                   new Label("View all registered members and manage accounts") {{ getStyleClass().add("card-subheader-label"); }});
        header.getChildren().add(hText);

        VBox body = new VBox(16);
        body.setStyle("-fx-padding:16 24 24 24;");

        tblMembers = new TableView<>();
        tblMembers.setPrefHeight(520);
        TableColumn<UserModel, Integer> colId = new TableColumn<>("User ID");
        colId.setPrefWidth(90); colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        TableColumn<UserModel, String> colName = new TableColumn<>("Name");
        colName.setPrefWidth(220); colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<UserModel, String> colEmail = new TableColumn<>("Email");
        colEmail.setPrefWidth(280); colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<UserModel, String> colPhone = new TableColumn<>("Phone");
        colPhone.setPrefWidth(160); colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<UserModel, String> colRole = new TableColumn<>("Role");
        colRole.setPrefWidth(130); colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        tblMembers.getColumns().addAll(colId, colName, colEmail, colPhone, colRole);

        HBox footer = new HBox(12);
        footer.setAlignment(Pos.CENTER_RIGHT);
        lblMemberStatus = new Label();
        lblMemberStatus.getStyleClass().add("status-label-error");
        Button btnDel = new Button("🗑 Delete Selected");
        btnDel.getStyleClass().add("btn-danger");
        btnDel.setOnAction(e -> controller.deleteMember());
        footer.getChildren().addAll(lblMemberStatus, btnDel);

        body.getChildren().addAll(tblMembers, footer);
        viewMemberManage.getChildren().addAll(header, body);
    }

    private void initNotificationsView() {
        viewNotifications = new VBox(0);
        viewNotifications.getStyleClass().add("card");
        viewNotifications.setManaged(false);
        viewNotifications.setVisible(false);

        HBox header = new HBox();
        header.setStyle("-fx-padding:20 24 16 24;-fx-border-color:transparent transparent #e2e8f0 transparent;-fx-border-width:1;");
        VBox hText = new VBox(2);
        hText.getChildren().addAll(new Label("🔔 Notifications") {{ getStyleClass().add("card-header-label"); }},
                                   new Label("Compose and broadcast announcements to all members") {{ getStyleClass().add("card-subheader-label"); }});
        header.getChildren().add(hText);

        VBox body = new VBox(18);
        body.setStyle("-fx-padding:16 24 24 24;");

        VBox formSection = new VBox(14);
        formSection.getStyleClass().add("form-section");
        formSection.getChildren().add(new Label("COMPOSE") {{ getStyleClass().add("sidebar-section-label"); setStyle("-fx-padding:0;"); }});

        HBox formRow = new HBox(14);
        formRow.setAlignment(Pos.BOTTOM_LEFT);
        VBox typeBox = new VBox(4);
        typeBox.setPrefWidth(220);
        typeBox.getChildren().add(new Label("Type") {{ getStyleClass().add("form-label"); }});
        cmbNotifType = new ComboBox<>();
        cmbNotifType.setPrefWidth(220);
        typeBox.getChildren().add(cmbNotifType);

        VBox msgBox = new VBox(4);
        HBox.setHgrow(msgBox, Priority.ALWAYS);
        msgBox.getChildren().add(new Label("Message") {{ getStyleClass().add("form-label"); }});
        txtNotifMessage = new TextField();
        txtNotifMessage.setPromptText("Enter notification message...");
        msgBox.getChildren().add(txtNotifMessage);

        Button btnSend = new Button("🔔 Send");
        btnSend.getStyleClass().add("btn-primary");
        btnSend.setStyle("-fx-padding:10 28;");
        btnSend.setOnAction(e -> controller.sendNotification());

        formRow.getChildren().addAll(typeBox, msgBox, btnSend);
        lblNotifStatus = new Label();
        lblNotifStatus.getStyleClass().add("status-label-success");
        formSection.getChildren().addAll(formRow, lblNotifStatus);

        tblNotifications = new TableView<>();
        tblNotifications.setPrefHeight(440);
        TableColumn<NotificationModel, String> colDate = new TableColumn<>("Date / Time");
        colDate.setPrefWidth(200); colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        TableColumn<NotificationModel, String> colType = new TableColumn<>("Type");
        colType.setPrefWidth(160); colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<NotificationModel, String> colMsg = new TableColumn<>("Message");
        colMsg.setPrefWidth(900); colMsg.setCellValueFactory(new PropertyValueFactory<>("message"));
        tblNotifications.getColumns().addAll(colDate, colType, colMsg);

        body.getChildren().addAll(formSection, tblNotifications);
        viewNotifications.getChildren().addAll(header, body);
    }

    private void wireController() {
        controller.setFields(lblPageTitle, btnSideHome, btnSideIssue, btnSideBooks, btnSideMembers, btnSideNotif,
                viewHome, viewIssueBook, viewBookManage, viewMemberManage, viewNotifications,
                lblStatTotalBooks, lblStatActiveIssues, lblStatMembers, lblStatUnpaidFines,
                chartBookAvailability, chartMemberRoles, chartTopBooks, chartFineStatus,
                txtIssueStudentEmail, lblIssueStudentName, tblIssueBooks, lblIssueStatus,
                lblBookFormTitle, btnBookSubmit, txtBookTitle, txtBookAuthor, txtBookPub, txtBookYear, txtBookCopies, lblBookStatus, tblBooks,
                tblMembers, lblMemberStatus,
                cmbNotifType, txtNotifMessage, lblNotifStatus, tblNotifications);
    }
}
