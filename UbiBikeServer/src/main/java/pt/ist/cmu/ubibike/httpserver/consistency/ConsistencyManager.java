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
            u.addPoints(t.getPointsEarned());

            if(u.getLogicalClock() +1 == t.getLogicalClock()){
                u.incLogicalClock();
                checkPendingEvents(u);
            }
            else{
                DBObjectCreation.insertPendingEvent(DBConnection.getConnection(), new PendingEvent(-1, u.getUid(), t.getLogicalClock(), -1, -1, t.getPointsEarned(), 0, PendingEvent.TRAJECTORY_TYPE));
                DBObjectUpdater.updateUser(DBConnection.getConnection(), u);
            }
        }
    }


    public boolean addNewPointsTransaction(PointsTransactionAllInfo pt) throws SQLException {

        synchronized (getLockForUser(pt.getSourceUid())){
            synchronized (getLockForUser(pt.getTargetUid())){

                User sourceUser = DBObjectSelector.getUserFromID(DBConnection.getConnection(), pt.getSourceUid());
                User targetUser = DBObjectSelector.getUserFromID(DBConnection.getConnection(), pt.getTargetUid());


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
        final int uid = u.getUid();

        pendingEventList.sort(new Comparator<PendingEvent>() {
            public int compare(PendingEvent p1, PendingEvent p2) {
                Integer p1LogicalClock = p1.getLogicalClockForUid(uid);
                Integer p2LogicalClock = p2.getLogicalClockForUid(uid);

                return p1LogicalClock.compareTo(p2LogicalClock);
            }
        });


        for (int i = 0; i < pendingEventList.size(); i++) {
            PendingEvent pendingEvent = pendingEventList.get(i);

            if(pendingEvent.getLogicalClockForUid(uid) == u.getLogicalClock() +1){
                User otherUser = executePendingEvent(u, pendingEvent);
                if(otherUser != null) usersToProcess.add(otherUser);
            }
            else{
                break;
            }
        }

        for (User u2: usersToProcess) {
            Object lock = getLockForUser(u2.getUid());

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


        int otherUid = (pendingEvent.getSourceUID() == u.getUid() ? pendingEvent.getTargetUID() :
                                                                     pendingEvent.getSourceUID());

        User otherUser = DBObjectSelector.getUserFromID(DBConnection.getConnection(), otherUid);

        Object lock2 = getLockForUser(otherUser.getUid());

        synchronized (lock2){
            //check if other user logical clock correct
            if(otherUser.getLogicalClock() == pendingEvent.getLogicalClockForUid(otherUid) -1){
                otherUser.incLogicalClock();
                u.incLogicalClock();

                //user is sending the points
                if(u.getUid() == pendingEvent.getSourceUID()){
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
    private synchronized Object getLockForUser(int uid){

        if(!this.locks.containsKey(uid)){
            this.locks.put(uid, new Object());
        }

        return this.locks.get(uid);
    }
}
