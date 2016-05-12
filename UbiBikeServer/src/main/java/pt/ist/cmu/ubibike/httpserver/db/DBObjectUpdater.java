package pt.ist.cmu.ubibike.httpserver.db;

import pt.ist.cmu.ubibike.httpserver.model.Booking;
import pt.ist.cmu.ubibike.httpserver.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by ASUS on 13/04/2016.
 */
public class DBObjectUpdater {
    public static void updateBooking(Connection conn, Booking b) throws SQLException{

        PreparedStatement stmt = conn.prepareStatement("UPDATE bookings SET bid = ?, uid = ?, active = ? WHERE booking_id = ?");

        stmt.setInt(1, b.getBid());
        stmt.setInt(2, b.getUid());
        stmt.setBoolean(3, b.isActive());
        stmt.setInt(4, b.getBookingID());

        stmt.executeUpdate();
    }

    public static void updateUser(Connection conn, User u) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE users SET username = ?, public_key = ?, password = ?, points = ?, logical_clock = ? WHERE uid = ?");

        stmt.setString(1, u.getUsername());
        stmt.setString(2, u.getPublicKey());
        stmt.setBytes(3, u.getPassword());
        stmt.setInt(4, u.getPoints());
        stmt.setInt(5, u.getLogicalClock());
        stmt.setInt(6, u.getUid());

        stmt.executeUpdate();
    }
}
