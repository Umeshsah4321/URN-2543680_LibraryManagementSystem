package application.controllers;

import application.connection;
import application.models.BookModel;
import application.models.FineModel;
import application.models.IssueModel;
import application.models.NotificationModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class studentDashboardController {

    @FXML private Label lblPageTitle;
    @FXML private Label lblTopUsername;
    @FXML private Label lblWelcome;

    // Sidebar
    @FXML private Button btnSideHome, btnSideSearch, btnSideBorrowed, btnSideFines, btnSideAccount;

    // Views
    @FXML private VBox viewHome, viewSearchBooks, viewBorrowedBooks, viewFines, viewAccountStatus;

    // Home Stats
    @FXML private Label lblStatTotalBooks, lblStatBorrowed, lblStatPending, lblStatFines;

    // Charts
    @FXML private PieChart chartMyStatus;
    @FXML private BarChart<String, Number> chartLibraryOverview;

    // Notifications table
    @FXML private TableView<NotificationModel> tblNotifications;

    // Search
    @FXML private TextField txtSearch;
    @FXML private TableView<BookModel> tblBooks;
    @FXML private Label lblSearchStatus;

    // Borrowed
    @FXML private TableView<IssueModel> tblIssues;
    @FXML private Label lblReturnStatus;

    // Fines
    @FXML private TableView<FineModel> tblFines;
    @FXML private Label lblFineStatus;

    // Account Status
    @FXML private Label lblAccName, lblAccEmail, lblAccPhone, lblAccRole;
    @FXML private TableView<IssueModel> tblAccIssues;
    @FXML private TableView<FineModel> tblAccFines;

    @FXML
    private void initialize() {
        if (loginController.loggedInUser != null) {
            lblWelcome.setText("Welcome back, " + loginController.loggedInUser.getName() + "! 👋");
            lblTopUsername.setText("📘 " + loginController.loggedInUser.getName());
        }
        hideAllViews();
        viewHome.setManaged(true); viewHome.setVisible(true);
        setActiveBtn(btnSideHome);
        loadHomeStats();
        loadCharts();
        loadNotifications();
    }

    private void hideAllViews() {
        viewHome.setManaged(false);          viewHome.setVisible(false);
        viewSearchBooks.setManaged(false);   viewSearchBooks.setVisible(false);
        viewBorrowedBooks.setManaged(false); viewBorrowedBooks.setVisible(false);
        viewFines.setManaged(false);         viewFines.setVisible(false);
        viewAccountStatus.setManaged(false); viewAccountStatus.setVisible(false);
    }

    private void setActiveBtn(Button active) {
        for (Button b : new Button[]{btnSideHome, btnSideSearch, btnSideBorrowed, btnSideFines, btnSideAccount}) {
            b.getStyleClass().remove("sidebar-btn-active");
            if (!b.getStyleClass().contains("sidebar-btn")) b.getStyleClass().add("sidebar-btn");
        }
        active.getStyleClass().remove("sidebar-btn");
        if (!active.getStyleClass().contains("sidebar-btn-active")) active.getStyleClass().add("sidebar-btn-active");
    }

    // ── Navigation ──────────────────────────────────────────────

    @FXML private void showHome() {
        hideAllViews(); lblPageTitle.setText("Home");
        viewHome.setManaged(true); viewHome.setVisible(true);
        setActiveBtn(btnSideHome);
        loadHomeStats(); loadCharts(); loadNotifications();
    }

    @FXML private void showSearchBooks() {
        hideAllViews(); lblPageTitle.setText("Search Books");
        viewSearchBooks.setManaged(true); viewSearchBooks.setVisible(true);
        setActiveBtn(btnSideSearch); lblSearchStatus.setText(""); loadAllBooks();
    }

    @FXML private void showBorrowedBooks() {
        hideAllViews(); lblPageTitle.setText("Borrowed Books");
        viewBorrowedBooks.setManaged(true); viewBorrowedBooks.setVisible(true);
        setActiveBtn(btnSideBorrowed); lblReturnStatus.setText(""); loadBorrowedBooks();
    }

    @FXML private void showFines() {
        hideAllViews(); lblPageTitle.setText("My Fines");
        viewFines.setManaged(true); viewFines.setVisible(true);
        setActiveBtn(btnSideFines); lblFineStatus.setText(""); loadFines();
    }

    @FXML private void showAccountStatus() {
        hideAllViews(); lblPageTitle.setText("Account Status");
        viewAccountStatus.setManaged(true); viewAccountStatus.setVisible(true);
        setActiveBtn(btnSideAccount); loadAccountStatus();
    }

    @FXML private void logout(ActionEvent event) {
        loginController.loggedInUser = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblPageTitle.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Library Management System - Login");
        } catch (IOException e) { e.printStackTrace(); }
    }

    // ── Home Stats & Charts ─────────────────────────────────────

    private void loadHomeStats() {
        try (Connection conn = connection.getConnection()) {
            ResultSet rs;
            rs = conn.prepareStatement("SELECT COUNT(*) FROM books").executeQuery();
            if (rs.next()) lblStatTotalBooks.setText(String.valueOf(rs.getInt(1)));

            PreparedStatement p2 = conn.prepareStatement("SELECT COUNT(*) FROM issues WHERE user_id=? AND status='ISSUED'");
            p2.setInt(1, loginController.loggedInUser.getUserId()); rs = p2.executeQuery();
            int borrow = rs.next() ? rs.getInt(1) : 0;
            lblStatBorrowed.setText(String.valueOf(borrow));
            lblStatPending.setText(String.valueOf(borrow));

            PreparedStatement p3 = conn.prepareStatement("SELECT COUNT(*) FROM fines WHERE user_id=? AND status='UNPAID'");
            p3.setInt(1, loginController.loggedInUser.getUserId()); rs = p3.executeQuery();
            if (rs.next()) lblStatFines.setText(String.valueOf(rs.getInt(1)));
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
            chartMyStatus.setData(FXCollections.observableArrayList(
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
                chartLibraryOverview.getData().clear();
                chartLibraryOverview.getData().add(series);
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
            tblNotifications.setItems(list);
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
            tblBooks.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void searchBooks() {
        String q = txtSearch.getText().trim();
        ObservableList<BookModel> list = FXCollections.observableArrayList();
        try (Connection conn = connection.getConnection()) {
            PreparedStatement p = conn.prepareStatement("SELECT * FROM books WHERE title LIKE ? OR author LIKE ?");
            p.setString(1,"%" + q + "%"); p.setString(2,"%" + q + "%");
            ResultSet rs = p.executeQuery();
            while (rs.next())
                list.add(new BookModel(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
                        rs.getString("publisher"), rs.getInt("publication_year"),
                        rs.getInt("total_copies"), rs.getInt("available_copies")));
            tblBooks.setItems(list);
            lblSearchStatus.setText("Found " + list.size() + " result(s).");
            lblSearchStatus.setStyle("-fx-text-fill:#059669;");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void borrowSelectedBook() {
        BookModel sel = tblBooks.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus(lblSearchStatus,"Select a book first.",false); return; }
        if (sel.getAvailableCopies() <= 0) { setStatus(lblSearchStatus,"No copies available.",false); return; }
        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement p1 = conn.prepareStatement("INSERT INTO issues(book_id,user_id,issue_date,status) VALUES(?,?,CURDATE(),'ISSUED')");
            p1.setInt(1,sel.getBookId()); p1.setInt(2,loginController.loggedInUser.getUserId()); p1.executeUpdate();
            PreparedStatement p2 = conn.prepareStatement("UPDATE books SET available_copies=available_copies-1 WHERE book_id=?");
            p2.setInt(1,sel.getBookId()); p2.executeUpdate(); conn.commit();
            setStatus(lblSearchStatus,"Borrowed: " + sel.getTitle(),true); loadAllBooks();
        } catch (Exception e) { e.printStackTrace(); setStatus(lblSearchStatus,"Error borrowing book.",false); }
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
            tblIssues.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void returnSelectedBook() {
        IssueModel sel = tblIssues.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus(lblReturnStatus,"Select a book to return.",false); return; }
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
                setStatus(lblReturnStatus,"Returned. Fine applied: $"+String.format("%.2f",fine),true);
            } else {
                setStatus(lblReturnStatus,"Returned on time. Thank you!",true);
            }
            conn.commit(); loadBorrowedBooks();
        } catch (Exception e) { e.printStackTrace(); setStatus(lblReturnStatus,"Error returning book.",false); }
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
            tblFines.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void paySelectedFine() {
        FineModel sel = tblFines.getSelectionModel().getSelectedItem();
        if (sel == null || "PAID".equals(sel.getStatus())) { setStatus(lblFineStatus,"Select an unpaid fine.",false); return; }
        try (Connection conn = connection.getConnection()) {
            PreparedStatement p = conn.prepareStatement("UPDATE fines SET status='PAID' WHERE fine_id=?");
            p.setInt(1,sel.getFineId()); p.executeUpdate();
            setStatus(lblFineStatus,"Fine paid successfully!",true); loadFines();
        } catch (Exception e) { e.printStackTrace(); setStatus(lblFineStatus,"Error paying fine.",false); }
    }

    // ── Account Status ──────────────────────────────────────────

    private void loadAccountStatus() {
        if (loginController.loggedInUser == null) return;
        lblAccName.setText(loginController.loggedInUser.getName());
        lblAccEmail.setText(loginController.loggedInUser.getEmail());
        lblAccPhone.setText(loginController.loggedInUser.getPhone()!=null ? loginController.loggedInUser.getPhone() : "N/A");
        lblAccRole.setText(loginController.loggedInUser.getRole());

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
            tblAccIssues.setItems(issues);
        } catch (Exception e) { e.printStackTrace(); }

        ObservableList<FineModel> fines = FXCollections.observableArrayList();
        try (Connection conn = connection.getConnection()) {
            PreparedStatement p = conn.prepareStatement("SELECT * FROM fines WHERE user_id=? AND status='UNPAID'");
            p.setInt(1,loginController.loggedInUser.getUserId()); ResultSet rs = p.executeQuery();
            while (rs.next())
                fines.add(new FineModel(rs.getInt("fine_id"),rs.getInt("issue_id"),rs.getInt("user_id"),
                        rs.getDouble("amount"),rs.getString("status")));
            tblAccFines.setItems(fines);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Utility ─────────────────────────────────────────────────

    private void setStatus(Label lbl, String msg, boolean ok) {
        lbl.setText(msg);
        lbl.setStyle(ok ? "-fx-text-fill:#059669;" : "-fx-text-fill:#dc2626;");
    }
}
