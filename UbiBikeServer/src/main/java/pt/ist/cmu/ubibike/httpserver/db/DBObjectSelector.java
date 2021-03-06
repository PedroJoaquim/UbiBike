package pt.ist.cmu.ubibike.httpserver.db;

import pt.ist.cmu.ubibike.httpserver.model.*;
import pt.ist.cmu.ubibike.httpserver.util.CoordinatesParser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBObjectSelector {


    public static User getUserFromID(Connection conn, int uid) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM users WHERE uid = " + uid);

        if (!result.next()) {
            return null;
        }

        User u = new User(result.getInt("uid"), result.getString("username"), result.getString("public_key"), result.getBytes("password"), result.getInt("points"), result.getInt("logical_clock"));

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return u;
    }

    public static User getUserFromUsername(Connection conn, String username) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
        stmt.setString(1, username);

        ResultSet result = stmt.executeQuery();

        if (!result.next()) {
            return null;
        }

        User u = new User(result.getInt("uid"), result.getString("username"), result.getString("public_key"), result.getBytes("password"), result.getInt("points"), result.getInt("logical_clock"));

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return u;
    }


    public static Trajectory getTrajectoryFromID(Connection conn, int tid) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM trajectories WHERE tid = " + tid);

        if (!result.next()) {
            return null;
        }

        Trajectory t = new Trajectory(result.getInt("tid"), result.getInt("start_sid"), result.getInt("end_sid"), result.getInt("uid"), result.getInt("points_earned"),
                CoordinatesParser.fromStoreFormat(result.getString("coords_text")),
                result.getLong("ride_start_timestamp"), result.getLong("ride_end_timestamp"), result.getFloat("distance"), result.getString("user_tid"));

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return t;

    }

    public static Trajectory getTrajectoryFromUserTID(Connection conn, String userTID) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM trajectories WHERE user_tid = " + userTID);

        if (!result.next()) {
            return null;
        }

        Trajectory t = new Trajectory(result.getInt("tid"), result.getInt("start_sid"), result.getInt("end_sid"), result.getInt("uid"), result.getInt("points_earned"),
                CoordinatesParser.fromStoreFormat(result.getString("coords_text")),
                result.getLong("ride_start_timestamp"), result.getLong("ride_end_timestamp"), result.getFloat("distance"), result.getString("user_tid"));

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return t;

    }

    public static Trajectory[] getTrajectoriesFromUser(Connection conn, int uid) throws SQLException {

        List<Trajectory> resultList = new ArrayList<Trajectory>();

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM trajectories WHERE uid = " + uid);

        while (result.next()) {
            resultList.add(new Trajectory(result.getInt("tid"), result.getInt("start_sid"), result.getInt("end_sid"), result.getInt("uid"), result.getInt("points_earned"),
                    CoordinatesParser.fromStoreFormat(result.getString("coords_text")),
                    result.getLong("ride_start_timestamp"), result.getLong("ride_end_timestamp"), result.getFloat("distance"), result.getString("user_tid")));
        }

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return resultList.toArray(new Trajectory[resultList.size()]);
    }


    public static List<PointsTransactionBaseInfo> getPointsTransactionFromUser(Connection conn, int uid) throws SQLException {

        List<PointsTransactionBaseInfo> resultList = new ArrayList<PointsTransactionBaseInfo>();

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM points_transactions WHERE source_uid = "+ uid+" OR target_uid = " + uid);

        while (result.next()) {
            resultList.add(new PointsTransactionBaseInfo(result.getString("source_uid"), result.getString("target_uid"), result.getInt("points"), result.getLong("transaction_timestamp")));
        }

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {}

        return resultList;
    }

    public static Session getSessionFromUID(Connection conn, int uid) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM sessions WHERE uid = " + uid);

        if (!result.next()) {
            return null;
        }

        Session s = new Session(result.getInt("uid"), result.getInt("session_id"), result.getTimestamp("start_timestamp").getTime());

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return s;
    }

    public static Session getSessionFromSessionID(Connection conn, int sessionID) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM sessions WHERE session_id = " + sessionID);

        if (!result.next()) {
            return null;
        }

        Session s = new Session(result.getInt("uid"), result.getInt("session_id"), result.getTimestamp("start_timestamp").getTime());

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return s;
    }


    public static Station[] getAllStations(Connection conn) throws SQLException {

        List<Station> resultList = new ArrayList<Station>();

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM stations");

        while (result.next()) {
            Bike[] bikes = getAvailableBikesFromStation(conn, result.getInt("sid"));
            resultList.add(new Station(result.getInt("sid"), result.getString("station_name"), result.getDouble("lat"), result.getDouble("lng"), bikes));
        }

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}


        return resultList.toArray(new Station[resultList.size()]);
    }

    public static Bike getBikeFromBid(Connection conn, int bid, int sid) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM bikes WHERE bid = " + bid);

        if (!result.next()) {
            return null;
        }

        Bike b = new Bike(result.getInt("bid"), result.getString("bike_addr"), sid);

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return b;
    }


    public static Bike[] getAvailableBikesFromStation(Connection conn, int sid) throws SQLException {

        List<Bike> resultList = new ArrayList<Bike>();

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT b.bid, b.bike_addr FROM stations AS s, bikes_stations AS bs, bikes As b WHERE s.sid = " + sid + " AND bs.sid = s.sid AND b.bid = bs.bid AND bs.bid NOT IN (SELECT bid FROM bookings)");

        while (result.next()) {
            resultList.add(new Bike(result.getInt("bid"), result.getString("bike_addr"), sid));
        }

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return  resultList.toArray(new Bike[resultList.size()]);
    }

    public static List<Bike> getBikesFromStation(Connection conn, int sid) throws SQLException {

        List<Bike> resultList = new ArrayList<Bike>();

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT b.bid, b.bike_addr FROM stations AS s, bikes_stations AS bs, bikes AS b WHERE s.sid = " + sid + " AND bs.sid = s.sid AND b.bid = bs.bid");

        while (result.next()) {
            resultList.add(new Bike(result.getInt("bid"), result.getString("bike_addr"), sid));
        }

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return  resultList;
    }

    public static Booking getBookingFromUID(Connection conn, int uid) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM bookings WHERE uid = " + uid);

        if (!result.next()) {

            try {
                result.close();
                stmt.close();
            } catch (SQLException e) {/*ignore*/}

            return null;
        }

        Booking b = new Booking(result.getInt("booking_id"), result.getInt("uid"), result.getInt("bid"), result.getInt("source_sid"), result.getLong("booking_timestamp"), result.getBoolean("active"));

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return b;
    }

    public static int calcUserGlobalRank(Connection conn, int uid) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT uid, points FROM users ORDER BY points DESC");
        int pos = 1;

        while (result.next()) {
            if(uid == result.getInt("uid")){
                return pos;
            }
            pos++;
        }

        return -1;
    }

    public static List<PendingEvent> getPendingEventsForUser(Connection conn, int uid) throws SQLException {
        List<PendingEvent> resultList = new ArrayList<PendingEvent>();

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM pending_events WHERE source_uid = " + uid + " OR target_uid = " + uid);

        while (result.next()) {
            resultList.add(new PendingEvent(result.getInt("pe_id"), result.getString("source_uid"), result.getInt("source_logical_clock"),
                                            result.getString("target_uid"), result.getInt("target_logical_clock"),
                                            result.getInt("points"), result.getLong("transaction_timestamp"),
                                            result.getInt("type")));
        }


        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return resultList;
    }

    public static PointsTransactionBaseInfo getEquivalentPointsTransaction(Connection connection, PointsTransactionAllInfo pt) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM points_transactions WHERE source_uid = ? AND target_uid = ? AND transaction_timestamp = ?");
        stmt.setString(1, pt.getSourceUsername());
        stmt.setString(2, pt.getTargetUsername());
        stmt.setLong(3, pt.getTimestamp());

        ResultSet result = stmt.executeQuery();

        if (!result.next()) {
            return null;
        }

        PointsTransactionBaseInfo ptBase = new PointsTransactionBaseInfo(result.getString("source_uid"), result.getString("target_uid"), result.getInt("points"), result.getLong("transaction_timestamp"));

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {}

        return ptBase;
    }

    public static PendingEvent getEquivalentPendingEvents(Connection connection, PointsTransactionAllInfo pt) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM pending_events WHERE source_uid = ? AND target_uid = ? AND transaction_timestamp = ?");
        stmt.setString(1, pt.getSourceUsername());
        stmt.setString(2, pt.getTargetUsername());
        stmt.setLong(3, pt.getTimestamp());

        ResultSet result = stmt.executeQuery();

        if (!result.next()) {
            return null;
        }

        PendingEvent pe = new PendingEvent(result.getInt("pe_id"), result.getString("source_uid"), result.getInt("source_logical_clock"),
                result.getString("target_uid"), result.getInt("target_logical_clock"),
                result.getInt("points"), result.getLong("transaction_timestamp"),
                result.getInt("type"));

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {}

        return pe;
    }
}
