package pt.ist.cmu.ubibike.httpserver.db;

import pt.ist.cmu.ubibike.httpserver.model.PointsTransaction;
import pt.ist.cmu.ubibike.httpserver.model.Session;
import pt.ist.cmu.ubibike.httpserver.model.Trajectory;
import pt.ist.cmu.ubibike.httpserver.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBObjectSelector {



    public static User getUserFromID(Connection conn, int uid) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM users WHERE uid = " + uid);

        if(!result.next()){
            return null;
        }

        User u = new User(result.getInt("uid"), result.getString("username"), result.getString("public_key"), result.getBytes("password"));

        try{result.close(); stmt.close();}catch (SQLException e) {/*ignore*/}

        return  u;
    }

    public static User getUserFromUsername(Connection conn, String username) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
        stmt.setString(1, username);

        ResultSet result = stmt.executeQuery();

        if(!result.next()){ return null; }

        User u = new User(result.getInt("uid"), result.getString("username"), result.getString("public_key"), result.getBytes("password"));

        try{result.close(); stmt.close();}catch (SQLException e) {/*ignore*/}

        return  u;
    }


    public static Trajectory getTrajectoryFromID(Connection conn, int tid) throws SQLException{

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM trajectories WHERE tid = " + tid);

        if(!result.next()){ return null;}

        Trajectory t = new Trajectory(result.getInt("tid"), result.getInt("uid"), result.getInt("points_earned"), result.getString("coords_json"), result.getString("user_tid"), result.getTimestamp("ride_timestamp").getTime());

        try{result.close(); stmt.close();}catch (SQLException e) {/*ignore*/}

        return  t;

    }

    public static Trajectory getTrajectoryFromUserTID(Connection conn, String userTID) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM trajectories WHERE user_tid = " + userTID);

        if(!result.next()){ return null;}

        Trajectory t = new Trajectory(result.getInt("tid"), result.getInt("uid"), result.getInt("points_earned"), result.getString("coords_json"), result.getString("user_tid"), result.getTimestamp("ride_timestamp").getTime());

        try{result.close(); stmt.close();}catch (SQLException e) {/*ignore*/}

        return  t;

    }

    public static List<Trajectory> getTrajectoriesFromUser(Connection conn, int uid) throws SQLException {

        List<Trajectory> resultList = new ArrayList<Trajectory>();

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM trajectories WHERE uid = " + uid);

        while (result.next()){
            resultList.add(new Trajectory(result.getInt("tid"), result.getInt("uid"), result.getInt("points_earned"), result.getString("coords_json"), result.getString("user_tid"), result.getTimestamp("ride_timestamp").getTime()));
        }

        try{result.close(); stmt.close();}catch (SQLException e) {/*ignore*/}

        return resultList;
    }

    public static PointsTransaction getPointsTransactionFromID(Connection conn, int ptid) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM points_transactions WHERE ptid = " + ptid);

        if(!result.next()){ return null;}

        PointsTransaction pt = new PointsTransaction(result.getInt("ptid"),
                                                     result.getInt("sender_uid"),
                                                     result.getInt("receiver_uid"),
                                                     result.getInt("points"),
                                                     result.getTimestamp("execution_timestamp"));

        try{result.close(); stmt.close();}catch (SQLException e) {/*ignore*/}

        return pt;
    }

    public static List<PointsTransaction> getPointsTransactionFromUser(Connection conn, int uid) throws SQLException{

        List<PointsTransaction> resultList = new ArrayList<PointsTransaction>();

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM points_transactions WHERE sender_uid = " + uid + "OR receiver_uid = " + uid);

        while (result.next()){
            resultList.add( new PointsTransaction(result.getInt("ptid"),
                                result.getInt("sender_uid"),
                                result.getInt("receiver_uid"),
                                result.getInt("points"),
                                result.getTimestamp("execution_timestamp")));
        }

        try{result.close(); stmt.close();}catch (SQLException e) {/*ignore*/}

        return resultList;
    }

    public static Session getSessionFromUID(Connection conn, int uid) throws SQLException{

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM sessions WHERE uid = " + uid);

        if(!result.next()){ return null;}

        Session s = new Session(result.getInt("uid"), result.getInt("session_id"), result.getTimestamp("start_timestamp").getTime());

        try{result.close(); stmt.close();}catch (SQLException e) {/*ignore*/}

        return s;
    }

    public static Session getSessionFromSessionID(Connection conn, int sessionID) throws SQLException{

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM sessions WHERE session_id = " + sessionID);

        if(!result.next()){ return null;}

        Session s = new Session(result.getInt("uid"), result.getInt("session_id"), result.getTimestamp("start_timestamp").getTime());

        try{result.close(); stmt.close();}catch (SQLException e) {/*ignore*/}

        return s;
    }


}
