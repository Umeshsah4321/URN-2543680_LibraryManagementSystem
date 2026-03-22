package application.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import application.models.BookModel;
import application.models.UserModel;
import application.models.NotificationModel;

public class LibrarianDashboardView extends BorderPane {

    // Sidebar buttons
    private Button btnSideHome, btnSideIssue, btnSideBooks, btnSideMembers, btnSideNotif, btnLogout;
    
    // Top Bar labels
    private Label lblPageTitle;

    // View Containers
    private VBox viewHome, viewIssueBook, viewBookManage, viewMemberManage, viewNotifications;

    // --- HOME Section Controls ---
    private Label lblStatTotalBooks, lblStatActiveIssues, lblStatMembers, lblStatUnpaidFines;
    private PieChart chartBookAvailability, chartFineStatus;
    private BarChart<String, Number> chartMemberRoles, chartTopBooks;

    // --- ISSUE Section Controls ---
    private TextField txtIssueStudentEmail;
    private Button btnFindStudent, btnIssueBook;
    private Label lblIssueStudentName, lblIssueStatus;
    private TableView<BookModel> tblIssueBooks;

    // --- BOOK MANAGE Section Controls ---
    private Label lblBookFormTitle, lblBookStatus;
    private Button btnClearBookForm, btnBookSubmit, btnEditBook, btnDeleteBook;
    private TextField txtBookTitle, txtBookAuthor, txtBookPub, txtBookYear, txtBookCopies;
    private TableView<BookModel> tblBooks;

    // --- MEMBER MANAGE Section Controls ---
    private TableView<UserModel> tblMembers;
    private Button btnDeleteMember;
    private Label lblMemberStatus;

    // --- NOTIFICATIONS Section Controls ---
    private ComboBox<String> cmbNotifType;
    private TextField txtNotifMessage;
    private Button btnSendNotif;
    private Label lblNotifStatus;
    private TableView<NotificationModel> tblNotifications;

    public LibrarianDashboardView() {
        this.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
        this.getStyleClass().add("root");

        // ══════════ SIDEBAR ══════════
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(32, 20, 24, 20));
        sidebar.setPrefWidth(260);

        VBox logoBox = new VBox(3);
        logoBox.setPadding(new Insets(0, 0, 22, 4));
        Label logoLabel = new Label("📖 LibraryPro");
        logoLabel.getStyleClass().add("sidebar-logo");
        Label subtitleLabel = new Label("Librarian Portal");
        subtitleLabel.getStyleClass().add("sidebar-subtitle");
        logoBox.getChildren().addAll(logoLabel, subtitleLabel);

        Region divider1 = new Region();
        divider1.getStyleClass().add("sidebar-divider");
        divider1.setPrefHeight(1);
        divider1.setMaxWidth(Double.MAX_VALUE);

        Label menuLabel = new Label("MAIN MENU");
        menuLabel.getStyleClass().add("sidebar-section-label");

        btnSideHome = createSidebarButton("⊞   Dashboard");
        btnSideIssue = createSidebarButton("📋   Issue Book");
        btnSideBooks = createSidebarButton("📚   Manage Books");
        btnSideMembers = createSidebarButton("👥   Manage Members");
        btnSideNotif = createSidebarButton("🔔   Notifications");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Region divider2 = new Region();
        divider2.getStyleClass().add("sidebar-divider");
        divider2.setPrefHeight(1);
        divider2.setMaxWidth(Double.MAX_VALUE);

        btnLogout = new Button("🚪   Logout");
        btnLogout.getStyleClass().add("btn-logout");
        btnLogout.setPrefWidth(220);
        VBox logoutBox = new VBox(btnLogout);
        logoutBox.setPadding(new Insets(10, 0, 0, 0));

        sidebar.getChildren().addAll(logoBox, divider1, menuLabel, btnSideHome, btnSideIssue, btnSideBooks, btnSideMembers, btnSideNotif, spacer, divider2, logoutBox);
        this.setLeft(sidebar);

        // ══════════ MAIN CONTENT AREA ══════════
        VBox mainArea = new VBox();
        VBox.setVgrow(mainArea, Priority.ALWAYS);

        // --- TOP BAR ---
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

        // --- SCROLL CONTENT ---
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox contentArea = new VBox(24);
        contentArea.getStyleClass().add("content-area");
        scrollPane.setContent(contentArea);

        // VIEWS (Sections)
        viewHome = new VBox(22);
        setupHomeSection();

        viewIssueBook = new VBox();
        setupIssueSection();

        viewBookManage = new VBox();
        setupBookManageSection();

        viewMemberManage = new VBox();
        setupMemberManageSection();

        viewNotifications = new VBox();
        setupNotificationsSection();

        contentArea.getChildren().addAll(viewHome, viewIssueBook, viewBookManage, viewMemberManage, viewNotifications);

        mainArea.getChildren().addAll(topBar, scrollPane);
        this.setCenter(mainArea);
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-btn");
        btn.setPrefWidth(220);
        return btn;
    }

    private void setupHomeSection() {
        VBox banner = new VBox(5);
        banner.getStyleClass().add("welcome-banner");
        Label t = new Label("Welcome back, Librarian! 👋");
        t.getStyleClass().add("welcome-banner-title");
        Label s = new Label("Here is your library overview for today.");
        s.getStyleClass().add("welcome-banner-sub");
        banner.getChildren().addAll(t, s);

        HBox statsRow = new HBox(18);
        lblStatTotalBooks = createStatCard(statsRow, "Total Books", "linear-gradient(to bottom right,#2563eb,#60a5fa)");
        lblStatActiveIssues = createStatCard(statsRow, "Active Issues", "linear-gradient(to bottom right,#059669,#34d399)");
        lblStatMembers = createStatCard(statsRow, "Members", "linear-gradient(to bottom right,#d97706,#fbbf24)");
        lblStatUnpaidFines = createStatCard(statsRow, "Unpaid Fines", "linear-gradient(to bottom right,#dc2626,#f87171)");

        HBox chartsRow1 = new HBox(18);
        VBox pieBox1 = createChartCard("📊 Book Availability Status", "Distribution of available vs issued books");
        chartBookAvailability = new PieChart();
        chartBookAvailability.setPrefHeight(280);
        pieBox1.getChildren().add(chartBookAvailability);
        HBox.setHgrow(pieBox1, Priority.ALWAYS);

        VBox barBox1 = createChartCard("📊 Member Distribution", "Number of students vs librarians registered");
        chartMemberRoles = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chartMemberRoles.setPrefHeight(280);
        chartMemberRoles.setLegendVisible(false);
        barBox1.getChildren().add(chartMemberRoles);
        HBox.setHgrow(barBox1, Priority.ALWAYS);
        chartsRow1.getChildren().addAll(pieBox1, barBox1);

        HBox chartsRow2 = new HBox(18);
        VBox barBox2 = createChartCard("📈 Top Borrowed Books", "Books with the most borrow records");
        chartTopBooks = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chartTopBooks.setPrefHeight(260);
        chartTopBooks.setLegendVisible(false);
        barBox2.getChildren().add(chartTopBooks);
        HBox.setHgrow(barBox2, Priority.ALWAYS);

        VBox pieBox2 = createChartCard("💳 Fine Status Overview", "Paid vs unpaid fines across all members");
        chartFineStatus = new PieChart();
        chartFineStatus.setPrefHeight(260);
        pieBox2.getChildren().add(chartFineStatus);
        HBox.setHgrow(pieBox2, Priority.ALWAYS);
        chartsRow2.getChildren().addAll(barBox2, pieBox2);

        viewHome.getChildren().addAll(banner, statsRow, chartsRow1, chartsRow2);
    }

    private Label createStatCard(HBox parent, String labelText, String gradient) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color:" + gradient + ";-fx-padding:28;");
        Label numLabel = new Label("—");
        numLabel.getStyleClass().add("stat-number");
        Label textLabel = new Label(labelText);
        textLabel.getStyleClass().add("stat-label");
        card.getChildren().addAll(numLabel, textLabel);
        HBox.setHgrow(card, Priority.ALWAYS);
        parent.getChildren().add(card);
        return numLabel;
    }

    private VBox createChartCard(String title, String subtitle) {
        VBox box = new VBox(4);
        box.getStyleClass().add("chart-card");
        Label t = new Label(title);
        t.getStyleClass().add("chart-title-label");
        Label s = new Label(subtitle);
        s.getStyleClass().add("card-subheader-label");
        box.getChildren().addAll(t, s);
        return box;
    }

    private void setupIssueSection() {
        viewIssueBook.getStyleClass().add("card");
        viewIssueBook.setManaged(false);
        viewIssueBook.setVisible(false);

        HBox header = createSectionHeader("📋 Issue Book", "Find a student by email, then select a book to issue");
        VBox body = new VBox(18);
        body.setPadding(new Insets(16, 24, 24, 24));

        VBox findSection = new VBox(14);
        findSection.getStyleClass().add("form-section");
        Label sectionTitle = new Label("FIND STUDENT");
        sectionTitle.getStyleClass().add("sidebar-section-label");
        sectionTitle.setStyle("-fx-padding:0;");
        
        HBox findRow = new HBox(14);
        findRow.setAlignment(Pos.BOTTOM_LEFT);
        VBox emailBox = new VBox(4);
        emailBox.setPrefWidth(340);
        Label emailLabel = new Label("Student Email");
        emailLabel.getStyleClass().add("form-label");
        txtIssueStudentEmail = new TextField();
        txtIssueStudentEmail.setPromptText("student@email.com");
        emailBox.getChildren().addAll(emailLabel, txtIssueStudentEmail);

        btnFindStudent = new Button("🔍 Find");
        btnFindStudent.getStyleClass().add("btn-secondary");
        btnFindStudent.setStyle("-fx-padding:10 22;");

        VBox foundBox = new VBox(4);
        HBox.setHgrow(foundBox, Priority.ALWAYS);
        Label foundLabel = new Label("Student Found");
        foundLabel.getStyleClass().add("form-label");
        lblIssueStudentName = new Label("—");
        lblIssueStudentName.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#059669;");
        foundBox.getChildren().addAll(foundLabel, lblIssueStudentName);
        findRow.getChildren().addAll(emailBox, btnFindStudent, foundBox);
        findSection.getChildren().addAll(sectionTitle, findRow);

        tblIssueBooks = new TableView<>();
        tblIssueBooks.setPrefHeight(380);

        HBox footer = new HBox(14);
        footer.setAlignment(Pos.CENTER_RIGHT);
        lblIssueStatus = new Label();
        lblIssueStatus.getStyleClass().add("status-label-success");
        btnIssueBook = new Button("📋 Issue Selected Book");
        btnIssueBook.getStyleClass().add("btn-success");
        footer.getChildren().addAll(lblIssueStatus, btnIssueBook);

        body.getChildren().addAll(findSection, tblIssueBooks, footer);
        viewIssueBook.getChildren().addAll(header, body);
    }

    private void setupBookManageSection() {
        viewBookManage.getStyleClass().add("card");
        viewBookManage.setManaged(false);
        viewBookManage.setVisible(false);

        HBox header = createSectionHeader("📚 Book Management", "Add, edit, or remove books from the library catalog");
        VBox body = new VBox(18);
        body.setPadding(new Insets(16, 24, 24, 24));

        VBox formSection = new VBox(14);
        formSection.getStyleClass().add("form-section");
        HBox formHeader = new HBox();
        formHeader.setAlignment(Pos.CENTER_LEFT);
        lblBookFormTitle = new Label("ADD NEW BOOK");
        lblBookFormTitle.getStyleClass().add("sidebar-section-label");
        lblBookFormTitle.setStyle("-fx-padding:0;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        btnClearBookForm = new Button("✕ Clear");
        btnClearBookForm.getStyleClass().add("btn-secondary");
        btnClearBookForm.setStyle("-fx-font-size:11px;-fx-padding:5 14;");
        formHeader.getChildren().addAll(lblBookFormTitle, sp, btnClearBookForm);

        HBox inputsRow = new HBox(12);
        inputsRow.setAlignment(Pos.BOTTOM_LEFT);
        txtBookTitle = createInputBox(inputsRow, "Title *", "Book title", -1);
        txtBookAuthor = createInputBox(inputsRow, "Author *", "Author name", -1);
        txtBookPub = createInputBox(inputsRow, "Publisher", "Publisher", 160);
        txtBookYear = createInputBox(inputsRow, "Year *", "YYYY", 90);
        txtBookCopies = createInputBox(inputsRow, "Copies *", "0", 90);
        btnBookSubmit = new Button("➕ Add Book");
        btnBookSubmit.getStyleClass().add("btn-success");
        btnBookSubmit.setStyle("-fx-padding:10 22;");
        inputsRow.getChildren().add(btnBookSubmit);
        
        lblBookStatus = new Label();
        lblBookStatus.getStyleClass().add("status-label-success");
        formSection.getChildren().addAll(formHeader, inputsRow, lblBookStatus);

        tblBooks = new TableView<>();
        tblBooks.setPrefHeight(400);

        HBox actionsRow = new HBox(12);
        actionsRow.setAlignment(Pos.CENTER_RIGHT);
        btnEditBook = new Button("✏️ Edit Selected");
        btnEditBook.getStyleClass().add("btn-secondary");
        btnDeleteBook = new Button("🗑 Delete Selected");
        btnDeleteBook.getStyleClass().add("btn-danger");
        actionsRow.getChildren().addAll(btnEditBook, btnDeleteBook);

        body.getChildren().addAll(formSection, tblBooks, actionsRow);
        viewBookManage.getChildren().addAll(header, body);
    }

    private TextField createInputBox(HBox parent, String label, String prompt, double width) {
        VBox v = new VBox(4);
        if (width > 0) v.setPrefWidth(width);
        else HBox.setHgrow(v, Priority.ALWAYS);
        Label l = new Label(label);
        l.getStyleClass().add("form-label");
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        v.getChildren().addAll(l, tf);
        parent.getChildren().add(v);
        return tf;
    }

    private void setupMemberManageSection() {
        viewMemberManage.getStyleClass().add("card");
        viewMemberManage.setManaged(false);
        viewMemberManage.setVisible(false);

        HBox header = createSectionHeader("👥 Member Management", "View all registered members and manage accounts");
        VBox body = new VBox(16);
        body.setPadding(new Insets(16, 24, 24, 24));
        tblMembers = new TableView<>();
        tblMembers.setPrefHeight(520);
        
        HBox footer = new HBox(12);
        footer.setAlignment(Pos.CENTER_RIGHT);
        lblMemberStatus = new Label();
        lblMemberStatus.getStyleClass().add("status-label-error");
        btnDeleteMember = new Button("🗑 Delete Selected");
        btnDeleteMember.getStyleClass().add("btn-danger");
        footer.getChildren().addAll(lblMemberStatus, btnDeleteMember);

        body.getChildren().addAll(tblMembers, footer);
        viewMemberManage.getChildren().addAll(header, body);
    }

    private void setupNotificationsSection() {
        viewNotifications.getStyleClass().add("card");
        viewNotifications.setManaged(false);
        viewNotifications.setVisible(false);

        HBox header = createSectionHeader("🔔 Notifications", "Compose and broadcast announcements to all members");
        VBox body = new VBox(18);
        body.setPadding(new Insets(16, 24, 24, 24));

        VBox composeSection = new VBox(14);
        composeSection.getStyleClass().add("form-section");
        Label compLabel = new Label("COMPOSE");
        compLabel.getStyleClass().add("sidebar-section-label");
        compLabel.setStyle("-fx-padding:0;");

        HBox compRow = new HBox(14);
        compRow.setAlignment(Pos.BOTTOM_LEFT);
        VBox typeBox = new VBox(4);
        typeBox.setPrefWidth(220);
        Label tl = new Label("Type");
        tl.getStyleClass().add("form-label");
        cmbNotifType = new ComboBox<>();
        cmbNotifType.setPrefWidth(220);
        typeBox.getChildren().addAll(tl, cmbNotifType);

        VBox msgBox = new VBox(4);
        HBox.setHgrow(msgBox, Priority.ALWAYS);
        Label ml = new Label("Message");
        ml.getStyleClass().add("form-label");
        txtNotifMessage = new TextField();
        txtNotifMessage.setPromptText("Enter notification message...");
        msgBox.getChildren().addAll(ml, txtNotifMessage);

        btnSendNotif = new Button("🔔 Send");
        btnSendNotif.getStyleClass().add("btn-primary");
        btnSendNotif.setStyle("-fx-padding:10 28;");
        compRow.getChildren().addAll(typeBox, msgBox, btnSendNotif);

        lblNotifStatus = new Label();
        lblNotifStatus.getStyleClass().add("status-label-success");
        composeSection.getChildren().addAll(compLabel, compRow, lblNotifStatus);

        tblNotifications = new TableView<>();
        tblNotifications.setPrefHeight(440);

        body.getChildren().addAll(composeSection, tblNotifications);
        viewNotifications.getChildren().addAll(header, body);
    }

    private HBox createSectionHeader(String titleText, String subText) {
        HBox header = new HBox();
        header.setStyle("-fx-padding:20 24 16 24;-fx-border-color:transparent transparent #e2e8f0 transparent;-fx-border-width:1;");
        VBox v = new VBox(2);
        Label t = new Label(titleText);
        t.getStyleClass().add("card-header-label");
        Label s = new Label(subText);
        s.getStyleClass().add("card-subheader-label");
        v.getChildren().addAll(t, s);
        header.getChildren().add(v);
        return header;
    }

    // --- GETTERS ---
    public Button getBtnSideHome() { return btnSideHome; }
    public Button getBtnSideIssue() { return btnSideIssue; }
    public Button getBtnSideBooks() { return btnSideBooks; }
    public Button getBtnSideMembers() { return btnSideMembers; }
    public Button getBtnSideNotif() { return btnSideNotif; }
    public Button getBtnLogout() { return btnLogout; }
    public Label getLblPageTitle() { return lblPageTitle; }
    public VBox getViewHome() { return viewHome; }
    public VBox getViewIssueBook() { return viewIssueBook; }
    public VBox getViewBookManage() { return viewBookManage; }
    public VBox getViewMemberManage() { return viewMemberManage; }
    public VBox getViewNotifications() { return viewNotifications; }
    public Label getLblStatTotalBooks() { return lblStatTotalBooks; }
    public Label getLblStatActiveIssues() { return lblStatActiveIssues; }
    public Label getLblStatMembers() { return lblStatMembers; }
    public Label getLblStatUnpaidFines() { return lblStatUnpaidFines; }
    public PieChart getChartBookAvailability() { return chartBookAvailability; }
    public PieChart getChartFineStatus() { return chartFineStatus; }
    public BarChart<String, Number> getChartMemberRoles() { return chartMemberRoles; }
    public BarChart<String, Number> getChartTopBooks() { return chartTopBooks; }
    public TextField getTxtIssueStudentEmail() { return txtIssueStudentEmail; }
    public Button getBtnFindStudent() { return btnFindStudent; }
    public Button getBtnIssueBook() { return btnIssueBook; }
    public Label getLblIssueStudentName() { return lblIssueStudentName; }
    public Label getLblIssueStatus() { return lblIssueStatus; }
    public TableView<BookModel> getTblIssueBooks() { return tblIssueBooks; }
    public Label getLblBookFormTitle() { return lblBookFormTitle; }
    public Label getLblBookStatus() { return lblBookStatus; }
    public Button getBtnClearBookForm() { return btnClearBookForm; }
    public Button getBtnBookSubmit() { return btnBookSubmit; }
    public Button getBtnEditBook() { return btnEditBook; }
    public Button getBtnDeleteBook() { return btnDeleteBook; }
    public TextField getTxtBookTitle() { return txtBookTitle; }
    public TextField getTxtBookAuthor() { return txtBookAuthor; }
    public TextField getTxtBookPub() { return txtBookPub; }
    public TextField getTxtBookYear() { return txtBookYear; }
    public TextField getTxtBookCopies() { return txtBookCopies; }
    public TableView<BookModel> getTblBooks() { return tblBooks; }
    public TableView<UserModel> getTblMembers() { return tblMembers; }
    public Button getBtnDeleteMember() { return btnDeleteMember; }
    public Label getLblMemberStatus() { return lblMemberStatus; }
    public ComboBox<String> getCmbNotifType() { return cmbNotifType; }
    public TextField getTxtNotifMessage() { return txtNotifMessage; }
    public Button getBtnSendNotif() { return btnSendNotif; }
    public Label getLblNotifStatus() { return lblNotifStatus; }
    public TableView<NotificationModel> getTblNotifications() { return tblNotifications; }
}
