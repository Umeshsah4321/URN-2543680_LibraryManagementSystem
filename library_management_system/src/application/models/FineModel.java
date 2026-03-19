package application.models;

public class FineModel {
    private int fineId;
    private int issueId;
    private int userId;
    private double amount;
    private String status; // "UNPAID" or "PAID"

    public FineModel(int fineId, int issueId, int userId, double amount, String status) {
        this.fineId = fineId;
        this.issueId = issueId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
    }

    public int getFineId() { return fineId; }
    public int getIssueId() { return issueId; }
    public int getUserId() { return userId; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }

    public void setFineId(int fineId) { this.fineId = fineId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
}
