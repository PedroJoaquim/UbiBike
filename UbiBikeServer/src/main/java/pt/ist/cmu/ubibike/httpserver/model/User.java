package pt.ist.cmu.ubibike.httpserver.model;

/**
 * Created by ASUS on 09/03/2016.
 */
public class User {

    private int uid;
    private String username;
    private String public_key;
    private byte[] password;

    public User(int uid, String username, String public_key, byte[] password) {
        this.uid = uid;
        this.username = username;
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
}
