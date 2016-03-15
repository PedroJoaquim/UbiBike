package pt.ist.cmu.ubibike.httpserver.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by ASUS on 09/03/2016.
 */
public class User {

    private int uid;
    private String username;
    private String public_key;
    private byte[] password;
    private Trajectory[] trajectories;

    public User(int uid, String username, String public_key, byte[] password) {
        this.uid = uid;
        this.username = username;
        this.public_key = public_key;
        this.password = password;
    }

    public User(int uid, String username, String public_key, byte[] password, Trajectory[] trajectories) {
        this.uid = uid;
        this.username = username;
        this.public_key = public_key;
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
        return public_key;
    }

    public void setPublicKey(String publicKey) {
        this.public_key = publicKey;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
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
