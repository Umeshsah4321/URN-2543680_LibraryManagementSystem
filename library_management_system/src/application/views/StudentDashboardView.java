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
import application.models.IssueModel;
import application.models.FineModel;
import application.models.NotificationModel;

public class StudentDashboardView extends BorderPane {

    // Sidebar buttons
    private Button btnSideHome, btnSideSearch, btnSideBorrowed, btnSideFines, btnSideAccount, btnLogout;
    
    // Top Bar labels
    private Label lblPageTitle, lblTopUsername;

    // View Containers (the actual content sections)
    private VBox viewHome, viewSearchBooks, viewBorrowedBooks, viewFines, viewAccountStatus;

    // --- HOME Section Controls ---
    private Label lblWelcome, lblStatTotalBooks, lblStatBorrowed, lblStatPending, lblStatFines;
    private PieChart chartMyStatus;
    private BarChart<String, Number> chartLibraryOverview;
    private TableView<NotificationModel> tblNotifications;

    // --- SEARCH Section Controls ---
    private TextField txtSearch;
    private Button btnSearch, btnBorrow;
    private TableView<BookModel> tblBooks;
    private Label lblSearchStatus;

    // --- BORROWED Section Controls ---
    private TableView<IssueModel> tblIssues;
    private Button btnReturn;
    private Label lblReturnStatus;

    // --- FINES Section Controls ---
    private TableView<FineModel> tblFines;
    private Button btnPayFine;
    private Label lblFineStatus;

    // --- ACCOUNT Section Controls ---
    private Label lblAccName, lblAccEmail, lblAccPhone, lblAccRole;
    private TableView<IssueModel> tblAccIssues;
    private TableView<FineModel> tblAccFines;

    public StudentDashboardView() {
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
        Label subtitleLabel = new Label("Student Portal");
        subtitleLabel.getStyleClass().add("sidebar-subtitle");
        logoBox.getChildren().addAll(logoLabel, subtitleLabel);

        Region divider1 = new Region();
        divider1.getStyleClass().add("sidebar-divider");
        divider1.setPrefHeight(1);
        divider1.setMaxWidth(Double.MAX_VALUE);

        Label menuLabel = new Label("MAIN MENU");
        menuLabel.getStyleClass().add("sidebar-section-label");

        btnSideHome = createSidebarButton("⊞   Home");
        btnSideSearch = createSidebarButton("🔍   Search Books");
        btnSideBorrowed = createSidebarButton("📚   Borrowed Books");
        btnSideFines = createSidebarButton("💳   Pay Fine");
        btnSideAccount = createSidebarButton("👤   Account Status");

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

        sidebar.getChildren().addAll(logoBox, divider1, menuLabel, btnSideHome, btnSideSearch, btnSideBorrowed, btnSideFines, btnSideAccount, spacer, divider2, logoutBox);
        this.setLeft(sidebar);

        // ══════════ MAIN CONTENT AREA ══════════
        VBox mainArea = new VBox();
        VBox.setVgrow(mainArea, Priority.ALWAYS);

        // --- TOP BAR ---
        HBox topBar = new HBox(12);
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        lblPageTitle = new Label("Home");
        lblPageTitle.getStyleClass().add("page-title-label");
        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        lblTopUsername = new Label("📘 Student");
        lblTopUsername.getStyleClass().add("user-badge");
        topBar.getChildren().addAll(lblPageTitle, topSpacer, lblTopUsername);

        // --- SCROLL CONTENT ---
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox contentArea = new VBox(24);
        contentArea.getStyleClass().add("content-area");
        scrollPane.setContent(contentArea);

        // ─── HOME VIEW ───
        viewHome = new VBox(22);
        setupHomeSection();

        // ─── SEARCH VIEW ───
        viewSearchBooks = new VBox();
        setupSearchSection();

        // ─── BORROWED VIEW ───
        viewBorrowedBooks = new VBox();
        setupBorrowedSection();

        // ─── FINES VIEW ───
        viewFines = new VBox();
        setupFinesSection();

        // ─── ACCOUNT STATUS VIEW ───
        viewAccountStatus = new VBox();
        setupAccountSection();

        contentArea.getChildren().addAll(viewHome, viewSearchBooks, viewBorrowedBooks, viewFines, viewAccountStatus);
        
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
        lblWelcome = new Label("Welcome back! 👋");
        lblWelcome.getStyleClass().add("welcome-banner-title");
        Label bannerSub = new Label("Here is a summary of your library activity.");
        bannerSub.getStyleClass().add("welcome-banner-sub");
        banner.getChildren().addAll(lblWelcome, bannerSub);

        HBox statsRow = new HBox(18);
        lblStatTotalBooks = createStatCard(statsRow, "Total Books", "linear-gradient(to bottom right,#2563eb,#60a5fa)");
        lblStatBorrowed = createStatCard(statsRow, "My Borrowed", "linear-gradient(to bottom right,#059669,#34d399)");
        lblStatPending = createStatCard(statsRow, "Pending Returns", "linear-gradient(to bottom right,#d97706,#fbbf24)");
        lblStatFines = createStatCard(statsRow, "Unpaid Fines", "linear-gradient(to bottom right,#dc2626,#f87171)");

        HBox chartsRow = new HBox(18);
        VBox pieBox = createChartCard("📊 My Book Status", "Overview of your borrowed vs returned books");
        chartMyStatus = new PieChart();
        chartMyStatus.setPrefHeight(280);
        pieBox.getChildren().add(chartMyStatus);
        HBox.setHgrow(pieBox, Priority.ALWAYS);

        VBox barBox = createChartCard("📈 Library Overview", "Available books vs books currently issued");
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        chartLibraryOverview = new BarChart<>(xAxis, yAxis);
        chartLibraryOverview.setPrefHeight(280);
        chartLibraryOverview.setLegendVisible(false);
        barBox.getChildren().add(chartLibraryOverview);
        HBox.setHgrow(barBox, Priority.ALWAYS);
        chartsRow.getChildren().addAll(pieBox, barBox);

        VBox notifBox = new VBox();
        notifBox.getStyleClass().add("card");
        HBox notifHeader = new HBox();
        notifHeader.setStyle("-fx-padding:18 24 14 24;-fx-border-color:transparent transparent #e2e8f0 transparent;-fx-border-width:1;");
        Label notifTitle = new Label("🔔 Latest Notifications");
        notifTitle.getStyleClass().add("card-header-label");
        notifHeader.getChildren().add(notifTitle);
        
        VBox notifTableBox = new VBox();
        notifTableBox.setPadding(new Insets(12, 24, 22, 24));
        tblNotifications = new TableView<>();
        tblNotifications.setPrefHeight(180);
        // Table columns will be set by controller for better type handling if needed, or here
        notifTableBox.getChildren().add(tblNotifications);
        notifBox.getChildren().addAll(notifHeader, notifTableBox);

        viewHome.getChildren().addAll(banner, statsRow, chartsRow, notifBox);
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

    private void setupSearchSection() {
        viewSearchBooks.getStyleClass().add("card");
        viewSearchBooks.setManaged(false);
        viewSearchBooks.setVisible(false);

        HBox header = new HBox();
        header.setStyle("-fx-padding:20 24 16 24;-fx-border-color:transparent transparent #e2e8f0 transparent;-fx-border-width:1;");
        VBox headerText = new VBox(2);
        Label title = new Label("🔍 Search Books");
        title.getStyleClass().add("card-header-label");
        Label sub = new Label("Search by title or author and borrow available books");
        sub.getStyleClass().add("card-subheader-label");
        headerText.getChildren().addAll(title, sub);
        header.getChildren().add(headerText);

        VBox body = new VBox(16);
        body.setPadding(new Insets(16, 24, 24, 24));
        
        HBox searchRow = new HBox(12);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        txtSearch = new TextField();
        txtSearch.setPromptText("🔍  Search by title or author...");
        txtSearch.setStyle("-fx-padding: 11 14;");
        HBox.setHgrow(txtSearch, Priority.ALWAYS);
        btnSearch = new Button("  Search  ");
        btnSearch.getStyleClass().add("btn-primary");
        btnSearch.setStyle("-fx-padding:11 28;");
        searchRow.getChildren().addAll(txtSearch, btnSearch);

        tblBooks = new TableView<>();
        tblBooks.setPrefHeight(430);

        HBox footer = new HBox(14);
        footer.setAlignment(Pos.CENTER_RIGHT);
        lblSearchStatus = new Label();
        lblSearchStatus.getStyleClass().add("status-label-success");
        btnBorrow = new Button("📥 Borrow Selected Book");
        btnBorrow.getStyleClass().add("btn-success");
        footer.getChildren().addAll(lblSearchStatus, btnBorrow);

        body.getChildren().addAll(searchRow, tblBooks, footer);
        viewSearchBooks.getChildren().addAll(header, body);
    }

    private void setupBorrowedSection() {
        viewBorrowedBooks.getStyleClass().add("card");
        viewBorrowedBooks.setManaged(false);
        viewBorrowedBooks.setVisible(false);

        HBox header = new HBox();
        header.setStyle("-fx-padding:20 24 16 24;-fx-border-color:transparent transparent #e2e8f0 transparent;-fx-border-width:1;");
        VBox headerText = new VBox(2);
        Label title = new Label("📚 My Borrowed Books");
        title.getStyleClass().add("card-header-label");
        Label sub = new Label("Books you currently have borrowed");
        sub.getStyleClass().add("card-subheader-label");
        headerText.getChildren().addAll(title, sub);
        header.getChildren().add(headerText);

        VBox body = new VBox(16);
        body.setPadding(new Insets(16, 24, 24, 24));
        tblIssues = new TableView<>();
        tblIssues.setPrefHeight(470);
        
        HBox footer = new HBox(14);
        footer.setAlignment(Pos.CENTER_RIGHT);
        lblReturnStatus = new Label();
        lblReturnStatus.getStyleClass().add("status-label-success");
        btnReturn = new Button("↩ Return Selected Book");
        btnReturn.getStyleClass().add("btn-primary");
        footer.getChildren().addAll(lblReturnStatus, btnReturn);

        body.getChildren().addAll(tblIssues, footer);
        viewBorrowedBooks.getChildren().addAll(header, body);
    }

    private void setupFinesSection() {
        viewFines.getStyleClass().add("card");
        viewFines.setManaged(false);
        viewFines.setVisible(false);

        HBox header = new HBox();
        header.setStyle("-fx-padding:20 24 16 24;-fx-border-color:transparent transparent #e2e8f0 transparent;-fx-border-width:1;");
        VBox headerText = new VBox(2);
        Label title = new Label("💳 My Fines");
        title.getStyleClass().add("card-header-label");
        Label sub = new Label("Outstanding overdue fines on your account");
        sub.getStyleClass().add("card-subheader-label");
        headerText.getChildren().addAll(title, sub);
        header.getChildren().add(headerText);

        VBox body = new VBox(16);
        body.setPadding(new Insets(16, 24, 24, 24));
        tblFines = new TableView<>();
        tblFines.setPrefHeight(450);

        HBox footer = new HBox(14);
        footer.setAlignment(Pos.CENTER_RIGHT);
        lblFineStatus = new Label();
        lblFineStatus.getStyleClass().add("status-label-success");
        btnPayFine = new Button("💳 Pay Selected Fine");
        btnPayFine.getStyleClass().add("btn-success");
        footer.getChildren().addAll(lblFineStatus, btnPayFine);

        body.getChildren().addAll(tblFines, footer);
        viewFines.getChildren().addAll(header, body);
    }

    private void setupAccountSection() {
        viewAccountStatus.getStyleClass().add("card");
        viewAccountStatus.setManaged(false);
        viewAccountStatus.setVisible(false);

        HBox header = new HBox();
        header.setStyle("-fx-padding:20 24 16 24;-fx-border-color:transparent transparent #e2e8f0 transparent;-fx-border-width:1;");
        VBox headerText = new VBox(2);
        Label title = new Label("👤 Account Status");
        title.getStyleClass().add("card-header-label");
        Label sub = new Label("Your profile, borrowings and fines at a glance");
        sub.getStyleClass().add("card-subheader-label");
        headerText.getChildren().addAll(title, sub);
        header.getChildren().add(headerText);

        VBox body = new VBox(22);
        body.setPadding(new Insets(18, 24, 28, 24));

        VBox profileSection = new VBox(14);
        profileSection.getStyleClass().add("form-section");
        Label profileHeader = new Label("PROFILE");
        profileHeader.getStyleClass().add("sidebar-section-label");
        profileHeader.setStyle("-fx-padding:0;");
        
        HBox profileGrid = new HBox(40);
        lblAccName = createProfileLabel(profileGrid, "Full Name");
        lblAccEmail = createProfileLabel(profileGrid, "Email");
        lblAccPhone = createProfileLabel(profileGrid, "Phone");
        lblAccRole = createProfileLabel(profileGrid, "Role");
        profileSection.getChildren().addAll(profileHeader, profileGrid);

        VBox issuesSection = new VBox(10);
        Label activeLabel = new Label("ACTIVE BORROWINGS");
        activeLabel.getStyleClass().add("sidebar-section-label");
        tblAccIssues = new TableView<>();
        tblAccIssues.setPrefHeight(220);
        issuesSection.getChildren().addAll(activeLabel, tblAccIssues);

        VBox finesSection = new VBox(10);
        Label unpaidLabel = new Label("UNPAID FINES");
        unpaidLabel.getStyleClass().add("sidebar-section-label");
        tblAccFines = new TableView<>();
        tblAccFines.setPrefHeight(180);
        finesSection.getChildren().addAll(unpaidLabel, tblAccFines);

        body.getChildren().addAll(profileSection, issuesSection, finesSection);
        viewAccountStatus.getChildren().addAll(header, body);
    }

    private Label createProfileLabel(HBox parent, String labelText) {
        VBox v = new VBox(4);
        Label l = new Label(labelText);
        l.getStyleClass().add("form-label");
        Label val = new Label("—");
        val.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#0f172a;");
        v.getChildren().addAll(l, val);
        parent.getChildren().add(v);
        return val;
    }

    // --- GETTERS ---
    public Button getBtnSideHome() { return btnSideHome; }
    public Button getBtnSideSearch() { return btnSideSearch; }
    public Button getBtnSideBorrowed() { return btnSideBorrowed; }
    public Button getBtnSideFines() { return btnSideFines; }
    public Button getBtnSideAccount() { return btnSideAccount; }
    public Button getBtnLogout() { return btnLogout; }
    public Label getLblPageTitle() { return lblPageTitle; }
    public Label getLblTopUsername() { return lblTopUsername; }
    public VBox getViewHome() { return viewHome; }
    public VBox getViewSearchBooks() { return viewSearchBooks; }
    public VBox getViewBorrowedBooks() { return viewBorrowedBooks; }
    public VBox getViewFines() { return viewFines; }
    public VBox getViewAccountStatus() { return viewAccountStatus; }
    public Label getLblWelcome() { return lblWelcome; }
    public Label getLblStatTotalBooks() { return lblStatTotalBooks; }
    public Label getLblStatBorrowed() { return lblStatBorrowed; }
    public Label getLblStatPending() { return lblStatPending; }
    public Label getLblStatFines() { return lblStatFines; }
    public PieChart getChartMyStatus() { return chartMyStatus; }
    public BarChart<String, Number> getChartLibraryOverview() { return chartLibraryOverview; }
    public TableView<NotificationModel> getTblNotifications() { return tblNotifications; }
    public TextField getTxtSearch() { return txtSearch; }
    public Button getBtnSearch() { return btnSearch; }
    public Button getBtnBorrow() { return btnBorrow; }
    public TableView<BookModel> getTblBooks() { return tblBooks; }
    public Label getLblSearchStatus() { return lblSearchStatus; }
    public TableView<IssueModel> getTblIssues() { return tblIssues; }
    public Button getBtnReturn() { return btnReturn; }
    public Label getLblReturnStatus() { return lblReturnStatus; }
    public TableView<FineModel> getTblFines() { return tblFines; }
    public Button getBtnPayFine() { return btnPayFine; }
    public Label getLblFineStatus() { return lblFineStatus; }
    public Label getLblAccName() { return lblAccName; }
    public Label getLblAccEmail() { return lblAccEmail; }
    public Label getLblAccPhone() { return lblAccPhone; }
    public Label getLblAccRole() { return lblAccRole; }
    public TableView<IssueModel> getTblAccIssues() { return tblAccIssues; }
    public TableView<FineModel> getTblAccFines() { return tblAccFines; }
}
