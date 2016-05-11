package pt.ist.cmu.ubibike.httpserver.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Trajectory {

    private int tid;
    private int uid;
    private int startSid;
    private int endSid;
    private int pointsEarned;
    private Coordinate[] coords;
    private long rideStartTimestamp;
    private long rideEndTimestamp;
    private float distance;
    private String userTID;
    private int logicalClock;

    public Trajectory(int tid, int startSid, int endSid, int uid, int pointsEarned, Coordinate[] coords, long rideStartTimestamp, long rideEndTimestamp, float distance, String userTID) {
        this.tid = tid;
        this.startSid = startSid;
        this.endSid = endSid;
        this.uid = uid;
        this.pointsEarned = pointsEarned;
        this.coords = coords;
        this.rideStartTimestamp = rideStartTimestamp;
        this.rideEndTimestamp = rideEndTimestamp;
        this.distance = distance;
        this.userTID = userTID;
    }

    public Trajectory() { }

    @JsonGetter("tid")
    public int getTid() {
        return tid;
    }

    @JsonSetter("tid")
    public void setTid(int tid) {
        this.tid = tid;
    }

    @JsonGetter("uid")
    public int getUid() {
        return uid;
    }

    @JsonSetter("uid")
    public void setUid(int uid) {
        this.uid = uid;
    }

    @JsonGetter("points_earned")
    public int getPointsEarned() {
        return pointsEarned;
    }

    @JsonSetter("points_earned")
    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    @JsonGetter("coords")
    public Coordinate[] getCoords() {
        return coords;
    }

    @JsonSetter("coords")
    public void setCoords(Coordinate[] coords) {
        this.coords = coords;
    }

    @JsonGetter("start_timestamp")
    public long getRideStartTimestamp() {
        return rideStartTimestamp;
    }

    @JsonSetter("start_timestamp")
    public void setRideStartTimestamp(long rideStartTimestamp) {
        this.rideStartTimestamp = rideStartTimestamp;
    }

    @JsonGetter("end_timestamp")
    public long getRideEndTimestamp() {
        return rideEndTimestamp;
    }

    @JsonSetter("end_timestamp")
    public void setRideEndTimestamp(long rideEndTimestamp) {
        this.rideEndTimestamp = rideEndTimestamp;
    }

    @JsonGetter("distance")
    public float getDistance() {
        return distance;
    }

    @JsonSetter("distance")
    public void setDistance(float distance) {
        this.distance = distance;
    }

    @JsonGetter("user_tid")
    public String getUserTID() {
        return userTID;
    }

    @JsonSetter("user_tid")
    public void setUserTID(String userTID) {
        this.userTID = userTID;
    }

    @JsonGetter("start_sid")
    public int getStartSid() {
        return startSid;
    }

    @JsonSetter("start_sid")
    public void setStartSid(int startSid) {
        this.startSid = startSid;
    }

    @JsonGetter("end_sid")
    public int getEndSid() {
        return endSid;
    }

    @JsonSetter("end_sid")
    public void setEndSid(int endSid) {
        this.endSid = endSid;
    }

    @JsonGetter("logical_clock")
    public int getLogicalClock() {
        return logicalClock;
    }

    @JsonSetter("logical_clock")
    public void setLogicalClock(int logicalClock) {
        this.logicalClock = logicalClock;
    }
}
