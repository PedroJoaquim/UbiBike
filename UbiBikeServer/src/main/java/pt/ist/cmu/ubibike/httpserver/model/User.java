package pt.ist.cmu.ubibike.httpserver.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by ASUS on 09/03/2016.
 */
public class User {

    private int uid;
    private String username;
    @JsonIgnore
    private String publicKey;
    @JsonIgnore
    private byte[] password;
    private Trajectory[] trajectories;
    private int points;
    private int globalRank;
    private int logicalClock;

    public User(int uid, String username, String public_key, byte[] password, int points, int logicalClock) {
        this.uid = uid;
        this.username = username;
        this.publicKey = public_key;
        this.password = password;
        this.globalRank = -1;
        this.points = points;
        this.logicalClock = logicalClock;
    }

    public User(int uid, String username, String public_key, byte[] password, Trajectory[] trajectories, int points, int logicalClock) {
        this.uid = uid;
        this.username = username;
        this.publicKey = public_key;
        this.password = password;
        this.trajectories = trajectories;
        this.globalRank = -1;
        this.points = points;
        this.logicalClock = logicalClock;
    }

    public User() {}

    @JsonGetter("uid")
    public int getUid() {
        return uid;
    }

    @JsonSetter("uid")
    public void setUid(int uid) {
        this.uid = uid;
    }

    @JsonGetter("username")
    public String getUsername() {
        return username;
    }

    @JsonSetter("username")
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    @JsonGetter("trajectories")
    public Trajectory[] getTrajectories() {
        return trajectories;
    }

    @JsonSetter("trajectories")
    public void setTrajectories(Trajectory[] trajectories) {
        this.trajectories = trajectories;
    }

    @JsonGetter("rank")
    public int getGlobalRank() {
        return globalRank;
    }

    @JsonSetter("rank")
    public void setGlobalRank(int globalRank) {
        this.globalRank = globalRank;
    }

    @JsonGetter("points")
    public int getPoints() {
        return points;
    }

    @JsonSetter("points")
    public void setPoints(int points) {
        this.points = points;
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
