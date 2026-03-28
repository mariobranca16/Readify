package data.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

    private static final String HOST = "readify.mysql.database.azure.com";
    private static final int PORT = 3306;
    private static final String DB = "readify";

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB +
                    "?useSSL=true" +
                    "&requireSSL=true" +
                    "&sslMode=REQUIRED" +
                    "&serverTimezone=UTC";

    private static final String USER = "MY_USER"; //da modificare
    private static final String PASSWORD = "MY_PASSWORD"; //da modificare

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL non trovato", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException("Errore durante l'operazione", e);
            }
        }
    }
}