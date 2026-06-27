package lee_tsayeg_rotem_boltanski.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton JDBC connection manager for zoo_db.
 *
 * SETUP:
 *   1. Install MySQL 8+ and create the database:
 *        source sql/schema.sql
 *        source sql/data.sql
 *        source sql/triggers.sql
 *   2. Update DB_USER and DB_PASSWORD below.
 *   3. Add the MySQL JDBC driver JAR to your project classpath:
 *        https://dev.mysql.com/downloads/connector/j/
 */
public class DBConnection {

    private static final String DB_URL  =
            "jdbc:mysql://localhost:3306/zoo_db" +
            "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "";   // ← update before running

    private static Connection instance;

    private DBConnection() {}

    /**
     * Returns a shared, lazily-created Connection.
     * Re-opens the connection automatically if it was closed.
     */
    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("[DB] Connected to zoo_db.");
        }
        return instance;
    }

    /** Closes the shared connection (call on program exit). */
    public static void close() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }

    /** Returns true if a connection can be established (used for fallback). */
    public static boolean isAvailable() {
        try {
            getConnection();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
