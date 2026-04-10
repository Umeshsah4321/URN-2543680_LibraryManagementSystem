package application.views;

import application.controllers.StudentDashboardController;
import application.models.BookModel;
import application.models.FineModel;
import application.models.IssueModel;
import application.models.NotificationModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class StudentDashboardView extends BorderPane {

    private StudentDashboardController controller;

    // Sidebar fields
    private Button btnSideHome, btnSideSearch, btnSideMyBooks, btnSideFines, btnSideAccount;
    private VBox viewHome, viewSearchBooks, viewMyBooks, viewFines, viewAccount;

    // Dashboard Home fields
    private Label lblPageTitle, lblStatTotalBooks, lblStatBorrowed, lblStatPending, lblStatFines;
    private TableView<NotificationModel> tblLatestNotifications;
    private PieChart chartBorrowStatus;
    private BarChart<String, Number> chartLibraryOverview;

    // Search Books fields
    private TextField txtSearchBook;
    private TableView<BookModel> tblSearchBooks;

    // My Books fields
    private TableView<IssueModel> tblMyBooks;

    // Fines fields
    private TableView<FineModel> tblFines;
    private Label lblFineStatus;

    // Account fields
    private Label lblAccName, lblAccEmail, lblAccPhone, lblAccAddress, lblAccRole, lblAccJoined;
    private TableView<NotificationModel> tblAccNotifications;

    public StudentDashboardView() {
        this.controller = new StudentDashboardController();
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
        Label subLogoLabel = new Label("Student Portal");
        subLogoLabel.getStyleClass().add("sidebar-subtitle");
        logoBox.getChildren().addAll(logoLabel, subLogoLabel);

        Region divider1 = new Region();
        divider1.getStyleClass().add("sidebar-divider");
        divider1.setPrefHeight(1);
        divider1.setMaxWidth(Double.MAX_VALUE);

        Label mainMenuLabel = new Label("MAIN MENU");
        mainMenuLabel.getStyleClass().add("sidebar-section-label");

        btnSideHome = createSidebarBtn("=  Home");
        btnSideSearch = createSidebarBtn("🔍  Search Books");
        btnSideMyBooks = createSidebarBtn("📚  Borrowed Books");
        btnSideFines = createSidebarBtn("💳  Pay Fine");
        btnSideAccount = createSidebarBtn("👤  Account Status");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("🚪  Logout");
        btnLogout.getStyleClass().add("btn-logout");
        btnLogout.setPrefWidth(220);

        sidebar.getChildren().addAll(logoBox, divider1, mainMenuLabel, btnSideHome, btnSideSearch, btnSideMyBooks, btnSideFines, btnSideAccount, spacer, btnLogout);
        setLeft(sidebar);

        // --- Main Content Area ---
        VBox mainPane = new VBox();
        VBox.setVgrow(mainPane, Priority.ALWAYS);

        // Top Bar
        HBox topBar = new HBox(12);
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        lblPageTitle = new Label("Home");
        lblPageTitle.getStyleClass().add("page-title-label");
        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        Label lblUser = new Label("👤 Umesh (Student)");
        lblUser.getStyleClass().add("user-badge");
        topBar.getChildren().addAll(lblPageTitle, topSpacer, lblUser);

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
        initSearchBooksView();
        initMyBooksView();
        initFinesView();
        initAccountView();

        contentArea.getChildren().addAll(viewHome, viewSearchBooks, viewMyBooks, viewFines, viewAccount);

        // --- Wire Controller ---
        wireController();

        // Event Handlers
        btnSideHome.setOnAction(e -> controller.showHome());
        btnSideSearch.setOnAction(e -> controller.showSearchBooks());
        btnSideMyBooks.setOnAction(e -> controller.showMyBooks());
        btnSideFines.setOnAction(e -> controller.showFines());
        btnSideAccount.setOnAction(e -> controller.showAccount());
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
        viewHome = new VBox(24);

        VBox banner = new VBox(10);
        banner.getStyleClass().add("welcome-banner");
        Label bTitle = new Label("Welcome back, Umesh! 👋");
        bTitle.getStyleClass().add("welcome-banner-title");
        Label bSub = new Label("Here is a summary of your library activity.");
        bSub.getStyleClass().add("welcome-banner-sub");
        banner.getChildren().addAll(bTitle, bSub);

        HBox statRow = new HBox(18);
        lblStatTotalBooks = createStatCard(statRow, "Total Books", "stat-card-blue");
        lblStatBorrowed = createStatCard(statRow, "My Borrowed", "stat-card-green");
        lblStatPending = createStatCard(statRow, "Pending Returns", "stat-card-orange");
        lblStatFines = createStatCard(statRow, "Unpaid Fines", "stat-card-red");

        HBox chartRow = new HBox(20);
        chartBorrowStatus = new PieChart();
        setupChartCard(chartRow, "⌸ My Book Status", "Overview of your borrowed vs returned books", chartBorrowStatus);

        chartLibraryOverview = new BarChart<>(new CategoryAxis(), new NumberAxis());
        setupChartCard(chartRow, "📈 Library Overview", "Available books vs books currently issued", chartLibraryOverview);

        VBox notifSection = new VBox(12);
        notifSection.getStyleClass().add("card");
        Label lblNotif = new Label("🔔 Latest Notifications");
        lblNotif.getStyleClass().add("card-header-label");
        tblLatestNotifications = new TableView<>();
        tblLatestNotifications.getStyleClass().add("dashboard-table");
        tblLatestNotifications.setPrefHeight(200);
        addColumn(tblLatestNotifications, "Date", "createdAt", 180);
        addColumn(tblLatestNotifications, "Type", "type", 140);
        addColumn(tblLatestNotifications, "Message", "message", 600);
        notifSection.getChildren().addAll(lblNotif, tblLatestNotifications);

        viewHome.getChildren().addAll(banner, statRow, chartRow, notifSection);
    }

    private Label createStatCard(HBox parent, String label, String styleClass) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("stat-card", styleClass);
        card.setAlignment(Pos.CENTER);
        HBox.setHgrow(card, Priority.ALWAYS);
        Label num = new Label("0");
        num.getStyleClass().add("stat-number");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-label");
        card.getChildren().addAll(num, lbl);
        parent.getChildren().add(card);
        return num;
    }

    private void setupChartCard(HBox parent, String title, String sub, Region chart) {
        VBox card = new VBox(8);
        card.getStyleClass().add("chart-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        Label lblT = new Label(title); lblT.getStyleClass().add("chart-title-label");
        Label lblS = new Label(sub); lblS.getStyleClass().add("card-subheader-label");
        chart.setPrefHeight(300); chart.setMaxWidth(Double.MAX_VALUE);
        card.getChildren().addAll(lblT, lblS, chart);
        parent.getChildren().add(card);
    }

    private void initSearchBooksView() {
        viewSearchBooks = new VBox(18);
        viewSearchBooks.getStyleClass().add("card");
        viewSearchBooks.setManaged(false); viewVisible(viewSearchBooks, false);

        Label lbl = new Label("🔍 Search Books");
        lbl.getStyleClass().add("card-header-label");

        HBox searchRow = new HBox(12);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        txtSearchBook = new TextField();
        txtSearchBook.setPromptText("Search by title, author, or publisher...");
        HBox.setHgrow(txtSearchBook, Priority.ALWAYS);
        txtSearchBook.setOnKeyReleased(e -> controller.searchBooks());
        Button btnSearch = new Button("🔍 Search");
        btnSearch.setOnAction(e -> controller.searchBooks());
        searchRow.getChildren().addAll(txtSearchBook, btnSearch);

        tblSearchBooks = new TableView<>();
        tblSearchBooks.setPrefHeight(500);
        addColumn(tblSearchBooks, "ID", "bookId", 70);
        addColumn(tblSearchBooks, "Title", "title", 350);
        addColumn(tblSearchBooks, "Author", "author", 250);
        addColumn(tblSearchBooks, "Publisher", "publisher", 200);
        addColumn(tblSearchBooks, "Year", "publicationYear", 100);
        addColumn(tblSearchBooks, "Available", "availableCopies", 120);

        viewSearchBooks.getChildren().addAll(lbl, searchRow, tblSearchBooks);
    }

    private void initMyBooksView() {
        viewMyBooks = new VBox(18);
        viewMyBooks.getStyleClass().add("card");
        viewMyBooks.setManaged(false); viewVisible(viewMyBooks, false);

        Label lbl = new Label("📚 My Borrowed Books");
        lbl.getStyleClass().add("card-header-label");

        tblMyBooks = new TableView<>();
        tblMyBooks.setPrefHeight(550);
        addColumn(tblMyBooks, "Issue ID", "issueId", 90);
        addColumn(tblMyBooks, "Book Title", "bookTitle", 400);
        addColumn(tblMyBooks, "Issue Date", "issueDate", 160);
        addColumn(tblMyBooks, "Return Date", "returnDate", 160);
        addColumn(tblMyBooks, "Status", "status", 140);

        viewMyBooks.getChildren().addAll(lbl, tblMyBooks);
    }

    private void initFinesView() {
        viewFines = new VBox(18);
        viewFines.getStyleClass().add("card");
        viewFines.setManaged(false); viewVisible(viewFines, false);

        Label lbl = new Label("💳 My Fines");
        lbl.getStyleClass().add("card-header-label");

        tblFines = new TableView<>();
        tblFines.setPrefHeight(450);
        addColumn(tblFines, "Fine ID", "fineId", 90);
        addColumn(tblFines, "Book Title", "bookTitle", 400);
        addColumn(tblFines, "Fine Amount", "amount", 140);
        addColumn(tblFines, "Fine Date", "fineDate", 160);
        addColumn(tblFines, "Status", "status", 140);

        HBox footer = new HBox(12);
        footer.setAlignment(Pos.CENTER_RIGHT);
        lblFineStatus = new Label();
        Button btnPay = new Button("💳 Pay Selected Fine");
        btnPay.setOnAction(e -> controller.payFine());
        footer.getChildren().addAll(lblFineStatus, btnPay);

        viewFines.getChildren().addAll(lbl, tblFines, footer);
    }

    private void initAccountView() {
        viewAccount = new VBox(24);
        viewAccount.setManaged(false); viewVisible(viewAccount, false);

        HBox topRow = new HBox(18);
        VBox infoCard = new VBox(15);
        infoCard.getStyleClass().add("card");
        HBox.setHgrow(infoCard, Priority.ALWAYS);
        Label lblT = new Label("👤 My Details"); lblT.getStyleClass().add("card-header-label");
        GridPane grid = new GridPane();
        grid.setHgap(30); grid.setVgap(18);
        int r = 0;
        lblAccName = addDetailRow(grid, "Full Name:", r++);
        lblAccEmail = addDetailRow(grid, "Email:", r++);
        lblAccPhone = addDetailRow(grid, "Phone:", r++);
        lblAccAddress = addDetailRow(grid, "Address:", r++);
        lblAccRole = addDetailRow(grid, "Role:", r++);
        lblAccJoined = addDetailRow(grid, "Joined:", r++);
        infoCard.getChildren().addAll(lblT, grid);

        VBox notifCard = new VBox(15);
        notifCard.getStyleClass().add("card");
        HBox.setHgrow(notifCard, Priority.ALWAYS);
        Label lblN = new Label("🔔 Recent Notifications"); lblN.getStyleClass().add("card-header-label");
        tblAccNotifications = new TableView<>();
        tblAccNotifications.setPrefHeight(280);
        addColumn(tblAccNotifications, "Date", "createdAt", 140);
        addColumn(tblAccNotifications, "Message", "message", 600);
        notifCard.getChildren().addAll(lblN, tblAccNotifications);

        topRow.getChildren().addAll(infoCard, notifCard);
        viewAccount.getChildren().add(topRow);
    }

    private Label addDetailRow(GridPane grid, String label, int row) {
        Label l = new Label(label); l.setStyle("-fx-font-weight:bold; -fx-text-fill:#64748b;");
        Label val = new Label("—"); val.setStyle("-fx-font-size:14px; -fx-font-weight:bold;");
        grid.add(l, 0, row); grid.add(val, 1, row);
        return val;
    }

    private <T> void addColumn(TableView<T> tbl, String title, String prop, double width) {
        TableColumn<T, Object> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        tbl.getColumns().add(col);
    }

    private void viewVisible(VBox v, boolean vis) {
        v.setVisible(vis);
    }

    private void wireController() {
        controller.setFields(lblPageTitle, btnSideHome, btnSideSearch, btnSideMyBooks, btnSideFines, btnSideAccount,
                viewHome, viewSearchBooks, viewMyBooks, viewFines, viewAccount,
                lblStatTotalBooks, lblStatBorrowed, lblStatPending, lblStatFines,
                chartBorrowStatus, chartLibraryOverview, tblLatestNotifications,
                txtSearchBook, tblSearchBooks,
                tblMyBooks,
                tblFines, lblFineStatus,
                lblAccName, lblAccEmail, lblAccPhone, lblAccAddress, lblAccRole, lblAccJoined, tblAccNotifications);
    }
}

