package application.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {

    // Database configuration
    private static final String URL = "jdbc:mysql://localhost:3306/library_db"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASSWORD = "Umesh1234@#@";

    // Method to get connection
    public static Connection getConnection() {

        try {
            // Load driver (safe for JavaFX / Eclipse)
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("✅ Database Connected Successfully!");
            return conn;

        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL Driver Not Found!");
            e.printStackTrace();

        } catch (SQLException e) {
            System.out.println("❌ Database Connection Failed!");
            e.printStackTrace();
        }

        return null;
    }

    // Optional: test directly from this class
    public static void main(String[] args) {
        Connection conn = getConnection();

        if (conn != null) {
            System.out.println("🎉 Connection Working!");
        } else {
            System.out.println("💥 Connection Failed!");
        }
    }
}