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

    public User(int uid, String username, String public_key, byte[] password) {
        this.uid = uid;
        this.username = username;
        this.publicKey = public_key;
        this.password = password;
    }

    public User(int uid, String username, String public_key, byte[] password, Trajectory[] trajectories) {
        this.uid = uid;
        this.username = username;
        this.publicKey = public_key;
        this.password = password;
        this.trajectories = trajectories;
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
}
