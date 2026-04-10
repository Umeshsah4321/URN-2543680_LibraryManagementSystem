package application.models;

public class NotificationModel {
    private int notificationId;
    private String message;
    private String type;
    private String createdAt;

    public NotificationModel(int notificationId, String message, String type, String createdAt) {
        this.notificationId = notificationId;
        this.message = message;
        this.type = type;
        this.createdAt = createdAt;
    }

    public int getNotificationId() { return notificationId; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public String getCreatedAt() { return createdAt; }

    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }
    public void setMessage(String message) { this.message = message; }
    public void setType(String type) { this.type = type; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
