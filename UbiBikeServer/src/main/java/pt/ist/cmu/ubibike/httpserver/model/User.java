package pt.ist.cmu.ubibike.httpserver.model;

/**
 * Created by ASUS on 09/03/2016.
 */
public class User {

    private int uid;
    private String username;
    private String email;
    private String public_key;
    private byte[] password;

    public User(int uid, String username, String email, String public_key, byte[] password) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.public_key = public_key;
        this.password = password;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }
}
