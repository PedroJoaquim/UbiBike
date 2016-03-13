package pt.ist.cmu.ubibike.httpserver.session;

import pt.ist.cmu.ubibike.httpserver.db.DBConnection;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectCreation;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectSelector;
import pt.ist.cmu.ubibike.httpserver.model.Session;
import pt.ist.cmu.ubibike.httpserver.model.User;
import pt.ist.cmu.ubibike.httpserver.session.tokens.SessionToken;
import pt.ist.cmu.ubibike.httpserver.session.tokens.TokenHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

public class SessionManager {

    /**
     *
     * @param u the user for whom we are creating the session
     * @return SessionToken
     *
     * @throws SQLException
     */
    public static String startSession(User u) throws SQLException {

        int uid = u.getUid();
        int sessionID;
        Connection connection = DBConnection.getConnection();
        Session s = DBObjectSelector.getSessionFromUID(connection, uid);

        if(s != null){
            sessionID = s.getSessionID();
        }
        else{
            sessionID = generateSessionID();
            DBObjectCreation.insertSession(connection, uid, sessionID);
        }

        return TokenHandler.generateSessionToken(sessionID);
    }

    public static User getUserFromSessionToken(String token) throws SQLException {

        SessionToken tokenObj = TokenHandler.readSessionToken(token);
        int sessionID = tokenObj.getSessionId();
        Connection conn = DBConnection.getConnection();
        Session s = DBObjectSelector.getSessionFromSessionID(conn, sessionID);

        if(s == null){
             throw new RuntimeException("Invalid Session Token");
        }

        return DBObjectSelector.getUserFromID(conn, s.getUid());
    }

    private static int generateSessionID(){
        Random rnd = new Random();
        return rnd.nextInt(999999999);
    }
}
