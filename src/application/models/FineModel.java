package application.models;

import java.sql.Date;

public class FineModel {
    private int fineId;
    private int issueId;
    private int userId;
    private double amount;
    private Date fineDate;
    private String status; // "UNPAID" or "PAID"
    private String bookTitle; // For UI display

    public FineModel(int fineId, int issueId, int userId, double amount, Date fineDate, String status) {
        this.fineId = fineId;
        this.issueId = issueId;
        this.userId = userId;
        this.amount = amount;
        this.fineDate = fineDate;
        this.status = status;
    }

    public int getFineId() { return fineId; }
    public int getIssueId() { return issueId; }
    public int getUserId() { return userId; }
    public double getAmount() { return amount; }
    public Date getFineDate() { return fineDate; }
    public String getStatus() { return status; }
    public String getBookTitle() { return bookTitle; }

    public void setFineId(int fineId) { this.fineId = fineId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setFineDate(Date fineDate) { this.fineDate = fineDate; }
    public void setStatus(String status) { this.status = status; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
}
