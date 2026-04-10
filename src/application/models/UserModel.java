package application.models;

public class UserModel {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String role; // "STUDENT" or "LIBRARIAN"
    private int specId; // Stores student_id or librarian_id

    public UserModel(int userId, String name, String email, String password, String phone, String address, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getRole() { return role; }
    public int getSpecId() { return specId; }

    public void setUserId(int userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setRole(String role) { this.role = role; }
    public void setSpecId(int specId) { this.specId = specId; }
}
