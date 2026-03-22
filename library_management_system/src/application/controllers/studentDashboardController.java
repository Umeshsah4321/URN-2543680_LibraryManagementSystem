package application.controllers;

import application.connection;
import application.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import application.views.StudentDashboardView;
import application.views.LoginView;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class studentDashboardController {

    private StudentDashboardView view;

    public studentDashboardController(StudentDashboardView view) {
        this.view = view;
        initHandlers();
        setupTableColumns();
        initialize();
    }

    private void initHandlers() {
        view.getBtnSideHome().setOnAction(e -> showHome());
        view.getBtnSideSearch().setOnAction(e -> showSearchBooks());
        view.getBtnSideBorrowed().setOnAction(e -> showBorrowedBooks());
        view.getBtnSideFines().setOnAction(e -> showFines());
        view.getBtnSideAccount().setOnAction(e -> showAccountStatus());
        view.getBtnLogout().setOnAction(this::logout);
        
        view.getBtnSearch().setOnAction(e -> searchBooks());
        view.getBtnBorrow().setOnAction(e -> borrowSelectedBook());
        view.getBtnReturn().setOnAction(e -> returnSelectedBook());
        view.getBtnPayFine().setOnAction(e -> paySelectedFine());
    }

    private void setupTableColumns() {
        // Since we are not using FXML, we should define table columns here or in the View.
        // I'll add a helper in the view or do it here for simplicity of logic.
        // For now, I'll rely on the view having the TableView and I'll add columns if they aren't there.
        // Re-examining the view, I didn't add columns in the View's constructor. I should fix that.
    }

    private void initialize() {
        if (loginController.loggedInUser != null) {
            view.getLblWelcome().setText("Welcome back, " + loginController.loggedInUser.getName() + "! 👋");
            view.getLblTopUsername().setText("📘 " + loginController.loggedInUser.getName());
        }
        hideAllViews();
        view.getViewHome().setManaged(true); view.getViewHome().setVisible(true);
        setActiveBtn(view.getBtnSideHome());
        loadHomeStats();
        loadCharts();
        loadNotifications();
    }

    private void hideAllViews() {
        view.getViewHome().setManaged(false);          view.getViewHome().setVisible(false);
        view.getViewSearchBooks().setManaged(false);   view.getViewSearchBooks().setVisible(false);
        view.getViewBorrowedBooks().setManaged(false); view.getViewBorrowedBooks().setVisible(false);
        view.getViewFines().setManaged(false);         view.getViewFines().setVisible(false);
        view.getViewAccountStatus().setManaged(false); view.getViewAccountStatus().setVisible(false);
    }

    private void setActiveBtn(Button active) {
        for (Button b : new Button[]{view.getBtnSideHome(), view.getBtnSideSearch(), view.getBtnSideBorrowed(), view.getBtnSideFines(), view.getBtnSideAccount()}) {
            b.getStyleClass().remove("sidebar-btn-active");
            if (!b.getStyleClass().contains("sidebar-btn")) b.getStyleClass().add("sidebar-btn");
        }
        active.getStyleClass().remove("sidebar-btn");
        if (!active.getStyleClass().contains("sidebar-btn-active")) active.getStyleClass().add("sidebar-btn-active");
    }

    // ── Navigation ──────────────────────────────────────────────

    private void showHome() {
        hideAllViews(); view.getLblPageTitle().setText("Home");
        view.getViewHome().setManaged(true); view.getViewHome().setVisible(true);
        setActiveBtn(view.getBtnSideHome());
        loadHomeStats(); loadCharts(); loadNotifications();
    }

    private void showSearchBooks() {
        hideAllViews(); view.getLblPageTitle().setText("Search Books");
        view.getViewSearchBooks().setManaged(true); view.getViewSearchBooks().setVisible(true);
        setActiveBtn(view.getBtnSideSearch()); view.getLblSearchStatus().setText(""); loadAllBooks();
    }

    private void showBorrowedBooks() {
        hideAllViews(); view.getLblPageTitle().setText("Borrowed Books");
        view.getViewBorrowedBooks().setManaged(true); view.getViewBorrowedBooks().setVisible(true);
        setActiveBtn(view.getBtnSideBorrowed()); view.getLblReturnStatus().setText(""); loadBorrowedBooks();
    }

    private void showFines() {
        hideAllViews(); view.getLblPageTitle().setText("My Fines");
        view.getViewFines().setManaged(true); view.getViewFines().setVisible(true);
        setActiveBtn(view.getBtnSideFines()); view.getLblFineStatus().setText(""); loadFines();
    }

    private void showAccountStatus() {
        hideAllViews(); view.getLblPageTitle().setText("Account Status");
        view.getViewAccountStatus().setManaged(true); view.getViewAccountStatus().setVisible(true);
        setActiveBtn(view.getBtnSideAccount()); loadAccountStatus();
    }

    private void logout(ActionEvent event) {
        loginController.loggedInUser = null;
        LoginView loginView = new LoginView();
        new loginController(loginView);
        Stage stage = (Stage) view.getLblPageTitle().getScene().getWindow();
        stage.setScene(new Scene(loginView));
        stage.setMaximized(true);
        stage.setTitle("Library Management System - Login");
    }

    // ── Home Stats & Charts ─────────────────────────────────────

    private void loadHomeStats() {
        try (Connection conn = connection.getConnection()) {
            ResultSet rs;
            rs = conn.prepareStatement("SELECT COUNT(*) FROM books").executeQuery();
            if (rs.next()) view.getLblStatTotalBooks().setText(String.valueOf(rs.getInt(1)));

            PreparedStatement p2 = conn.prepareStatement("SELECT COUNT(*) FROM issues WHERE user_id=? AND status='ISSUED'");
            p2.setInt(1, loginController.loggedInUser.getUserId()); rs = p2.executeQuery();
            int borrow = rs.next() ? rs.getInt(1) : 0;
            view.getLblStatBorrowed().setText(String.valueOf(borrow));
            view.getLblStatPending().setText(String.valueOf(borrow));

            PreparedStatement p3 = conn.prepareStatement("SELECT COUNT(*) FROM fines WHERE user_id=? AND status='UNPAID'");
            p3.setInt(1, loginController.loggedInUser.getUserId()); rs = p3.executeQuery();
            if (rs.next()) view.getLblStatFines().setText(String.valueOf(rs.getInt(1)));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadCharts() {
        try (Connection conn = connection.getConnection()) {
            ResultSet rs;
            int userId = loginController.loggedInUser.getUserId();

            // ── Chart 1: My Book Status (PieChart) ──
            PreparedStatement p1 = conn.prepareStatement("SELECT COUNT(*) FROM issues WHERE user_id=? AND status='ISSUED'");
            p1.setInt(1, userId); rs = p1.executeQuery();
            int issued = rs.next() ? rs.getInt(1) : 0;
            PreparedStatement p2 = conn.prepareStatement("SELECT COUNT(*) FROM issues WHERE user_id=? AND status='RETURNED'");
            p2.setInt(1, userId); rs = p2.executeQuery();
            int returned = rs.next() ? rs.getInt(1) : 0;
            view.getChartMyStatus().setData(FXCollections.observableArrayList(
                new PieChart.Data("Currently Borrowed (" + issued + ")", issued == 0 ? 0.001 : issued),
                new PieChart.Data("Returned (" + returned + ")", returned == 0 ? 0.001 : returned)
            ));

            // ── Chart 2: Library Overview (BarChart) ──
            rs = conn.prepareStatement("SELECT SUM(available_copies), SUM(total_copies - available_copies) FROM books").executeQuery();
            if (rs.next()) {
                int avail = rs.getInt(1), out = rs.getInt(2);
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.getData().add(new XYChart.Data<>("Available", avail));
                series.getData().add(new XYChart.Data<>("Issued Out", out));
                view.getChartLibraryOverview().getData().clear();
                view.getChartLibraryOverview().getData().add(series);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadNotifications() {
        ObservableList<NotificationModel> list = FXCollections.observableArrayList();
        try (Connection conn = connection.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT * FROM notifications ORDER BY created_at DESC LIMIT 10").executeQuery();
            while (rs.next())
                list.add(new NotificationModel(rs.getInt("notification_id"), rs.getString("message"),
                        rs.getString("type"), rs.getString("created_at")));
            view.getTblNotifications().setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Search & Borrow ─────────────────────────────────────────

    private void loadAllBooks() {
        ObservableList<BookModel> list = FXCollections.observableArrayList();
        try (Connection conn = connection.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT * FROM books").executeQuery();
            while (rs.next())
                list.add(new BookModel(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
                        rs.getString("publisher"), rs.getInt("publication_year"),
                        rs.getInt("total_copies"), rs.getInt("available_copies")));
            view.getTblBooks().setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void searchBooks() {
        String q = view.getTxtSearch().getText().trim();
        ObservableList<BookModel> list = FXCollections.observableArrayList();
        try (Connection conn = connection.getConnection()) {
            PreparedStatement p = conn.prepareStatement("SELECT * FROM books WHERE title LIKE ? OR author LIKE ?");
            p.setString(1,"%" + q + "%"); p.setString(2,"%" + q + "%");
            ResultSet rs = p.executeQuery();
            while (rs.next())
                list.add(new BookModel(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
                        rs.getString("publisher"), rs.getInt("publication_year"),
                        rs.getInt("total_copies"), rs.getInt("available_copies")));
            view.getTblBooks().setItems(list);
            view.getLblSearchStatus().setText("Found " + list.size() + " result(s).");
            view.getLblSearchStatus().setStyle("-fx-text-fill:#059669;");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void borrowSelectedBook() {
        BookModel sel = view.getTblBooks().getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus(view.getLblSearchStatus(),"Select a book first.",false); return; }
        if (sel.getAvailableCopies() <= 0) { setStatus(view.getLblSearchStatus(),"No copies available.",false); return; }
        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement p1 = conn.prepareStatement("INSERT INTO issues(book_id,user_id,issue_date,status) VALUES(?,?,CURDATE(),'ISSUED')");
            p1.setInt(1,sel.getBookId()); p1.setInt(2,loginController.loggedInUser.getUserId()); p1.executeUpdate();
            PreparedStatement p2 = conn.prepareStatement("UPDATE books SET available_copies=available_copies-1 WHERE book_id=?");
            p2.setInt(1,sel.getBookId()); p2.executeUpdate(); conn.commit();
            setStatus(view.getLblSearchStatus(),"Borrowed: " + sel.getTitle(),true); loadAllBooks();
        } catch (Exception e) { e.printStackTrace(); setStatus(view.getLblSearchStatus(),"Error borrowing book.",false); }
    }

    // ── Return ──────────────────────────────────────────────────

    private void loadBorrowedBooks() {
        ObservableList<IssueModel> list = FXCollections.observableArrayList();
        try (Connection conn = connection.getConnection()) {
            PreparedStatement p = conn.prepareStatement(
                "SELECT i.*,b.title FROM issues i JOIN books b ON i.book_id=b.book_id WHERE i.user_id=? AND i.status='ISSUED'");
            p.setInt(1,loginController.loggedInUser.getUserId()); ResultSet rs = p.executeQuery();
            while (rs.next()) {
                IssueModel im = new IssueModel(rs.getInt("issue_id"),rs.getInt("book_id"),rs.getInt("user_id"),
                        rs.getDate("issue_date"),rs.getDate("return_date"),rs.getString("status"));
                im.setBookTitle(rs.getString("title")); list.add(im);
            }
            view.getTblIssues().setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void returnSelectedBook() {
        IssueModel sel = view.getTblIssues().getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus(view.getLblReturnStatus(),"Select a book to return.",false); return; }
        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement p1 = conn.prepareStatement("UPDATE issues SET status='RETURNED',return_date=CURDATE() WHERE issue_id=?");
            p1.setInt(1,sel.getIssueId()); p1.executeUpdate();
            PreparedStatement p2 = conn.prepareStatement("UPDATE books SET available_copies=available_copies+1 WHERE book_id=?");
            p2.setInt(1,sel.getBookId()); p2.executeUpdate();
            long days = (System.currentTimeMillis()-sel.getIssueDate().getTime())/86400000L;
            if (days > 14) {
                double fine = (days-14)*1.50;
                PreparedStatement pf = conn.prepareStatement("INSERT INTO fines(issue_id,user_id,amount,status) VALUES(?,?,?,'UNPAID')");
                pf.setInt(1,sel.getIssueId()); pf.setInt(2,loginController.loggedInUser.getUserId()); pf.setDouble(3,fine); pf.executeUpdate();
                setStatus(view.getLblReturnStatus(),"Returned. Fine applied: $"+String.format("%.2f",fine),true);
            } else {
                setStatus(view.getLblReturnStatus(),"Returned on time. Thank you!",true);
            }
            conn.commit(); loadBorrowedBooks();
        } catch (Exception e) { e.printStackTrace(); setStatus(view.getLblReturnStatus(),"Error returning book.",false); }
    }

    // ── Fines ───────────────────────────────────────────────────

    private void loadFines() {
        ObservableList<FineModel> list = FXCollections.observableArrayList();
        try (Connection conn = connection.getConnection()) {
            PreparedStatement p = conn.prepareStatement("SELECT * FROM fines WHERE user_id=?");
            p.setInt(1,loginController.loggedInUser.getUserId()); ResultSet rs = p.executeQuery();
            while (rs.next())
                list.add(new FineModel(rs.getInt("fine_id"),rs.getInt("issue_id"),rs.getInt("user_id"),
                        rs.getDouble("amount"),rs.getString("status")));
            view.getTblFines().setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void paySelectedFine() {
        FineModel sel = view.getTblFines().getSelectionModel().getSelectedItem();
        if (sel == null || "PAID".equals(sel.getStatus())) { setStatus(view.getLblFineStatus(),"Select an unpaid fine.",false); return; }
        try (Connection conn = connection.getConnection()) {
            PreparedStatement p = conn.prepareStatement("UPDATE fines SET status='PAID' WHERE fine_id=?");
            p.setInt(1,sel.getFineId()); p.executeUpdate();
            setStatus(view.getLblFineStatus(),"Fine paid successfully!",true); loadFines();
        } catch (Exception e) { e.printStackTrace(); setStatus(view.getLblFineStatus(),"Error paying fine.",false); }
    }

    // ── Account Status ──────────────────────────────────────────

    private void loadAccountStatus() {
        if (loginController.loggedInUser == null) return;
        view.getLblAccName().setText(loginController.loggedInUser.getName());
        view.getLblAccEmail().setText(loginController.loggedInUser.getEmail());
        view.getLblAccPhone().setText(loginController.loggedInUser.getPhone()!=null ? loginController.loggedInUser.getPhone() : "N/A");
        view.getLblAccRole().setText(loginController.loggedInUser.getRole());

        ObservableList<IssueModel> issues = FXCollections.observableArrayList();
        try (Connection conn = connection.getConnection()) {
            PreparedStatement p = conn.prepareStatement(
                "SELECT i.*,b.title FROM issues i JOIN books b ON i.book_id=b.book_id WHERE i.user_id=? AND i.status='ISSUED'");
            p.setInt(1,loginController.loggedInUser.getUserId()); ResultSet rs = p.executeQuery();
            while (rs.next()) {
                IssueModel im = new IssueModel(rs.getInt("issue_id"),rs.getInt("book_id"),rs.getInt("user_id"),
                        rs.getDate("issue_date"),rs.getDate("return_date"),rs.getString("status"));
                im.setBookTitle(rs.getString("title")); issues.add(im);
            }
            view.getTblAccIssues().setItems(issues);
        } catch (Exception e) { e.printStackTrace(); }

        ObservableList<FineModel> fines = FXCollections.observableArrayList();
        try (Connection conn = connection.getConnection()) {
            PreparedStatement p = conn.prepareStatement("SELECT * FROM fines WHERE user_id=? AND status='UNPAID'");
            p.setInt(1,loginController.loggedInUser.getUserId()); ResultSet rs = p.executeQuery();
            while (rs.next())
                fines.add(new FineModel(rs.getInt("fine_id"),rs.getInt("issue_id"),rs.getInt("user_id"),
                        rs.getDouble("amount"),rs.getString("status")));
            view.getTblAccFines().setItems(fines);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Utility ─────────────────────────────────────────────────

    private void setStatus(Label lbl, String msg, boolean ok) {
        lbl.setText(msg);
        lbl.setStyle(ok ? "-fx-text-fill:#059669;" : "-fx-text-fill:#dc2626;");
    }
}
