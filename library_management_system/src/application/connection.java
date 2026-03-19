package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connection {

    public static Connection getConnection() {
        try {
            // Force driver loading — very helpful in many JavaFX / runtime classpath situations
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/library_db"
                    + "?useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=UTC"
                    + "&useUnicode=true"
                    + "&characterEncoding=UTF-8";

            
            String user = "root"; // change if needed
            String password = "Umesh1234@#@"; // your mysql password

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Database Connected");
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
}