package pt.ist.cmu.ubibike.httpserver.model;

import java.util.Date;

public class Trajectory {

    private int tid;
    private int uid;
    private int pointsEarned;
    private String coordsJSON;
    private String userTID;
    private Date rideTimestamp;

    public Trajectory(int tid, int uid, int pointsEarned, String coordsJSON, String userTID, Date rideTimestamp) {
        this.tid = tid;
        this.uid = uid;
        this.pointsEarned = pointsEarned;
        this.coordsJSON = coordsJSON;
        this.userTID = userTID;
        this.rideTimestamp = rideTimestamp;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public String getCoordsJSON() {
        return coordsJSON;
    }

    public void setCoordsJSON(String coordsJSON) {
        this.coordsJSON = coordsJSON;
    }

    public Date getRideTimestamp() {
        return rideTimestamp;
    }

    public void setRideTimestamp(Date rideTimestamp) {
        this.rideTimestamp = rideTimestamp;
    }

    public String getUserTID() {
        return userTID;
    }

    public void setUserTID(String userTID) {
        this.userTID = userTID;
    }
}
