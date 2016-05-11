package pt.ist.cmu.ubibike.httpserver.consistency;

import pt.ist.cmu.ubibike.httpserver.db.DBConnection;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectCreation;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectUpdater;
import pt.ist.cmu.ubibike.httpserver.model.PendingEvent;
import pt.ist.cmu.ubibike.httpserver.model.Trajectory;
import pt.ist.cmu.ubibike.httpserver.model.User;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by ASUS on 11/05/2016.
 */
public class ConsistencyManager {

    private static ConsistencyManager instance;
    private HashMap<Integer, Object> locks;

    public ConsistencyManager() {
        this.locks = new HashMap<Integer, Object>();
    }

    public static synchronized ConsistencyManager getInstance(){
        if(instance == null){
            instance = new ConsistencyManager();
        }

        return instance;
    }

    public void addNewTrajectory(User u, Trajectory t) throws SQLException {
        synchronized (getLockForUser(u.getUid())){

            DBObjectCreation.insertTrajectory(DBConnection.getConnection(), t);
            u.incPoints(t.getPointsEarned());

            if(u.getLogicalClock() +1 == t.getLogicalClock()){
                u.incLogicalClock();
                checkPendingEvents(u);
            }
            else{
                DBObjectCreation.insertPendingEvent(DBConnection.getConnection(), new PendingEvent(u.getUid(), t.getLogicalClock(), -1, -1, t.getPointsEarned(), PendingEvent.TRAJECTORY_TYPE));
            }

            DBObjectUpdater.updateUser(DBConnection.getConnection(), u);
        }
    }

    private void checkPendingEvents(User u) {



    }


    /*
     * Gets Locks for users
     */
    private synchronized Object getLockForUser(int uid){

        if(!this.locks.containsKey(uid)){
            this.locks.put(uid, new Object());
        }

        return this.locks.get(uid);
    }

    /*
     * Updates the user logical clock
     */

    private void updateUserLogicalClock(User u) throws SQLException {
        u.incLogicalClock();
        DBObjectUpdater.updateUser(DBConnection.getConnection(), u);
    }


}
