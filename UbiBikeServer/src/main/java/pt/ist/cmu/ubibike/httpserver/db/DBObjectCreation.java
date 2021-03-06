package pt.ist.cmu.ubibike.httpserver.db;

import pt.ist.cmu.ubibike.httpserver.model.Coordinate;
import pt.ist.cmu.ubibike.httpserver.model.PendingEvent;
import pt.ist.cmu.ubibike.httpserver.model.PointsTransactionAllInfo;
import pt.ist.cmu.ubibike.httpserver.model.Trajectory;
import pt.ist.cmu.ubibike.httpserver.util.CoordinatesParser;

import java.sql.*;

public class DBObjectCreation {

    public static int insertUser(Connection conn, String username, String public_key, byte[] password) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users(username, public_key, password) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        int newId;

        stmt.setString(1, username);
        stmt.setString(2, public_key);
        stmt.setBytes(3, password);

        stmt.executeUpdate();

        ResultSet result = stmt.getGeneratedKeys();

        if(!result.next()){ throw new SQLException();}

        newId = result.getInt(1);

        try{result.close(); stmt.close();}catch (SQLException e) {/*ignore*/}


        return newId;
    }

    public static int insertTrajectory(Connection conn, Trajectory t) throws SQLException {

        int newId;
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO trajectories(uid, start_sid, end_sid, coords_text, points_earned, user_tid, distance, ride_start_timestamp, ride_end_timestamp)" +
                                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);


        stmt.setInt(1, t.getUid());
        stmt.setInt(2, t.getStartSid());
        stmt.setInt(3, t.getEndSid());
        stmt.setString(4, CoordinatesParser.toStoreFormat(t.getCoords()));
        stmt.setInt(5, t.getPointsEarned());
        stmt.setString(6, t.getUserTID());
        stmt.setFloat(7, t.getDistance());
        stmt.setLong(8, t.getRideStartTimestamp());
        stmt.setLong(9, t.getRideEndTimestamp());

        stmt.executeUpdate();

        ResultSet result = stmt.getGeneratedKeys();

        if(!result.next()){ throw new SQLException();}

        newId = result.getInt(1);

        try{result.close(); stmt.close();} catch (SQLException e) {/*ignore*/}

        return newId;
    }

    public static void insertPointsTransaction(Connection conn, PointsTransactionAllInfo pt) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO points_transactions(source_uid, source_logical_clock, target_uid, target_logical_clock, points, transaction_timestamp) " +
                                                       " VALUES (?, ?, ?, ?, ?, ?)");

        stmt.setString(1, pt.getSourceUsername());
        stmt.setInt(2, pt.getSourceLogialClock());
        stmt.setString(3, pt.getTargetUsername());
        stmt.setInt(4, pt.getTargetLogicalClock());
        stmt.setInt(5, pt.getPoints());
        stmt.setLong(6, pt.getTimestamp());

        stmt.executeUpdate();

        try{stmt.close();} catch (SQLException e) {/*ignore*/}

    }

    public static void insertPointsTransaction(Connection conn, PendingEvent pe) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO points_transactions(source_uid, source_logical_clock, target_uid, target_logical_clock, points, transaction_timestamp) " +
                                                       " VALUES (?, ?, ?, ?, ?, ?)");

        stmt.setString(1, pe.getSourceUsername());
        stmt.setInt(2, pe.getSourceLogicalClock());
        stmt.setString(3, pe.getTargetUsername());
        stmt.setInt(4, pe.getTargetLogicalClock());
        stmt.setInt(5, pe.getPoints());
        stmt.setLong(6, pe.getTransactionTimestamp());

        stmt.executeUpdate();

        try{stmt.close();} catch (SQLException e) {/*ignore*/}
    }

    public static void insertSession(Connection conn, int uid, int sessionID) throws SQLException{

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO sessions(uid, session_id) VALUES(?,?)");

        stmt.setInt(1, uid);
        stmt.setInt(2, sessionID);

        stmt.executeUpdate();

        try{stmt.close();} catch (SQLException e) {/*ignore*/}
    }

    public static void insertBikeBooking(Connection conn, int uid, int bid, int sid) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO bookings(bid, uid, source_sid) VALUES(?,?,?)");

        stmt.setInt(1, bid);
        stmt.setInt(2, uid);
        stmt.setInt(3, sid);

        stmt.executeUpdate();

        try{stmt.close();} catch (SQLException e) {/*ignore*/}
    }

    public static void addBikeToStation(Connection conn, int bid, int sid) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO bikes_stations(sid, bid) VALUES(?,?)");

        stmt.setInt(1, sid);
        stmt.setInt(2, bid);

        stmt.executeUpdate();

        try{stmt.close();} catch (SQLException e) {/*ignore*/}
    }

    public static void insertPendingEvent(Connection connection, PendingEvent pe) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement("INSERT INTO pending_events(source_uid, source_logical_clock, target_uid, target_logical_clock, points, transaction_timestamp, type)  " +
                                                             " VALUES(?, ?, ?, ?, ?, ?, ?)");

        stmt.setString(1, pe.getSourceUsername());
        stmt.setInt(2, pe.getSourceLogicalClock());
        stmt.setString(3, pe.getTargetUsername());
        stmt.setInt(4, pe.getTargetLogicalClock());
        stmt.setInt(5, pe.getPoints());
        stmt.setLong(6, pe.getTransactionTimestamp());
        stmt.setInt(7, pe.getType());

        stmt.executeUpdate();

        try{stmt.close();} catch (SQLException e) {/*ignore*/}
    }
}
