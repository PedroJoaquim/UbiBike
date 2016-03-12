package pt.ist.cmu.ubibike.httpserver.session.tokens;

import com.fasterxml.jackson.annotation.*;
import org.codehaus.jackson.annotate.JsonIgnore;
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

    public PublicKeyToken(){
        //for json object mapper only
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

    @JsonGetter("public_key")
    public String getPublicKey() {
        return publicKey;
    }

    @JsonSetter("public_key")
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @JsonGetter("ttl")
    public String getValidationTimestamp() {
        return validationTimestamp;
    }

    @JsonSetter("ttl")
    public void setValidationTimestamp(String validationTimestamp) {
        this.validationTimestamp = validationTimestamp;
    }
}
