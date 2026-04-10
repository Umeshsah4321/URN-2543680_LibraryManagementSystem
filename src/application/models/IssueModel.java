package application.models;

import java.sql.Date;

public class IssueModel {
    private int issueId;
    private int bookId;
    private int userId;
    private Date issueDate;
    private Date returnDate;
    private String status;
    private String bookTitle; // For easier UI display

    public IssueModel(int issueId, int bookId, int userId, Date issueDate, Date returnDate, String status) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.userId = userId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public int getIssueId() { return issueId; }
    public int getBookId() { return bookId; }
    public int getUserId() { return userId; }
    public Date getIssueDate() { return issueDate; }
    public Date getReturnDate() { return returnDate; }
    public String getStatus() { return status; }
    public String getBookTitle() { return bookTitle; }

    public void setIssueId(int issueId) { this.issueId = issueId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
    public void setStatus(String status) { this.status = status; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
}
