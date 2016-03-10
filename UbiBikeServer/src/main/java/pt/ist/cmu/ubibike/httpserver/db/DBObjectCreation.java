package pt.ist.cmu.ubibike.httpserver.db;

import java.sql.*;

public class DBObjectCreation {

    public static int insertUser(Connection conn, String username, String email, String public_key, byte[] password) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users(username, email, public_key, password) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        int newId;

        stmt.setString(1, username);
        stmt.setString(2, email);
        stmt.setString(3, public_key);
        stmt.setBytes(4, password);

        stmt.executeUpdate();

        ResultSet result = stmt.getGeneratedKeys();

        if(!result.next()){ throw new SQLException();}

        newId = result.getInt(1);

        try{result.close(); stmt.close();}catch (SQLException e) {/*ignore*/}


        return newId;
    }

    public static int insertTrajectory(Connection conn, int uid, String coordsJSON, int pointsEarned, String userTID) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO trajectories(uid, coords_json, points_earned, user_tid) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        int newId;

        stmt.setInt(1, uid);
        stmt.setString(2, coordsJSON);
        stmt.setInt(3, pointsEarned);
        stmt.setString(4, userTID);

        stmt.executeUpdate();

        ResultSet result = stmt.getGeneratedKeys();

        if(!result.next()){ throw new SQLException();}

        newId = result.getInt(1);

        try{result.close(); stmt.close();} catch (SQLException e) {/*ignore*/}

        return newId;
    }

    public static int insertPointsTransaction(Connection conn, int senderUID, int receiverUID, int points) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO points_transactions(sender_uid, receiver_uid, points) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        int newId;

        stmt.setInt(1, senderUID);
        stmt.setInt(2, receiverUID);
        stmt.setInt(3, points);

        stmt.executeUpdate();

        ResultSet result = stmt.getGeneratedKeys();

        if(!result.next()){ throw new SQLException();}

        newId = result.getInt(1);

        try{result.close(); stmt.close();} catch (SQLException e) {/*ignore*/}

        return newId;
    }

}
