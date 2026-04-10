package application.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {

    private static boolean isInitialized = false;
    
    public static Connection getConnection() {
        try {
            // Force driver loading — very helpful in many JavaFX / runtime classpath situations
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/um"
                    + "?useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=UTC"
                    + "&useUnicode=true"
                    + "&characterEncoding=UTF-8";

            String user = "root"; // change if needed
            String password = "Umesh1234@#@"; // your mysql password

            Connection conn = DriverManager.getConnection(url, user, password);
            
            if (!isInitialized) {
                initializeDatabase(conn);
                isInitialized = true;
            }
            
            return conn;

        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC Driver JAR not found in classpath!");
            e.printStackTrace();
            return null;

        } catch (SQLException e) {
            System.err.println("ERROR: Connection failed → " + e.getMessage());
            e.printStackTrace();
            return null;

        } catch (Exception e) {
            System.err.println("Unexpected error during connection:");
            e.printStackTrace();
            return null;
        }
    }

    private static void initializeDatabase(Connection conn) {
        try (java.sql.Statement stmt = conn.createStatement()) {
            // Create Users Table
            stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "user_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(255) NOT NULL, "
                    + "email VARCHAR(255) UNIQUE NOT NULL, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "phone VARCHAR(50), "
                    + "address VARCHAR(255), "
                    + "role ENUM('STUDENT', 'LIBRARIAN') NOT NULL)");

            // Create Students Table
            stmt.execute("CREATE TABLE IF NOT EXISTS students ("
                    + "student_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "user_id INT UNIQUE NOT NULL, "
                    + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)");

            // Create Librarians Table
            stmt.execute("CREATE TABLE IF NOT EXISTS librarians ("
                    + "librarian_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "user_id INT UNIQUE NOT NULL, "
                    + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)");

            // Create Books Table
            stmt.execute("CREATE TABLE IF NOT EXISTS books ("
                    + "book_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "title VARCHAR(255) NOT NULL, "
                    + "author VARCHAR(255), "
                    + "publisher VARCHAR(255), "
                    + "publication_year INT, "
                    + "total_copies INT DEFAULT 0, "
                    + "available_copies INT DEFAULT 0)");

            // Create Issues Table
            stmt.execute("CREATE TABLE IF NOT EXISTS issues ("
                    + "issue_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "book_id INT NOT NULL, "
                    + "user_id INT NOT NULL, "
                    + "issue_date DATE, "
                    + "return_date DATE, "
                    + "status ENUM('ISSUED', 'RETURNED') DEFAULT 'ISSUED', "
                    + "FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)");

            // Create Fines Table
            stmt.execute("CREATE TABLE IF NOT EXISTS fines ("
                    + "fine_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "issue_id INT NOT NULL, "
                    + "user_id INT NOT NULL, "
                    + "amount DECIMAL(10, 2) NOT NULL, "
                    + "fine_date DATE, "
                    + "status ENUM('UNPAID', 'PAID') DEFAULT 'UNPAID', "
                    + "FOREIGN KEY (issue_id) REFERENCES issues(issue_id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)");

            // Create Notifications Table
            stmt.execute("CREATE TABLE IF NOT EXISTS notifications ("
                    + "notification_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "message TEXT NOT NULL, "
                    + "type ENUM('GENERAL', 'OVERDUE', 'DUE_REMINDER') DEFAULT 'GENERAL', "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            System.out.println("Database Initialized Successfully");

        } catch (SQLException e) {
            System.err.println("ERROR: Database initialization failed!");
            e.printStackTrace();
        }
    }
}
