package pt.ist.cmu.ubibike.httpserver.Session.Tokens;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.joda.time.DateTime;
import pt.ist.cmu.ubibike.httpserver.model.User;


public class PublicKeyToken {

    private int uid;

    private String username;

    private String publicKey;

    private String validationTimestamp;

    public PublicKeyToken(User u) {
        this.uid = u.getUid();
        this.username = u.getUsername();
        this.publicKey = u.getPublicKey();
        this.validationTimestamp = generateTimestamp();
    }

    private String generateTimestamp() {
        DateTime dt = DateTime.now();
        DateTime dt1 = dt.plusDays(1);

        return Long.toString(dt1.getMillis());
    }

    @JsonGetter("uid")
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @JsonGetter("username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonGetter("publicKey")
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @JsonGetter("ttl")
    public String getValidationTimestamp() {
        return validationTimestamp;
    }

    public void setValidationTimestamp(String validationTimestamp) {
        this.validationTimestamp = validationTimestamp;
    }
}
