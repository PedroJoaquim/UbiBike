package pt.ist.cmu.ubibike.httpserver.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DBObjectRemove {

    public static void removeBikeFromStation(Connection conn, int bid) throws SQLException{
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM bikes_stations WHERE bid = " + bid);

    }

    public static void removeBookingFromUser(Connection conn, int uid) throws SQLException{
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM bookings WHERE uid = " + uid);
    }

    public static void removePendingEvent(Connection connection, int peID) {

    }
}
