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

        User u = new User(result.getInt("uid"), result.getString("username"), result.getString("public_key"), result.getBytes("password"));

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

        User u = new User(result.getInt("uid"), result.getString("username"), result.getString("public_key"), result.getBytes("password"));

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

        Trajectory t = new Trajectory(result.getInt("tid"), result.getInt("uid"), result.getInt("points_earned"), CoordinatesParser.fromStoreFormat(result.getString("coords_text")),
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

        Trajectory t = new Trajectory(result.getInt("tid"), result.getInt("uid"), result.getInt("points_earned"), CoordinatesParser.fromStoreFormat(result.getString("coords_text")),
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
            resultList.add(new Trajectory(result.getInt("tid"), result.getInt("uid"), result.getInt("points_earned"), CoordinatesParser.fromStoreFormat(result.getString("coords_text")),
                                           result.getLong("ride_start_timestamp"), result.getLong("ride_end_timestamp"), result.getFloat("distance"), result.getString("user_tid")));
        }

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return resultList.toArray(new Trajectory[resultList.size()]);
    }

    public static PointsTransaction getPointsTransactionFromID(Connection conn, int ptid) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM points_transactions WHERE ptid = " + ptid);

        if (!result.next()) {
            return null;
        }

        PointsTransaction pt = new PointsTransaction(result.getInt("ptid"),
                result.getInt("sender_uid"),
                result.getInt("receiver_uid"),
                result.getInt("points"),
                result.getTimestamp("execution_timestamp"));

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return pt;
    }

    public static PointsTransaction[] getPointsTransactionFromUser(Connection conn, int uid) throws SQLException {

        List<PointsTransaction> resultList = new ArrayList<PointsTransaction>();

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM points_transactions WHERE sender_uid = " + uid + "OR receiver_uid = " + uid);

        while (result.next()) {
            resultList.add(new PointsTransaction(result.getInt("ptid"),
                    result.getInt("sender_uid"),
                    result.getInt("receiver_uid"),
                    result.getInt("points"),
                    result.getTimestamp("execution_timestamp")));
        }

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return resultList.toArray(new PointsTransaction[resultList.size()]);
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

    public static Bike[] getAvailableBikesFromStation(Connection conn, int sid) throws SQLException {

        List<Bike> resultList = new ArrayList<Bike>();

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT bid FROM stations AS s, bikes_stations AS bs WHERE s.sid = " + sid + " AND bs.sid = s.sid AND bs.bid NOT IN (SELECT bid FROM bookings)");

        while (result.next()) {
            resultList.add(new Bike(result.getInt("bid")));
        }

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return  resultList.toArray(new Bike[resultList.size()]);
    }

    public static Booking[] getBookingsFromUID(Connection conn, int uid) throws SQLException {

        List<Booking> resultList = new ArrayList<Booking>();

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM bookings WHERE uid = " + uid);

        while (result.next()){
            resultList.add(new Booking(result.getInt("uid"), result.getInt("bid"), result.getLong("booking_timestamp"), result.getBoolean("active")));
        }

        try {
            result.close();
            stmt.close();
        } catch (SQLException e) {/*ignore*/}

        return resultList.toArray(new Booking[resultList.size()]);
    }
}
