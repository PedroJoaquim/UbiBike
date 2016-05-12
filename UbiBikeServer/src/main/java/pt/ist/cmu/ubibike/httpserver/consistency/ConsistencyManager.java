package pt.ist.cmu.ubibike.httpserver.consistency;

import pt.ist.cmu.ubibike.httpserver.db.*;
import pt.ist.cmu.ubibike.httpserver.model.PendingEvent;
import pt.ist.cmu.ubibike.httpserver.model.PointsTransactionAllInfo;
import pt.ist.cmu.ubibike.httpserver.model.Trajectory;
import pt.ist.cmu.ubibike.httpserver.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ASUS on 11/05/2016.
 */
public class ConsistencyManager {

    private static ConsistencyManager instance;
    private HashMap<String, Object> locks;

    public ConsistencyManager() {
        this.locks = new HashMap<String, Object>();
    }

    public static synchronized ConsistencyManager getInstance(){
        if(instance == null){
            instance = new ConsistencyManager();
        }

        return instance;
    }

    public void addNewTrajectory(User u, Trajectory t) throws SQLException {
        synchronized (getLockForUser(u.getUsername())){

            DBObjectCreation.insertTrajectory(DBConnection.getConnection(), t);
            u.addPoints(t.getPointsEarned());

            if(u.getLogicalClock() +1 == t.getLogicalClock()){
                u.incLogicalClock();
                checkPendingEvents(u);
            }
            else{
                DBObjectCreation.insertPendingEvent(DBConnection.getConnection(), new PendingEvent(-1, u.getUsername(), t.getLogicalClock(), "", -1, t.getPointsEarned(), 0, PendingEvent.TRAJECTORY_TYPE));
                DBObjectUpdater.updateUser(DBConnection.getConnection(), u);
            }
        }
    }


    public boolean addNewPointsTransaction(PointsTransactionAllInfo pt) throws SQLException {

        synchronized (getLockForUser(pt.getSourceUsername())){
            synchronized (getLockForUser(pt.getTargetUsername())){

                User sourceUser = DBObjectSelector.getUserFromUsername(DBConnection.getConnection(), pt.getSourceUsername());
                User targetUser = DBObjectSelector.getUserFromUsername(DBConnection.getConnection(), pt.getTargetUsername());


                if(actionAlreadyPerformed(pt)){
                    return false;
                }

                if((sourceUser.getLogicalClock() +1 == pt.getSourceLogialClock()) &&
                        (targetUser.getLogicalClock() +1 == pt.getTargetLogicalClock())){


                    sourceUser.incLogicalClock();
                    targetUser.incLogicalClock();

                    sourceUser.removePoints(pt.getPoints());
                    targetUser.addPoints(pt.getPoints());

                    DBObjectCreation.insertPointsTransaction(DBConnection.getConnection(), pt);

                    checkPendingEvents(sourceUser);
                    checkPendingEvents(targetUser);
                } else{

                    PendingEvent pe = pt.toPendingEvent();
                    DBObjectCreation.insertPendingEvent(DBConnection.getConnection(), pe);
                }
            }
        }

        return true;
    }


    //do not consider the same request twice
    private boolean actionAlreadyPerformed(PointsTransactionAllInfo pt) throws SQLException {

        if(DBObjectSelector.getEquivalentPointsTransaction(DBConnection.getConnection(), pt) != null){
            return true;
        }

        if(DBObjectSelector.getEquivalentPendingEvents(DBConnection.getConnection(), pt) != null){
            return true;
        }

        return false;
    }


    private void checkPendingEvents(User u) throws SQLException {

        List<User> usersToProcess = new ArrayList<User>();
        List<PendingEvent> pendingEventList = DBObjectSelector.getPendingEventsForUser(DBConnection.getConnection(), u.getUid());
        final String username = u.getUsername();

        pendingEventList.sort(new Comparator<PendingEvent>() {
            public int compare(PendingEvent p1, PendingEvent p2) {
                Integer p1LogicalClock = p1.getLogicalClockForUid(username);
                Integer p2LogicalClock = p2.getLogicalClockForUid(username);

                return p1LogicalClock.compareTo(p2LogicalClock);
            }
        });


        for (int i = 0; i < pendingEventList.size(); i++) {
            PendingEvent pendingEvent = pendingEventList.get(i);

            if(pendingEvent.getLogicalClockForUid(username) == u.getLogicalClock() +1){
                User otherUser = executePendingEvent(u, pendingEvent);
                if(otherUser != null) usersToProcess.add(otherUser);
            }
            else{
                break;
            }
        }

        for (User u2: usersToProcess) {
            Object lock = getLockForUser(u2.getUsername());

            synchronized (lock){
                checkPendingEvents(u2);
            }
        }

        DBObjectUpdater.updateUser(DBConnection.getConnection(), u);
    }

    private User executePendingEvent(User u, PendingEvent pendingEvent) throws SQLException {

        if(pendingEvent.getType() == PendingEvent.TRAJECTORY_TYPE){
            u.incLogicalClock();
            DBObjectRemove.removePendingEvent(DBConnection.getConnection(), pendingEvent.getPeID());
            return null;
        }


        String otherUsername = (pendingEvent.getSourceUsername().equals(u.getUsername()) ? pendingEvent.getTargetUsername() :
                                                                                   pendingEvent.getSourceUsername());

        User otherUser = DBObjectSelector.getUserFromUsername(DBConnection.getConnection(), otherUsername);

        Object lock2 = getLockForUser(otherUsername);

        synchronized (lock2){
            //check if other user logical clock correct
            if(otherUser.getLogicalClock() == pendingEvent.getLogicalClockForUid(otherUsername) -1){
                otherUser.incLogicalClock();
                u.incLogicalClock();

                //user is sending the points
                if(u.getUsername().equals(pendingEvent.getSourceUsername())){
                    u.removePoints(pendingEvent.getPoints());
                    otherUser.addPoints(pendingEvent.getPoints());
                } else{
                    u.addPoints(pendingEvent.getPoints());
                    otherUser.removePoints(pendingEvent.getPoints());
                }

                DBObjectCreation.insertPointsTransaction(DBConnection.getConnection(), pendingEvent);
                DBObjectRemove.removePendingEvent(DBConnection.getConnection(), pendingEvent.getPeID());
                return otherUser;
            }

            return null;
        }
    }


    /*
     * Gets Locks for users
     */
    private synchronized Object getLockForUser(String username){

        if(!this.locks.containsKey(username)){
            this.locks.put(username, new Object());
        }

        return this.locks.get(username);
    }
}
