package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton encargado de mantener una única conexión reutilizable.
 */
public final class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() {
        url = System.getenv().getOrDefault(
                "BIBLIOTECA_DB_URL",
                "jdbc:mysql://localhost:3306/biblioteca?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
        );
        user = System.getenv().getOrDefault("BIBLIOTECA_DB_USER", "root");
        password = System.getenv().getOrDefault("BIBLIOTECA_DB_PASSWORD", "");
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    public synchronized void closeConnection() {
        if (connection == null) {
            return;
        }

        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {
            // El cierre se intenta al terminar la aplicación.
        }
    }
}
