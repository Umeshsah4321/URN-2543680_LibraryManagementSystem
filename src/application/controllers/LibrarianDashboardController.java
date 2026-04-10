package application.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import application.models.BookModel;
import application.models.ConnectionUtil;
import application.models.NotificationModel;
import application.models.UserModel;
import application.views.LoginView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LibrarianDashboardController {

    private Label lblPageTitle;

    // Sidebar buttons
    private Button btnSideHome;
    private Button btnSideIssue;
    private Button btnSideBooks;
    private Button btnSideMembers;
    private Button btnSideNotif;

    // Views
    private VBox viewHome;
    private VBox viewIssueBook;
    private VBox viewBookManage;
    private VBox viewMemberManage;
    private VBox viewNotifications;

    // Home Stats
    private Label lblStatTotalBooks;
    private Label lblStatActiveIssues;
    private Label lblStatMembers;
    private Label lblStatUnpaidFines;

    // Charts
    private PieChart chartBookAvailability;
    private BarChart<String, Number> chartMemberRoles;
    private BarChart<String, Number> chartTopBooks;
    private PieChart chartFineStatus;

    // Issue Book
    private TextField txtIssueStudentEmail;
    private Label lblIssueStudentName;
    private TableView<BookModel> tblIssueBooks;
    private Label lblIssueStatus;
    private int foundStudentUserId = -1;

    // Book Management
    private Label lblBookFormTitle;
    private Button btnBookSubmit;
    private TextField txtBookTitle, txtBookAuthor, txtBookPub, txtBookYear, txtBookCopies;
    private Label lblBookStatus;
    private TableView<BookModel> tblBooks;
    private int editingBookId = -1;

    // Member Management
    private TableView<UserModel> tblMembers;
    private Label lblMemberStatus;

    // Notifications
    private ComboBox<String> cmbNotifType;
    private TextField txtNotifMessage;
    private Label lblNotifStatus;
    private TableView<NotificationModel> tblNotifications;

    public void setFields(Label lblPageTitle, Button btnSideHome, Button btnSideIssue, Button btnSideBooks, Button btnSideMembers, Button btnSideNotif,
    		VBox viewHome, VBox viewIssueBook, VBox viewBookManage, VBox viewMemberManage, VBox viewNotifications,
    		Label lblStatTotalBooks, Label lblStatActiveIssues, Label lblStatMembers, Label lblStatUnpaidFines,
    		PieChart chartBookAvailability, BarChart<String, Number> chartMemberRoles, BarChart<String, Number> chartTopBooks, PieChart chartFineStatus,
    		TextField txtIssueStudentEmail, Label lblIssueStudentName, TableView<BookModel> tblIssueBooks, Label lblIssueStatus,
    		Label lblBookFormTitle, Button btnBookSubmit, TextField txtBookTitle, TextField txtBookAuthor, TextField txtBookPub, TextField txtBookYear, TextField txtBookCopies, Label lblBookStatus, TableView<BookModel> tblBooks,
    		TableView<UserModel> tblMembers, Label lblMemberStatus,
    		ComboBox<String> cmbNotifType, TextField txtNotifMessage, Label lblNotifStatus, TableView<NotificationModel> tblNotifications) {

    	this.lblPageTitle = lblPageTitle;
    	this.btnSideHome = btnSideHome;
    	this.btnSideIssue = btnSideIssue;
    	this.btnSideBooks = btnSideBooks;
    	this.btnSideMembers = btnSideMembers;
    	this.btnSideNotif = btnSideNotif;
    	this.viewHome = viewHome;
    	this.viewIssueBook = viewIssueBook;
    	this.viewBookManage = viewBookManage;
    	this.viewMemberManage = viewMemberManage;
    	this.viewNotifications = viewNotifications;
    	this.lblStatTotalBooks = lblStatTotalBooks;
    	this.lblStatActiveIssues = lblStatActiveIssues;
    	this.lblStatMembers = lblStatMembers;
    	this.lblStatUnpaidFines = lblStatUnpaidFines;
    	this.chartBookAvailability = chartBookAvailability;
    	this.chartMemberRoles = chartMemberRoles;
    	this.chartTopBooks = chartTopBooks;
    	this.chartFineStatus = chartFineStatus;
    	this.txtIssueStudentEmail = txtIssueStudentEmail;
    	this.lblIssueStudentName = lblIssueStudentName;
    	this.tblIssueBooks = tblIssueBooks;
    	this.lblIssueStatus = lblIssueStatus;
    	this.lblBookFormTitle = lblBookFormTitle;
    	this.btnBookSubmit = btnBookSubmit;
    	this.txtBookTitle = txtBookTitle;
    	this.txtBookAuthor = txtBookAuthor;
    	this.txtBookPub = txtBookPub;
    	this.txtBookYear = txtBookYear;
    	this.txtBookCopies = txtBookCopies;
    	this.lblBookStatus = lblBookStatus;
    	this.tblBooks = tblBooks;
    	this.tblMembers = tblMembers;
    	this.lblMemberStatus = lblMemberStatus;
    	this.cmbNotifType = cmbNotifType;
    	this.txtNotifMessage = txtNotifMessage;
    	this.lblNotifStatus = lblNotifStatus;
    	this.tblNotifications = tblNotifications;
    }

    public void initialize() {
        cmbNotifType.setItems(FXCollections.observableArrayList("GENERAL", "OVERDUE", "DUE_REMINDER"));
        cmbNotifType.setValue("GENERAL");
        hideAllViews();
        viewHome.setManaged(true); viewHome.setVisible(true);
        setActiveBtn(btnSideHome);
        loadHomeStats();
        loadCharts();
    }

    private void hideAllViews() {
        viewHome.setManaged(false);          viewHome.setVisible(false);
        viewIssueBook.setManaged(false);     viewIssueBook.setVisible(false);
        viewBookManage.setManaged(false);    viewBookManage.setVisible(false);
        viewMemberManage.setManaged(false);  viewMemberManage.setVisible(false);
        viewNotifications.setManaged(false); viewNotifications.setVisible(false);
    }

    private void setActiveBtn(Button active) {
        for (Button b : new Button[]{btnSideHome, btnSideIssue, btnSideBooks, btnSideMembers, btnSideNotif}) {
            b.getStyleClass().remove("sidebar-btn-active");
            if (!b.getStyleClass().contains("sidebar-btn")) {
				b.getStyleClass().add("sidebar-btn");
			}
        }
        active.getStyleClass().remove("sidebar-btn");
        if (!active.getStyleClass().contains("sidebar-btn-active")) {
			active.getStyleClass().add("sidebar-btn-active");
		}
    }

    // ── Navigation ──────────────────────────────────────────────

    public void showHome() {
        hideAllViews();
        lblPageTitle.setText("Dashboard");
        viewHome.setManaged(true); viewHome.setVisible(true);
        setActiveBtn(btnSideHome);
        loadHomeStats(); loadCharts();
    }

    public void showIssueBook() {
        hideAllViews();
        lblPageTitle.setText("Issue Book");
        viewIssueBook.setManaged(true); viewIssueBook.setVisible(true);
        setActiveBtn(btnSideIssue);
        foundStudentUserId = -1; lblIssueStudentName.setText("—"); lblIssueStatus.setText("");
        loadAllBooksForIssue();
    }

    public void showBookManage() {
        hideAllViews();
        lblPageTitle.setText("Manage Books");
        viewBookManage.setManaged(true); viewBookManage.setVisible(true);
        setActiveBtn(btnSideBooks);
        clearBookForm(); loadBooks();
    }

    public void showMemberManage() {
        hideAllViews();
        lblPageTitle.setText("Manage Members");
        viewMemberManage.setManaged(true); viewMemberManage.setVisible(true);
        setActiveBtn(btnSideMembers);
        lblMemberStatus.setText(""); loadMembers();
    }

    public void showNotifications() {
        hideAllViews();
        lblPageTitle.setText("Notifications");
        viewNotifications.setManaged(true); viewNotifications.setVisible(true);
        setActiveBtn(btnSideNotif);
        lblNotifStatus.setText(""); loadNotifications();
    }

    public void logout(ActionEvent event) {
        LoginController.loggedInUser = null;
        Stage stage = (Stage) lblPageTitle.getScene().getWindow();
        stage.setScene(new Scene(new LoginView()));
        stage.setMaximized(true);
        stage.setTitle("Library Management System - Login");
    }

    // ── Home Stats ──────────────────────────────────────────────

    private void loadHomeStats() {
        try (Connection conn = ConnectionUtil.getConnection()) {
            ResultSet rs;
            rs = conn.prepareStatement("SELECT COUNT(*) FROM books").executeQuery();
            if (rs.next()) {
				lblStatTotalBooks.setText(String.valueOf(rs.getInt(1)));
			}

            rs = conn.prepareStatement("SELECT COUNT(*) FROM issues WHERE status='ISSUED'").executeQuery();
            if (rs.next()) {
				lblStatActiveIssues.setText(String.valueOf(rs.getInt(1)));
			}

            rs = conn.prepareStatement("SELECT COUNT(*) FROM users").executeQuery();
            if (rs.next()) {
				lblStatMembers.setText(String.valueOf(rs.getInt(1)));
			}

            rs = conn.prepareStatement("SELECT COUNT(*) FROM fines WHERE status='UNPAID'").executeQuery();
            if (rs.next()) {
				lblStatUnpaidFines.setText(String.valueOf(rs.getInt(1)));
			}
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadCharts() {
        try (Connection conn = ConnectionUtil.getConnection()) {
            ResultSet rs;

            // ── Chart 1: Book Availability (PieChart) ──
            rs = conn.prepareStatement("SELECT SUM(available_copies), SUM(total_copies - available_copies) FROM books").executeQuery();
            if (rs.next()) {
                int avail = rs.getInt(1), issued = rs.getInt(2);
                chartBookAvailability.setData(FXCollections.observableArrayList(
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
            chartMemberRoles.getData().clear();
            chartMemberRoles.getData().add(memberSeries);

            // ── Chart 3: Top 5 Borrowed Books (BarChart) ──
            rs = conn.prepareStatement(
                "SELECT b.title, COUNT(i.issue_id) AS cnt FROM issues i " +
                "JOIN books b ON i.book_id=b.book_id GROUP BY i.book_id ORDER BY cnt DESC LIMIT 5").executeQuery();
            XYChart.Series<String, Number> topSeries = new XYChart.Series<>();
            while (rs.next()) {
                String title = rs.getString("title");
                if (title.length() > 20) {
					title = title.substring(0, 18) + "…";
				}
                topSeries.getData().add(new XYChart.Data<>(title, rs.getInt("cnt")));
            }
            chartTopBooks.getData().clear();
            chartTopBooks.getData().add(topSeries);

            // ── Chart 4: Fine Status (PieChart) ──
            rs = conn.prepareStatement("SELECT COUNT(*) FROM fines WHERE status='PAID'").executeQuery();
            int paid = rs.next() ? rs.getInt(1) : 0;
            rs = conn.prepareStatement("SELECT COUNT(*) FROM fines WHERE status='UNPAID'").executeQuery();
            int unpaid = rs.next() ? rs.getInt(1) : 0;
            chartFineStatus.setData(FXCollections.observableArrayList(
                new PieChart.Data("Paid (" + paid + ")", paid == 0 ? 0.001 : paid),
                new PieChart.Data("Unpaid (" + unpaid + ")", unpaid == 0 ? 0.001 : unpaid)
            ));

        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Issue Book ──────────────────────────────────────────────

    private void loadAllBooksForIssue() {
        ObservableList<BookModel> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionUtil.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT * FROM books WHERE available_copies > 0").executeQuery();
            while (rs.next()) {
				list.add(new BookModel(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
                        rs.getString("publisher"), rs.getInt("publication_year"),
                        rs.getInt("total_copies"), rs.getInt("available_copies")));
			}
            tblIssueBooks.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void findStudentForIssue() {
        String email = txtIssueStudentEmail.getText().trim();
        if (email.isEmpty()) { setStatus(lblIssueStatus, "Please enter a student email.", false); return; }
        try (Connection conn = ConnectionUtil.getConnection()) {
            PreparedStatement pst = conn.prepareStatement(
                "SELECT u.user_id, u.name FROM users u INNER JOIN students s ON u.user_id=s.user_id WHERE u.email=?");
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                foundStudentUserId = rs.getInt("user_id");
                lblIssueStudentName.setText(rs.getString("name") + " (ID: " + foundStudentUserId + ")");
                setStatus(lblIssueStatus, "Student found! Select a book and click Issue.", true);
            } else {
                foundStudentUserId = -1;
                lblIssueStudentName.setText("Not found");
                setStatus(lblIssueStatus, "No student with this email.", false);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void issueBook() {
        if (foundStudentUserId == -1) { setStatus(lblIssueStatus, "Find a valid student first.", false); return; }
        BookModel sel = tblIssueBooks.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus(lblIssueStatus, "Please select a book.", false); return; }
        try (Connection conn = ConnectionUtil.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement p1 = conn.prepareStatement("INSERT INTO issues(book_id,user_id,issue_date,status) VALUES(?,?,CURDATE(),'ISSUED')");
            p1.setInt(1, sel.getBookId()); p1.setInt(2, foundStudentUserId); p1.executeUpdate();
            PreparedStatement p2 = conn.prepareStatement("UPDATE books SET available_copies=available_copies-1 WHERE book_id=?");
            p2.setInt(1, sel.getBookId()); p2.executeUpdate();
            conn.commit();
            setStatus(lblIssueStatus, "'" + sel.getTitle() + "' issued successfully!", true);
            foundStudentUserId = -1; lblIssueStudentName.setText("—"); txtIssueStudentEmail.clear();
            loadAllBooksForIssue();
        } catch (Exception e) { e.printStackTrace(); setStatus(lblIssueStatus, "Error issuing book.", false); }
    }

    // ── Book Management ─────────────────────────────────────────

    private void loadBooks() {
        ObservableList<BookModel> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionUtil.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT * FROM books").executeQuery();
            while (rs.next()) {
				list.add(new BookModel(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
                        rs.getString("publisher"), rs.getInt("publication_year"),
                        rs.getInt("total_copies"), rs.getInt("available_copies")));
			}
            tblBooks.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void addOrUpdateBook() {
        String title = txtBookTitle.getText().trim(), author = txtBookAuthor.getText().trim();
        String pub = txtBookPub.getText().trim(), yearStr = txtBookYear.getText().trim(), copStr = txtBookCopies.getText().trim();
        if (title.isEmpty() || author.isEmpty() || yearStr.isEmpty() || copStr.isEmpty()) {
            setStatus(lblBookStatus, "Fill in all required fields (*).", false); return;
        }
        try (Connection conn = ConnectionUtil.getConnection()) {
            int year = Integer.parseInt(yearStr), copies = Integer.parseInt(copStr);
            if (editingBookId == -1) {
                PreparedStatement p = conn.prepareStatement(
                    "INSERT INTO books(title,author,publisher,publication_year,total_copies,available_copies) VALUES(?,?,?,?,?,?)");
                p.setString(1,title); p.setString(2,author); p.setString(3,pub);
                p.setInt(4,year); p.setInt(5,copies); p.setInt(6,copies); p.executeUpdate();
                setStatus(lblBookStatus, "Book added successfully!", true);
            } else {
                PreparedStatement p = conn.prepareStatement(
                    "UPDATE books SET title=?,author=?,publisher=?,publication_year=?,total_copies=? WHERE book_id=?");
                p.setString(1,title); p.setString(2,author); p.setString(3,pub);
                p.setInt(4,year); p.setInt(5,copies); p.setInt(6,editingBookId); p.executeUpdate();
                setStatus(lblBookStatus, "Book updated!", true);
            }
            clearBookForm(); loadBooks();
        } catch (NumberFormatException e) { setStatus(lblBookStatus, "Year and Copies must be numbers.", false);
        } catch (Exception e) { e.printStackTrace(); setStatus(lblBookStatus, "Error saving book.", false); }
    }

    public void editSelectedBook() {
        BookModel sel = tblBooks.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus(lblBookStatus, "Select a book to edit.", false); return; }
        editingBookId = sel.getBookId();
        txtBookTitle.setText(sel.getTitle()); txtBookAuthor.setText(sel.getAuthor());
        txtBookPub.setText(sel.getPublisher()); txtBookYear.setText(String.valueOf(sel.getPublicationYear()));
        txtBookCopies.setText(String.valueOf(sel.getTotalCopies()));
        lblBookFormTitle.setText("EDIT BOOK (ID: " + editingBookId + ")");
        btnBookSubmit.setText("💾 Update Book");
        setStatus(lblBookStatus, "Editing book — modify fields and click Update.", true);
    }

    public void clearBookForm() {
        editingBookId = -1;
        txtBookTitle.clear(); txtBookAuthor.clear(); txtBookPub.clear(); txtBookYear.clear(); txtBookCopies.clear();
        lblBookFormTitle.setText("ADD NEW BOOK"); btnBookSubmit.setText("➕ Add Book"); lblBookStatus.setText("");
    }

    public void deleteBook() {
        BookModel sel = tblBooks.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus(lblBookStatus, "Select a book to delete.", false); return; }
        try (Connection conn = ConnectionUtil.getConnection()) {
            PreparedStatement p = conn.prepareStatement("DELETE FROM books WHERE book_id=?");
            p.setInt(1, sel.getBookId()); p.executeUpdate();
            setStatus(lblBookStatus, "Deleted: " + sel.getTitle(), true); loadBooks();
        } catch (Exception e) { e.printStackTrace(); setStatus(lblBookStatus, "Cannot delete (may be issued).", false); }
    }

    // ── Member Management ───────────────────────────────────────

    private void loadMembers() {
        ObservableList<UserModel> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionUtil.getConnection()) {
            ResultSet rs = conn.prepareStatement(
                "SELECT u.user_id,u.name,u.email,u.phone,u.address,u.role,COALESCE(s.student_id,l.librarian_id) spec_id " +
                "FROM users u LEFT JOIN students s ON u.user_id=s.user_id LEFT JOIN librarians l ON u.user_id=l.user_id").executeQuery();
            while (rs.next()) {
                UserModel um = new UserModel(rs.getInt("user_id"), rs.getString("name"), rs.getString("email"),
                        "", rs.getString("phone"), rs.getString("address"), rs.getString("role"));
                um.setSpecId(rs.getInt("spec_id")); list.add(um);
            }
            tblMembers.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void deleteMember() {
        UserModel sel = tblMembers.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus(lblMemberStatus, "Select a member to delete.", false); return; }
        if (LoginController.loggedInUser != null && sel.getUserId() == LoginController.loggedInUser.getUserId()) {
            setStatus(lblMemberStatus, "You cannot delete your own account.", false); return;
        }
        try (Connection conn = ConnectionUtil.getConnection()) {
            PreparedStatement p = conn.prepareStatement("DELETE FROM users WHERE user_id=?");
            p.setInt(1, sel.getUserId()); p.executeUpdate();
            setStatus(lblMemberStatus, "Member deleted.", true); loadMembers();
        } catch (Exception e) { e.printStackTrace(); setStatus(lblMemberStatus, "Error deleting member.", false); }
    }

    // ── Notifications ───────────────────────────────────────────

    private void loadNotifications() {
        ObservableList<NotificationModel> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionUtil.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT * FROM notifications ORDER BY created_at DESC").executeQuery();
            while (rs.next()) {
				list.add(new NotificationModel(rs.getInt("notification_id"), rs.getString("message"),
                        rs.getString("type"), rs.getString("created_at")));
			}
            tblNotifications.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void sendNotification() {
        String msg = txtNotifMessage.getText().trim(), type = cmbNotifType.getValue();
        if (msg.isEmpty()) { setStatus(lblNotifStatus, "Enter a message.", false); return; }
        try (Connection conn = ConnectionUtil.getConnection()) {
            PreparedStatement p = conn.prepareStatement("INSERT INTO notifications(message,type) VALUES(?,?)");
            p.setString(1, msg); p.setString(2, type); p.executeUpdate();
            txtNotifMessage.clear();
            setStatus(lblNotifStatus, "Notification sent!", true); loadNotifications();
        } catch (Exception e) { e.printStackTrace(); setStatus(lblNotifStatus, "Error sending.", false); }
    }

    // ── Utility ─────────────────────────────────────────────────

    private void setStatus(Label lbl, String msg, boolean ok) {
        lbl.setText(msg);
        lbl.setStyle(ok ? "-fx-text-fill:#059669;" : "-fx-text-fill:#dc2626;");
    }
}
