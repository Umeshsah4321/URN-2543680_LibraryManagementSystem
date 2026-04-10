package application.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import application.models.BookModel;
import application.models.ConnectionUtil;
import application.models.FineModel;
import application.models.IssueModel;
import application.models.NotificationModel;
import application.views.LoginView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentDashboardController {

    private Label lblPageTitle;

    // Sidebar buttons
    private Button btnSideHome, btnSideSearch, btnSideMyBooks, btnSideFines, btnSideAccount;

    // Views
    private VBox viewHome, viewSearchBooks, viewMyBooks, viewFines, viewAccount;

    // Home Stats
    private Label lblStatTotalBooks, lblStatBorrowed, lblStatPending, lblStatFines;
    private TableView<NotificationModel> tblLatestNotifications;

    // Charts
    private PieChart chartBorrowStatus;
    private BarChart<String, Number> chartLibraryOverview;

    // Search Books
    private TextField txtSearchBook;
    private TableView<BookModel> tblSearchBooks;

    // My Books
    private TableView<IssueModel> tblMyBooks;

    // Fines
    private TableView<FineModel> tblFines;
    private Label lblFineStatus;

    // Account
    private Label lblAccName, lblAccEmail, lblAccPhone, lblAccAddress, lblAccRole, lblAccJoined;
    private TableView<NotificationModel> tblAccNotifications;

    public void setFields(Label lblPageTitle, Button btnSideHome, Button btnSideSearch, Button btnSideMyBooks, Button btnSideFines, Button btnSideAccount,
    		VBox viewHome, VBox viewSearchBooks, VBox viewMyBooks, VBox viewFines, VBox viewAccount,
    		Label lblStatTotalBooks, Label lblStatBorrowed, Label lblStatPending, Label lblStatFines,
    		PieChart chartBorrowStatus, BarChart<String, Number> chartLibraryOverview, TableView<NotificationModel> tblLatestNotifications,
    		TextField txtSearchBook, TableView<BookModel> tblSearchBooks,
    		TableView<IssueModel> tblMyBooks,
    		TableView<FineModel> tblFines, Label lblFineStatus,
    		Label lblAccName, Label lblAccEmail, Label lblAccPhone, Label lblAccAddress, Label lblAccRole, Label lblAccJoined, TableView<NotificationModel> tblAccNotifications) {

    	this.lblPageTitle = lblPageTitle;
    	this.btnSideHome = btnSideHome;
    	this.btnSideSearch = btnSideSearch;
    	this.btnSideMyBooks = btnSideMyBooks;
    	this.btnSideFines = btnSideFines;
    	this.btnSideAccount = btnSideAccount;
    	this.viewHome = viewHome;
    	this.viewSearchBooks = viewSearchBooks;
    	this.viewMyBooks = viewMyBooks;
    	this.viewFines = viewFines;
    	this.viewAccount = viewAccount;
    	this.lblStatTotalBooks = lblStatTotalBooks;
    	this.lblStatBorrowed = lblStatBorrowed;
    	this.lblStatPending = lblStatPending;
    	this.lblStatFines = lblStatFines;
    	this.chartBorrowStatus = chartBorrowStatus;
    	this.chartLibraryOverview = chartLibraryOverview;
        this.tblLatestNotifications = tblLatestNotifications;
    	this.txtSearchBook = txtSearchBook;
    	this.tblSearchBooks = tblSearchBooks;
    	this.tblMyBooks = tblMyBooks;
    	this.tblFines = tblFines;
    	this.lblFineStatus = lblFineStatus;
    	this.lblAccName = lblAccName;
    	this.lblAccEmail = lblAccEmail;
    	this.lblAccPhone = lblAccPhone;
    	this.lblAccAddress = lblAccAddress;
    	this.lblAccRole = lblAccRole;
    	this.lblAccJoined = lblAccJoined;
    	this.tblAccNotifications = tblAccNotifications;
    }

    public void initialize() {
        hideAllViews();
        viewHome.setManaged(true); viewHome.setVisible(true);
        setActiveBtn(btnSideHome);
        loadHomeStats();
        loadCharts();
        loadNotifications();
    }

    private void hideAllViews() {
        viewHome.setManaged(false);        viewHome.setVisible(false);
        viewSearchBooks.setManaged(false); viewSearchBooks.setVisible(false);
        viewMyBooks.setManaged(false);     viewMyBooks.setVisible(false);
        viewFines.setManaged(false);       viewFines.setVisible(false);
        viewAccount.setManaged(false);     viewAccount.setVisible(false);
    }

    private void setActiveBtn(Button active) {
        for (Button b : new Button[]{btnSideHome, btnSideSearch, btnSideMyBooks, btnSideFines, btnSideAccount}) {
            b.getStyleClass().remove("sidebar-btn-active");
            if (b.getStyleClass().isEmpty() || !b.getStyleClass().contains("sidebar-btn")) {
				b.getStyleClass().add("sidebar-btn");
			}
        }
        active.getStyleClass().remove("sidebar-btn");
        if (!active.getStyleClass().contains("sidebar-btn-active")) {
			active.getStyleClass().add("sidebar-btn-active");
		}
    }

    public void showHome() {
        hideAllViews(); lblPageTitle.setText("Home");
        viewHome.setManaged(true); viewHome.setVisible(true);
        setActiveBtn(btnSideHome);
        loadHomeStats(); loadCharts(); loadNotifications();
    }

    public void showSearchBooks() {
        hideAllViews(); lblPageTitle.setText("Search Books");
        viewSearchBooks.setManaged(true); viewSearchBooks.setVisible(true);
        setActiveBtn(btnSideSearch); txtSearchBook.clear(); loadAllBooks();
    }

    public void showMyBooks() {
        hideAllViews(); lblPageTitle.setText("Borrowed Books");
        viewMyBooks.setManaged(true); viewMyBooks.setVisible(true);
        setActiveBtn(btnSideMyBooks); loadMyBooks();
    }

    public void showFines() {
        hideAllViews(); lblPageTitle.setText("Pay Fine");
        viewFines.setManaged(true); viewFines.setVisible(true);
        setActiveBtn(btnSideFines); lblFineStatus.setText(""); loadMyFines();
    }

    public void showAccount() {
        hideAllViews(); lblPageTitle.setText("Account Status");
        viewAccount.setManaged(true); viewAccount.setVisible(true);
        setActiveBtn(btnSideAccount); loadAccountInfo();
    }

    public void logout(ActionEvent event) {
        LoginController.loggedInUser = null;
        Stage stage = (Stage) lblPageTitle.getScene().getWindow();
        stage.setScene(new Scene(new LoginView()));
        stage.setMaximized(true);
        stage.setTitle("Library Management System - Login");
    }

    private void loadHomeStats() {
        if (LoginController.loggedInUser == null) {
			return;
		}
        int userId = LoginController.loggedInUser.getUserId();
        try (Connection conn = ConnectionUtil.getConnection()) {
            ResultSet rs;
            rs = conn.prepareStatement("SELECT COUNT(*) FROM books").executeQuery();
            if (rs.next()) {
				lblStatTotalBooks.setText(String.valueOf(rs.getInt(1)));
			}

            rs = conn.prepareStatement("SELECT COUNT(*) FROM issues WHERE user_id=" + userId + " AND status='ISSUED'").executeQuery();
            if (rs.next()) {
				lblStatBorrowed.setText(String.valueOf(rs.getInt(1)));
			}

            rs = conn.prepareStatement("SELECT COUNT(*) FROM issues WHERE user_id=" + userId + " AND status='ISSUED' AND return_date < CURDATE()").executeQuery();
            if (rs.next()) {
				lblStatPending.setText(String.valueOf(rs.getInt(1)));
			}

            rs = conn.prepareStatement("SELECT SUM(amount) FROM fines WHERE user_id=" + userId + " AND status='UNPAID'").executeQuery();
            if (rs.next()) {
                double total = rs.getDouble(1);
                lblStatFines.setText("₹" + String.format("%.0f", total));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadCharts() {
        if (LoginController.loggedInUser == null) {
			return;
		}
        int userId = LoginController.loggedInUser.getUserId();
        try (Connection conn = ConnectionUtil.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT status, COUNT(*) FROM issues WHERE user_id=" + userId + " GROUP BY status").executeQuery();
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            while (rs.next()) {
				pieData.add(new PieChart.Data(rs.getString(1), rs.getInt(2)));
			}
            chartBorrowStatus.setData(pieData);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            rs = conn.prepareStatement("SELECT title, available_copies FROM books LIMIT 5").executeQuery();
            while (rs.next()) {
				series.getData().add(new XYChart.Data<>(rs.getString(1), rs.getInt(2)));
			}
            chartLibraryOverview.getData().clear(); chartLibraryOverview.getData().add(series);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadNotifications() {
        ObservableList<NotificationModel> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionUtil.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT * FROM notifications ORDER BY created_at DESC LIMIT 5").executeQuery();
            while (rs.next()) {
				list.add(new NotificationModel(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
			}
            tblLatestNotifications.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadAllBooks() {
        ObservableList<BookModel> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionUtil.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT * FROM books").executeQuery();
            while (rs.next()) {
				list.add(new BookModel(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
                        rs.getString("publisher"), rs.getInt("publication_year"),
                        rs.getInt("total_copies"), rs.getInt("available_copies")));
			}
            tblSearchBooks.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void searchBooks() {
        String query = txtSearchBook.getText().trim();
        if (query.isEmpty()) { loadAllBooks(); return; }
        ObservableList<BookModel> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR publisher LIKE ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            String p = "%" + query + "%"; pst.setString(1, p); pst.setString(2, p); pst.setString(3, p);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
				list.add(new BookModel(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
                        rs.getString("publisher"), rs.getInt("publication_year"),
                        rs.getInt("total_copies"), rs.getInt("available_copies")));
			}
            tblSearchBooks.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadMyBooks() {
        if (LoginController.loggedInUser == null) {
			return;
		}
        ObservableList<IssueModel> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionUtil.getConnection()) {
            String sql = "SELECT i.issue_id, b.title, i.issue_date, i.return_date, i.status " +
                         "FROM issues i JOIN books b ON i.book_id=b.book_id WHERE i.user_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, LoginController.loggedInUser.getUserId());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                IssueModel im = new IssueModel(rs.getInt(1), 0, LoginController.loggedInUser.getUserId(), rs.getDate(3), rs.getDate(4), rs.getString(5));
                im.setBookTitle(rs.getString(2));
                list.add(im);
            }
            tblMyBooks.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadMyFines() {
        if (LoginController.loggedInUser == null) {
			return;
		}
        ObservableList<FineModel> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionUtil.getConnection()) {
            String sql = "SELECT f.fine_id, b.title, f.amount, f.fine_date, f.status " +
                         "FROM fines f JOIN issues i ON f.issue_id=i.issue_id JOIN books b ON i.book_id=b.book_id WHERE f.user_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, LoginController.loggedInUser.getUserId());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                FineModel fm = new FineModel(rs.getInt(1), 0, LoginController.loggedInUser.getUserId(), rs.getDouble(3), rs.getDate(4), rs.getString(5));
                fm.setBookTitle(rs.getString(2));
                list.add(fm);
            }
            tblFines.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void payFine() {
        FineModel sel = tblFines.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus(lblFineStatus, "Select a fine to pay.", false); return; }
        if (sel.getStatus().equalsIgnoreCase("PAID")) { setStatus(lblFineStatus, "Already paid!", true); return; }
        try (Connection conn = ConnectionUtil.getConnection()) {
            PreparedStatement pst = conn.prepareStatement("UPDATE fines SET status='PAID' WHERE fine_id=?");
            pst.setInt(1, sel.getFineId()); pst.executeUpdate();
            setStatus(lblFineStatus, "Fine paid successfully!", true);
            loadMyFines(); loadHomeStats();
        } catch (Exception e) { e.printStackTrace(); setStatus(lblFineStatus, "Payment error.", false); }
    }

    private void loadAccountInfo() {
        if (LoginController.loggedInUser == null) {
			return;
		}
        application.models.UserModel u = LoginController.loggedInUser;
        lblAccName.setText(u.getName()); lblAccEmail.setText(u.getEmail());
        lblAccPhone.setText(u.getPhone() != null ? u.getPhone() : "N/A");
        lblAccAddress.setText(u.getAddress()); lblAccRole.setText(u.getRole());
        lblAccJoined.setText("2024-01-15");
        ObservableList<NotificationModel> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionUtil.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT * FROM notifications ORDER BY created_at DESC LIMIT 10").executeQuery();
            while (rs.next()) {
				list.add(new NotificationModel(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
			}
            tblAccNotifications.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setStatus(Label lbl, String msg, boolean ok) {
        lbl.setText(msg);
        lbl.setStyle(ok ? "-fx-text-fill:#059669;" : "-fx-text-fill:#dc2626;");
    }
}
