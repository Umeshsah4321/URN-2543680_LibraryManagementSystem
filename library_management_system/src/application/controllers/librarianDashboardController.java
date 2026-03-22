package application.controllers;

import application.connection;
import application.models.BookModel;
import application.models.NotificationModel;
import application.models.UserModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import application.views.LibrarianDashboardView;
import application.views.LoginView;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class librarianDashboardController {

    private LibrarianDashboardView view;
    private int foundStudentUserId = -1;
    private int editingBookId = -1;

    public librarianDashboardController(LibrarianDashboardView view) {
        this.view = view;
        initHandlers();
        initialize();
    }

    private void initHandlers() {
        view.getBtnSideHome().setOnAction(e -> showHome());
        view.getBtnSideIssue().setOnAction(e -> showIssueBook());
        view.getBtnSideBooks().setOnAction(e -> showBookManage());
        view.getBtnSideMembers().setOnAction(e -> showMemberManage());
        view.getBtnSideNotif().setOnAction(e -> showNotifications());
        view.getBtnLogout().setOnAction(this::logout);

        view.getBtnFindStudent().setOnAction(e -> findStudentForIssue());
        view.getBtnIssueBook().setOnAction(e -> issueBook());
        view.getBtnBookSubmit().setOnAction(e -> addOrUpdateBook());
        view.getBtnEditBook().setOnAction(e -> editSelectedBook());
        view.getBtnClearBookForm().setOnAction(e -> clearBookForm());
        view.getBtnDeleteBook().setOnAction(e -> deleteBook());
        view.getBtnDeleteMember().setOnAction(e -> deleteMember());
        view.getBtnSendNotif().setOnAction(e -> sendNotification());
    }

    private void initialize() {
        view.getCmbNotifType().setItems(FXCollections.observableArrayList("GENERAL", "OVERDUE", "DUE_REMINDER"));
        view.getCmbNotifType().setValue("GENERAL");
        hideAllViews();
        view.getViewHome().setManaged(true); view.getViewHome().setVisible(true);
        setActiveBtn(view.getBtnSideHome());
        loadHomeStats();
        loadCharts();
    }

    private void hideAllViews() {
        view.getViewHome().setManaged(false);          view.getViewHome().setVisible(false);
        view.getViewIssueBook().setManaged(false);     view.getViewIssueBook().setVisible(false);
        view.getViewBookManage().setManaged(false);    view.getViewBookManage().setVisible(false);
        view.getViewMemberManage().setManaged(false);  view.getViewMemberManage().setVisible(false);
        view.getViewNotifications().setManaged(false); view.getViewNotifications().setVisible(false);
    }

    private void setActiveBtn(Button active) {
        for (Button b : new Button[]{view.getBtnSideHome(), view.getBtnSideIssue(), view.getBtnSideBooks(), view.getBtnSideMembers(), view.getBtnSideNotif()}) {
            b.getStyleClass().remove("sidebar-btn-active");
            if (!b.getStyleClass().contains("sidebar-btn")) b.getStyleClass().add("sidebar-btn");
        }
        active.getStyleClass().remove("sidebar-btn");
        if (!active.getStyleClass().contains("sidebar-btn-active")) active.getStyleClass().add("sidebar-btn-active");
    }

    // ── Navigation ──────────────────────────────────────────────

    private void showHome() {
        hideAllViews();
        view.getLblPageTitle().setText("Dashboard");
        view.getViewHome().setManaged(true); view.getViewHome().setVisible(true);
        setActiveBtn(view.getBtnSideHome());
        loadHomeStats(); loadCharts();
    }

    private void showIssueBook() {
        hideAllViews();
        view.getLblPageTitle().setText("Issue Book");
        view.getViewIssueBook().setManaged(true); view.getViewIssueBook().setVisible(true);
        setActiveBtn(view.getBtnSideIssue());
        foundStudentUserId = -1; view.getLblIssueStudentName().setText("—"); view.getLblIssueStatus().setText("");
        loadAllBooksForIssue();
    }

    private void showBookManage() {
        hideAllViews();
        view.getLblPageTitle().setText("Manage Books");
        view.getViewBookManage().setManaged(true); view.getViewBookManage().setVisible(true);
        setActiveBtn(view.getBtnSideBooks());
        clearBookForm(); loadBooks();
    }

    private void showMemberManage() {
        hideAllViews();
        view.getLblPageTitle().setText("Manage Members");
        view.getViewMemberManage().setManaged(true); view.getViewMemberManage().setVisible(true);
        setActiveBtn(view.getBtnSideMembers());
        view.getLblMemberStatus().setText(""); loadMembers();
    }

    private void showNotifications() {
        hideAllViews();
        view.getLblPageTitle().setText("Notifications");
        view.getViewNotifications().setManaged(true); view.getViewNotifications().setVisible(true);
        setActiveBtn(view.getBtnSideNotif());
        view.getLblNotifStatus().setText(""); loadNotifications();
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

    // ── Home Stats ──────────────────────────────────────────────

    private void loadHomeStats() {
        try (Connection conn = connection.getConnection()) {
            ResultSet rs;
            rs = conn.prepareStatement("SELECT COUNT(*) FROM books").executeQuery();
            if (rs.next()) view.getLblStatTotalBooks().setText(String.valueOf(rs.getInt(1)));

            rs = conn.prepareStatement("SELECT COUNT(*) FROM issues WHERE status='ISSUED'").executeQuery();
            if (rs.next()) view.getLblStatActiveIssues().setText(String.valueOf(rs.getInt(1)));

            rs = conn.prepareStatement("SELECT COUNT(*) FROM users").executeQuery();
            if (rs.next()) view.getLblStatMembers().setText(String.valueOf(rs.getInt(1)));

            rs = conn.prepareStatement("SELECT COUNT(*) FROM fines WHERE status='UNPAID'").executeQuery();
            if (rs.next()) view.getLblStatUnpaidFines().setText(String.valueOf(rs.getInt(1)));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadCharts() {
        try (Connection conn = connection.getConnection()) {
            ResultSet rs;

            // ── Chart 1: Book Availability (PieChart) ──
            rs = conn.prepareStatement("SELECT SUM(available_copies), SUM(total_copies - available_copies) FROM books").executeQuery();
            if (rs.next()) {
                int avail = rs.getInt(1), issued = rs.getInt(2);
                view.getChartBookAvailability().setData(FXCollections.observableArrayList(
                    new PieChart.Data("Available (" + avail + ")", avail),
                    new PieChart.Data("Issued (" + issued + ")", issued)
                ));
            }

            // ── Chart 2: Member Roles (BarChart) ──
            rs = conn.prepareStatement("SELECT COUNT(*) FROM students").executeQuery();
            int students = rs.next() ? rs.getInt(1) : 0;
            rs = conn.prepareStatement("SELECT COUNT(*) FROM librarians").executeQuery();
            int librarians = rs.next() ? rs.getInt(1) : 0;
            XYChart.Series<String, Number> memberSeries = new XYChart.Series<>();
            memberSeries.getData().add(new XYChart.Data<>("Students", students));
            memberSeries.getData().add(new XYChart.Data<>("Librarians", librarians));
            view.getChartMemberRoles().getData().clear();
            view.getChartMemberRoles().getData().add(memberSeries);

            // ── Chart 3: Top 5 Borrowed Books (BarChart) ──
            rs = conn.prepareStatement(
                "SELECT b.title, COUNT(i.issue_id) AS cnt FROM issues i " +
                "JOIN books b ON i.book_id=b.book_id GROUP BY i.book_id ORDER BY cnt DESC LIMIT 5").executeQuery();
            XYChart.Series<String, Number> topSeries = new XYChart.Series<>();
            while (rs.next()) {
                String title = rs.getString("title");
                if (title.length() > 20) title = title.substring(0, 18) + "…";
                topSeries.getData().add(new XYChart.Data<>(title, rs.getInt("cnt")));
            }
            view.getChartTopBooks().getData().clear();
            view.getChartTopBooks().getData().add(topSeries);

            // ── Chart 4: Fine Status (PieChart) ──
            rs = conn.prepareStatement("SELECT COUNT(*) FROM fines WHERE status='PAID'").executeQuery();
            int paid = rs.next() ? rs.getInt(1) : 0;
            rs = conn.prepareStatement("SELECT COUNT(*) FROM fines WHERE status='UNPAID'").executeQuery();
            int unpaid = rs.next() ? rs.getInt(1) : 0;
            view.getChartFineStatus().setData(FXCollections.observableArrayList(
                new PieChart.Data("Paid (" + paid + ")", paid == 0 ? 0.001 : paid),
                new PieChart.Data("Unpaid (" + unpaid + ")", unpaid == 0 ? 0.001 : unpaid)
            ));

        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Issue Book ──────────────────────────────────────────────

    private void loadAllBooksForIssue() {
        ObservableList<BookModel> list = FXCollections.observableArrayList();
        try (Connection conn = connection.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT * FROM books WHERE available_copies > 0").executeQuery();
            while (rs.next())
                list.add(new BookModel(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
                        rs.getString("publisher"), rs.getInt("publication_year"),
                        rs.getInt("total_copies"), rs.getInt("available_copies")));
            view.getTblIssueBooks().setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void findStudentForIssue() {
        String email = view.getTxtIssueStudentEmail().getText().trim();
        if (email.isEmpty()) { setStatus(view.getLblIssueStatus(), "Please enter a student email.", false); return; }
        try (Connection conn = connection.getConnection()) {
            PreparedStatement pst = conn.prepareStatement(
                "SELECT u.user_id, u.name FROM users u INNER JOIN students s ON u.user_id=s.user_id WHERE u.email=?");
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                foundStudentUserId = rs.getInt("user_id");
                view.getLblIssueStudentName().setText(rs.getString("name") + " (ID: " + foundStudentUserId + ")");
                setStatus(view.getLblIssueStatus(), "Student found! Select a book and click Issue.", true);
            } else {
                foundStudentUserId = -1;
                view.getLblIssueStudentName().setText("Not found");
                setStatus(view.getLblIssueStatus(), "No student with this email.", false);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void issueBook() {
        if (foundStudentUserId == -1) { setStatus(view.getLblIssueStatus(), "Find a valid student first.", false); return; }
        BookModel sel = view.getTblIssueBooks().getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus(view.getLblIssueStatus(), "Please select a book.", false); return; }
        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement p1 = conn.prepareStatement("INSERT INTO issues(book_id,user_id,issue_date,status) VALUES(?,?,CURDATE(),'ISSUED')");
            p1.setInt(1, sel.getBookId()); p1.setInt(2, foundStudentUserId); p1.executeUpdate();
            PreparedStatement p2 = conn.prepareStatement("UPDATE books SET available_copies=available_copies-1 WHERE book_id=?");
            p2.setInt(1, sel.getBookId()); p2.executeUpdate();
            conn.commit();
            setStatus(view.getLblIssueStatus(), "'" + sel.getTitle() + "' issued successfully!", true);
            foundStudentUserId = -1; view.getLblIssueStudentName().setText("—"); view.getTxtIssueStudentEmail().clear();
            loadAllBooksForIssue();
        } catch (Exception e) { e.printStackTrace(); setStatus(view.getLblIssueStatus(), "Error issuing book.", false); }
    }

    // ── Book Management ─────────────────────────────────────────

    private void loadBooks() {
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

    private void addOrUpdateBook() {
        String title = view.getTxtBookTitle().getText().trim(), author = view.getTxtBookAuthor().getText().trim();
        String pub = view.getTxtBookPub().getText().trim(), yearStr = view.getTxtBookYear().getText().trim(), copStr = view.getTxtBookCopies().getText().trim();
        if (title.isEmpty() || author.isEmpty() || yearStr.isEmpty() || copStr.isEmpty()) {
            setStatus(view.getLblBookStatus(), "Fill in all required fields (*).", false); return;
        }
        try (Connection conn = connection.getConnection()) {
            int year = Integer.parseInt(yearStr), copies = Integer.parseInt(copStr);
            if (editingBookId == -1) {
                PreparedStatement p = conn.prepareStatement(
                    "INSERT INTO books(title,author,publisher,publication_year,total_copies,available_copies) VALUES(?,?,?,?,?,?)");
                p.setString(1,title); p.setString(2,author); p.setString(3,pub);
                p.setInt(4,year); p.setInt(5,copies); p.setInt(6,copies); p.executeUpdate();
                setStatus(view.getLblBookStatus(), "Book added successfully!", true);
            } else {
                PreparedStatement p = conn.prepareStatement(
                    "UPDATE books SET title=?,author=?,publisher=?,publication_year=?,total_copies=? WHERE book_id=?");
                p.setString(1,title); p.setString(2,author); p.setString(3,pub);
                p.setInt(4,year); p.setInt(5,copies); p.setInt(6,editingBookId); p.executeUpdate();
                setStatus(view.getLblBookStatus(), "Book updated!", true);
            }
            clearBookForm(); loadBooks();
        } catch (NumberFormatException e) { setStatus(view.getLblBookStatus(), "Year and Copies must be numbers.", false);
        } catch (Exception e) { e.printStackTrace(); setStatus(view.getLblBookStatus(), "Error saving book.", false); }
    }

    private void editSelectedBook() {
        BookModel sel = view.getTblBooks().getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus(view.getLblBookStatus(), "Select a book to edit.", false); return; }
        editingBookId = sel.getBookId();
        view.getTxtBookTitle().setText(sel.getTitle()); view.getTxtBookAuthor().setText(sel.getAuthor());
        view.getTxtBookPub().setText(sel.getPublisher()); view.getTxtBookYear().setText(String.valueOf(sel.getPublicationYear()));
        view.getTxtBookCopies().setText(String.valueOf(sel.getTotalCopies()));
        view.getLblBookFormTitle().setText("EDIT BOOK (ID: " + editingBookId + ")");
        view.getBtnBookSubmit().setText("💾 Update Book");
        setStatus(view.getLblBookStatus(), "Editing book — modify fields and click Update.", true);
    }

    private void clearBookForm() {
        editingBookId = -1;
        view.getTxtBookTitle().clear(); view.getTxtBookAuthor().clear(); view.getTxtBookPub().clear(); view.getTxtBookYear().clear(); view.getTxtBookCopies().clear();
        view.getLblBookFormTitle().setText("ADD NEW BOOK"); view.getBtnBookSubmit().setText("➕ Add Book"); view.getLblBookStatus().setText("");
    }

    private void deleteBook() {
        BookModel sel = view.getTblBooks().getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus(view.getLblBookStatus(), "Select a book to delete.", false); return; }
        try (Connection conn = connection.getConnection()) {
            PreparedStatement p = conn.prepareStatement("DELETE FROM books WHERE book_id=?");
            p.setInt(1, sel.getBookId()); p.executeUpdate();
            setStatus(view.getLblBookStatus(), "Deleted: " + sel.getTitle(), true); loadBooks();
        } catch (Exception e) { e.printStackTrace(); setStatus(view.getLblBookStatus(), "Cannot delete (may be issued).", false); }
    }

    // ── Member Management ───────────────────────────────────────

    private void loadMembers() {
        ObservableList<UserModel> list = FXCollections.observableArrayList();
        try (Connection conn = connection.getConnection()) {
            ResultSet rs = conn.prepareStatement(
                "SELECT u.user_id,u.name,u.email,u.phone,u.address,u.role,COALESCE(s.student_id,l.librarian_id) spec_id " +
                "FROM users u LEFT JOIN students s ON u.user_id=s.user_id LEFT JOIN librarians l ON u.user_id=l.user_id").executeQuery();
            while (rs.next()) {
                UserModel um = new UserModel(rs.getInt("user_id"), rs.getString("name"), rs.getString("email"),
                        "", rs.getString("phone"), rs.getString("address"), rs.getString("role"));
                um.setSpecId(rs.getInt("spec_id")); list.add(um);
            }
            view.getTblMembers().setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void deleteMember() {
        UserModel sel = view.getTblMembers().getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus(view.getLblMemberStatus(), "Select a member to delete.", false); return; }
        if (loginController.loggedInUser != null && sel.getUserId() == loginController.loggedInUser.getUserId()) {
            setStatus(view.getLblMemberStatus(), "You cannot delete your own account.", false); return;
        }
        try (Connection conn = connection.getConnection()) {
            PreparedStatement p = conn.prepareStatement("DELETE FROM users WHERE user_id=?");
            p.setInt(1, sel.getUserId()); p.executeUpdate();
            setStatus(view.getLblMemberStatus(), "Member deleted.", true); loadMembers();
        } catch (Exception e) { e.printStackTrace(); setStatus(view.getLblMemberStatus(), "Error deleting member.", false); }
    }

    // ── Notifications ───────────────────────────────────────────

    private void loadNotifications() {
        ObservableList<NotificationModel> list = FXCollections.observableArrayList();
        try (Connection conn = connection.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT * FROM notifications ORDER BY created_at DESC").executeQuery();
            while (rs.next())
                list.add(new NotificationModel(rs.getInt("notification_id"), rs.getString("message"),
                        rs.getString("type"), rs.getString("created_at")));
            view.getTblNotifications().setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void sendNotification() {
        String msg = view.getTxtNotifMessage().getText().trim(), type = view.getCmbNotifType().getValue();
        if (msg.isEmpty()) { setStatus(view.getLblNotifStatus(), "Enter a message.", false); return; }
        try (Connection conn = connection.getConnection()) {
            PreparedStatement p = conn.prepareStatement("INSERT INTO notifications(message,type) VALUES(?,?)");
            p.setString(1, msg); p.setString(2, type); p.executeUpdate();
            view.getTxtNotifMessage().clear();
            setStatus(view.getLblNotifStatus(), "Notification sent!", true); loadNotifications();
        } catch (Exception e) { e.printStackTrace(); setStatus(view.getLblNotifStatus(), "Error sending.", false); }
    }

    // ── Utility ─────────────────────────────────────────────────

    private void setStatus(Label lbl, String msg, boolean ok) {
        lbl.setText(msg);
        lbl.setStyle(ok ? "-fx-text-fill:#059669;" : "-fx-text-fill:#dc2626;");
    }
}
