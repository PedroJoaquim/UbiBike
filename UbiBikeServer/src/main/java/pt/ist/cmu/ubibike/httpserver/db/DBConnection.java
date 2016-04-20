package pt.ist.cmu.ubibike.httpserver.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String host = "db.ist.utl.pt";
    private static final String db = "ist175624";
    private static final String username = "ist175624";
    private static final String password = "teho1967";


    private static Connection conn;

    public static Connection getConnection() {

        try {
            if (conn == null || conn.isValid(0) || conn.isClosed()) {
                conn = loadConnection();
            }
        } catch (SQLException e) {
            conn = loadConnection();
        }

        return conn;
    }

    private static Connection loadConnection() {
        String host = "jdbc:mysql://" + DBConnection.host + ":3306/" + DBConnection.db + "?autoReconnect=true";

        try {
            return DriverManager.getConnection(host, DBConnection.username, DBConnection.password);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            System.exit(0);
        }

        return null;
    }
}
